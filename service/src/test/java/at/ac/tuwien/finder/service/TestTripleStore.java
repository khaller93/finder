package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.unittest.SpatialServicesTest;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.ExternalResource;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is a test triple store that contains a dump of the DB.
 *
 * @author Kevin Haller
 */
public class TestTripleStore extends ExternalResource {

    private static Model dbTestData;

    static {
        try {
            dbTestData = Rio.parse(
                SpatialServicesTest.class.getClassLoader().getResourceAsStream("dbTestDump.trig"),
                "", RDFFormat.TRIG);
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
        when(tripleStoreManager.getConnection()).thenReturn(repository.getConnection());
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
        if(repository != null) {
            repository.shutDown();
        }
    }
}
