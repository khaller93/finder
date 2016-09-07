package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigSchema;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.StatementCollector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides the utilities for testing the local triple store.
 *
 * @author Kevin Haller
 */
public abstract class TripleStoreTest {

    private static final File TEST_DIR = new File("./db-test");
    private static TripleStoreManager tripleStoreManager;

    @BeforeClass
    public static void setUpClass()
        throws IOException, TripleStoreManagerException, URISyntaxException, RDFParseException,
        RDFHandlerException {
        if (!TEST_DIR.exists()) {
            if (!TEST_DIR.mkdir()) {
                throw new IOException(String.format("Test directory '%s' could not be created.",
                    TEST_DIR.getAbsolutePath()));
            }
        }
        Model storeConfigurationModel = new TreeModel();
        try (InputStream configIn = TripleStoreManager.class
            .getResourceAsStream(getTripleStoreTestConfigurationFilePath())) {
            if (configIn == null) {
                throw new IOException(String
                    .format("Configuration file at '%s' cannot be accessed.",
                        getTripleStoreTestConfigurationFilePath()));
            }
            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
            rdfParser.setRDFHandler(new StatementCollector(storeConfigurationModel));
            rdfParser.parse(configIn, RepositoryConfigSchema.NAMESPACE);
        }
        TripleStoreManager.changeTripleStoreConfigurationFile(storeConfigurationModel);
        tripleStoreManager = TripleStoreManager.getInstance(TEST_DIR.getAbsolutePath());
    }

    @Before
    public void setUp() throws RepositoryException {
        RepositoryConnection connection = getConnection();
        connection.begin();
        for (Map.Entry<String, String> testDataEntry : getTestData().entrySet()) {
            try (InputStream inputStream = TripleStoreTest.class.getClassLoader()
                .getResourceAsStream(testDataEntry.getValue())) {
                connection.add(inputStream, "http://finder.test.tuwien.ac.at/", RDFFormat.TURTLE,
                    new URIImpl(testDataEntry.getKey()));
            } catch (Exception e) {
                connection.rollback();
                break;
            }
        }
        connection.commit();
    }

    @After
    public void tearDown() throws RepositoryException {
        RepositoryConnection connection = getConnection();
        connection.remove((Resource) null, (URI) null, (Resource) null);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        FileUtils.deleteDirectory(TEST_DIR);
    }

    /**
     * Gets the {@link RepositoryConnection} to the triple store instance for testing.
     *
     * @return the {@link RepositoryConnection} to the triple store instance for testing.
     * @throws RepositoryException if no connection to the triple store can be established.
     */
    protected RepositoryConnection getConnection() throws RepositoryException {
        return tripleStoreManager.getConnection();
    }

    /**
     * Gets the test data that shall be added to the triple store. The kex represents the graph name
     * and the value the path to the RDF file (TTL syntax).
     *
     * @return the test data that shall be added to the triple store.
     */
    protected Map<String, String> getTestData() {
        Map<String, String> testDataMap = new LinkedHashMap<>();
        testDataMap.put("http://finder.test.tuwien.ac.at/spatial", "spatialDemo.ttl");
        testDataMap.put("http://finder.test.tuwien.ac.at/location", "locationDemo.ttl");
        return testDataMap;
    }

    /**
     * Gets the configuration file of the triple store that shall be used for testing.
     *
     * @return the configuration file of the triple store that shall be used for testing.
     */
    protected static String getTripleStoreTestConfigurationFilePath() {
        return "/config/test-store-config.ttl";
    }

}
