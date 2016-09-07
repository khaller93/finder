package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryConfigSchema;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
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

    public static final URI BASE_NAMED_GRAPH;
    public static final URI SPATIAL_NAMED_GRAPH;
    private static final String STORE_CONF_PATH = "/config/sesame-assembler.ttl";

    private static TripleStoreManager tripleStoreManager;
    private static Semaphore tripleStoreReference = new Semaphore(-1);
    private static String baseDirectory;

    private static Model storeConfigurationModel;

    static {
        Properties dataManagementProperties = new Properties();
        try (InputStream propertiesStream = TripleStoreManager.class.getClassLoader()
            .getResourceAsStream("config/datamanagement.properties")) {
            dataManagementProperties.load(propertiesStream);
        } catch (IOException e) {
            logger.error("The property file for data-manegement cannot be accessed. {}", e);
            System.exit(1);
        }
        BASE_NAMED_GRAPH = new URIImpl(dataManagementProperties.getProperty("base.iri"));
        SPATIAL_NAMED_GRAPH = new URIImpl(BASE_NAMED_GRAPH.toString() + "spatial");
        storeConfigurationModel = new TreeModel();
        try (InputStream configIn = TripleStoreManager.class.getResourceAsStream(STORE_CONF_PATH)) {
            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
            rdfParser.setRDFHandler(new StatementCollector(storeConfigurationModel));
            rdfParser.parse(configIn, RepositoryConfigSchema.NAMESPACE);
        } catch (IOException | RDFHandlerException | RDFParseException e) {
            logger.error("The configuration file for triple store cannot be accessed. {}", e);
        }
    }

    private RepositoryManager repositoryManager;
    private Repository repository;

    /**
     * Gets an instance of the {@link TripleStoreManager} and increments the reference counter. If
     * the reference counter is 0, a new instance will be created, otherwise the already existing
     * triple store instance will be returned. The location of the triple store will be the current
     * working directory.
     *
     * @return an instance of the {@link TripleStoreManager}.
     * @throws TripleStoreManagerException if no instance can be created.
     */
    public synchronized static TripleStoreManager getInstance() throws TripleStoreManagerException {
        return getInstance(".");
    }

    /**
     * Gets an instance of the {@link TripleStoreManager} and increments the reference counter. If
     * the reference counter is 0, a new instance will be created, otherwise the already existing
     * triple store instance will be returned. The location of the triple store will be at the given
     * base directory.
     *
     * @param baseDir the directory where the triple store shall be located.
     * @return an instance of the {@link TripleStoreManager}.
     * @throws TripleStoreManagerException if no instance can be created.
     */
    public synchronized static TripleStoreManager getInstance(String baseDir)
        throws TripleStoreManagerException {
        if (baseDirectory == null || baseDir.equals(baseDirectory)) {
            tripleStoreReference.release();
            if (tripleStoreManager == null) {
                try {
                    RepositoryManager repositoryManager =
                        new LocalRepositoryManager(new File(baseDir));
                    repositoryManager.initialize();
                    tripleStoreManager = new TripleStoreManager(repositoryManager);
                    baseDirectory = baseDir;
                    logger
                        .debug("The triple store manager {} has been started.", tripleStoreManager);
                } catch (TripleStoreManagerException | RepositoryException e) {
                    logger.debug("The triple store manager cannot be established. {}", e);
                }
            }
            return tripleStoreManager;
        } else {
            throw new TripleStoreManagerException(String.format(
                "A triple store manager already exists at '%s'. No one at '%s' can be created",
                new File(baseDirectory).getAbsolutePath(), baseDir));
        }
    }

    /**
     * Creates a new instance of {@link TripleStoreManager}. The configuration file for the
     * {@link Repository} must be stored at {@code STORE_ASSEMBLY_FILE} and must be valid,
     * otherwise a {@link TripleStoreManagerException} will be thrown.
     */
    private TripleStoreManager(RepositoryManager repositoryManager)
        throws TripleStoreManagerException {
        this.repositoryManager = repositoryManager;
        try {
            repository = repositoryManager.hasRepositoryConfig("graphdb-repo") ?
                repositoryManager.getRepository("graphdb-repo") :
                initializeRepository();
        } catch (RepositoryException | RepositoryConfigException e) {
            throw new TripleStoreManagerException(e);
        }
    }

    /**
     * Initializes the repository by loading the configuration given by
     * {@code GRAPHDB_ASSEMBLY_FILE}.
     *
     * @return the newly initialized repository.
     * @throws TripleStoreManagerException if the initialization of the repository is not possible.
     */
    private Repository initializeRepository() throws TripleStoreManagerException {
        Optional<Resource> repositoryNodeOptionalSubject =
            storeConfigurationModel.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY)
                .subjects().stream().findFirst();
        if (repositoryNodeOptionalSubject.isPresent()) {
            Resource repositoryNode = repositoryNodeOptionalSubject.get();
            try {
                RepositoryConfig repositoryConfig =
                    RepositoryConfig.create(storeConfigurationModel, repositoryNode);
                repositoryManager.addRepositoryConfig(repositoryConfig);
                return repositoryManager.getRepository("graphdb-repo");
            } catch (RepositoryException | RepositoryConfigException e) {
                throw new TripleStoreManagerException(e);
            }
        } else {
            throw new TripleStoreManagerException(String
                .format("The configuration file at %s for accessing the triple store is not valid.",
                    STORE_CONF_PATH));
        }
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
    public RepositoryConnection getConnection() throws RepositoryException {
        return repository.getConnection();
    }

    /**
     * Changes the configuration file of the triple store to the given one.
     */
    public static void changeTripleStoreConfigurationFile(Model storeConfigurationModel) {
        TripleStoreManager.storeConfigurationModel = storeConfigurationModel;
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
        baseDirectory = null;
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
        if (tripleStoreReference.tryAcquire() && !force) {
            return;
        }
        if (repository != null)
            repository.shutDown();
        repositoryManager.shutDown();
        cleanUp();
        logger.debug("The triple store manager {} has been closed.", this);
    }
}
