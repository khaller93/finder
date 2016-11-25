package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.unittest.SpatialServicesTest;
import org.apache.commons.csv.CSVFormat;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.rules.ExternalResource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is a test triple store that contains a dump of the DB.
 *
 * @author Kevin Haller
 */
public class TestTripleStore extends ExternalResource {

    private static final String TEST_DUMP_PATH = "dbTestDump.trig";

    private static Model dbTestData;

    static {
        try {
            dbTestData = Rio.parse(
                SpatialServicesTest.class.getClassLoader().getResourceAsStream(TEST_DUMP_PATH), "",
                RDFFormat.TRIG);
        } catch (IOException e) {
            throw new IllegalArgumentException("The test dumb cannot be accessed.", e);
        }
    }




    private TripleStoreManager tripleStoreManager;
    private Repository repository;

    @Override
    public void before() {
        tripleStoreManager = mock(TripleStoreManager.class);
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(dbTestData);
        }
        when(tripleStoreManager.getConnection()).thenAnswer(new Answer<RepositoryConnection>() {
            @Override
            public synchronized RepositoryConnection answer(InvocationOnMock invocation)
                throws Throwable {
                return repository.getConnection();
            }
        });
    }

    /**
     * Gets the test {@link TripleStoreManager}.
     *
     * @return the test {@link TripleStoreManager}.
     */
    public TripleStoreManager getTripleStoreManager() {
        return tripleStoreManager;
    }

    @Override
    public void after() {
        if (repository != null) {
            repository.shutDown();
        }
    }
}
