package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.datamanagement.catalog.DataCatalog;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * This class represents a triple store manager that manages the access to the local triple store.
 * <p>
 * This class represents a triple store manager that manages details about the triple store, the
 * structure of the data (graphs) and access to it.
 *
 * @author Kevin Haller
 */
public class TripleStoreManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(TripleStoreManager.class);

    public static final IRI BASE;
    private static final String GRAPH_DB_NAME = "finder-repo";

    private static TripleStoreManager tripleStoreManager;
    private static Semaphore tripleStoreReference = new Semaphore(-1);
    private static URL DEFAULT_TRIPLE_STORE_URL;
    private static URL baseURL;

    static {
        Properties dataManagementProperties = new Properties();
        try (InputStream propertiesStream = TripleStoreManager.class.getClassLoader()
            .getResourceAsStream("config/datamanagement.properties")) {
            dataManagementProperties.load(propertiesStream);
            try {
                DEFAULT_TRIPLE_STORE_URL = new URL(dataManagementProperties.getProperty("db.url"));
            } catch (MalformedURLException e) {
                logger.error("The default location url {} of the triple store is malformed. {}",
                    dataManagementProperties.getProperty("db.url"), e);
            }
        } catch (IOException e) {
            logger.error("The property file for data-manegement cannot be accessed. {}", e);
            System.exit(1);
        }
        BASE = SimpleValueFactory.getInstance()
            .createIRI(dataManagementProperties.getProperty("base.iri"));
    }

    private RepositoryManager repositoryManager;
    private Repository repository;
    private DataCatalog dataCatalog;

    /**
     * Gets an instance of the {@link TripleStoreManager} and increments the reference counter. If
     * the reference counter is 0, a new instance will be created, otherwise the already existing
     * triple store instance will be returned. The location of the triple store will be the
     * {@code DEFAULT_TRIPLE_STORE_URL} read in from the data management properties file.
     *
     * @return an instance of the {@link TripleStoreManager}.
     * @throws TripleStoreManagerException if no instance can be created.
     */
    public synchronized static TripleStoreManager getInstance() {
        try {
            return getInstance(DEFAULT_TRIPLE_STORE_URL);
        } catch (TripleStoreManagerException e) {
            return null;
        }
    }

    /**
     * Gets an instance of the {@link TripleStoreManager} and increments the reference counter. If
     * the reference counter is 0, a new instance will be created, otherwise the already existing
     * triple store instance will be returned. The location of the triple store will be at the given
     * URL.
     *
     * @param dbURL {@link URL} where the triple store is located.
     * @return an instance of the {@link TripleStoreManager}.
     * @throws TripleStoreManagerException if no instance can be created.
     */
    public synchronized static TripleStoreManager getInstance(URL dbURL)
        throws TripleStoreManagerException {
        if (baseURL == null || dbURL.equals(baseURL)) {
            tripleStoreReference.release();
            logger.debug("New instance of triple store manager requested. Total references: {} ",
                tripleStoreReference.availablePermits() + 1);
            if (tripleStoreManager == null) {
                try {
                    RepositoryManager repositoryManager =
                        new RemoteRepositoryManager(dbURL.toString());
                    repositoryManager.initialize();
                    tripleStoreManager = new TripleStoreManager(repositoryManager);
                    baseURL = dbURL;
                    logger.debug("The triple store manager {} has been initialized.",
                        tripleStoreManager);
                } catch (TripleStoreManagerException | RepositoryException e) {
                    logger.debug("The triple store manager cannot be established. {}", e);
                }
            }
            return tripleStoreManager;
        } else {
            throw new TripleStoreManagerException(String.format(
                "A triple store manager already exists for '%s'. No one for '%s' can be created",
                baseURL, dbURL));
        }
    }

    /**
     * Creates a new instance of {@link TripleStoreManager}. The configuration file for the
     * {@link Repository} must be stored at {@code STORE_ASSEMBLY_FILE} and must be valid,
     * otherwise a {@link TripleStoreManagerException} will be thrown.
     */
    private TripleStoreManager(RepositoryManager repositoryManager)
        throws TripleStoreManagerException {
        try {
            if (repositoryManager.hasRepositoryConfig(GRAPH_DB_NAME)) {
                repository = repositoryManager.getRepository(GRAPH_DB_NAME);
            } else {
                logger.error(String
                    .format("The repository with the name %s cannot be located at %s",
                        GRAPH_DB_NAME, baseURL));
                System.err.println(String
                    .format("The repository with the name %s cannot be located at %s",
                        GRAPH_DB_NAME, baseURL));
                System.exit(1);
            }
        } catch (RepositoryException | RepositoryConfigException e) {
            throw new TripleStoreManagerException(e);
        }
        this.repositoryManager = repositoryManager;
        this.dataCatalog = new DataCatalog(this);
    }

    /**
     * Gets the {@link DataCatalog} holding information about managed {@link DataSet}.
     *
     * @return {@link DataCatalog} holding information about managed {@link DataSet}.
     */
    public synchronized DataCatalog getDataCatalog() {
        return this.dataCatalog;
    }

    /**
     * Creates a new {@link RepositoryConnection} to the triple store that is managed by this
     * {@link TripleStoreManager}.
     *
     * @return new {@link RepositoryConnection} to the triple store managed by this
     * {@link TripleStoreManager}.
     * @throws RepositoryException if no connection cannot created for the managed
     *                             {@link Repository}.
     */
    public synchronized RepositoryConnection getConnection() throws RepositoryException {
        logger.debug("Connection from {} requested.", this);
        return repository.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void close() throws Exception {
        close(false);
    }

    /**
     * Cleans up all the variables specific for the given instance of {@link TripleStoreManager}.
     */
    private void cleanUp() {
        tripleStoreReference = new Semaphore(-1);
        tripleStoreManager = null;
        baseURL = null;
    }

    /**
     * Closes this {@link TripleStoreManager}, if the reference counter is 0 or if the given force
     * value is true; then the reference counter will be ignored.
     *
     * @param force true, if the reference counter shall be ignored.
     * @throws RepositoryException if the {@link Repository} managed by this
     *                             {@link TripleStoreManager} cannot be closed.
     */
    public synchronized void close(boolean force) throws RepositoryException {
        logger.debug("Shutdown of triple store manager requested. Total references: {} ",
            tripleStoreReference.availablePermits() + 1);
        if (tripleStoreReference.tryAcquire() && !force) {
            return;
        }
        if (repository != null) {
            repository.shutDown();
        }
        repositoryManager.shutDown();
        cleanUp();
        logger.debug("The triple store manager {} has been closed.", this);
    }
}
