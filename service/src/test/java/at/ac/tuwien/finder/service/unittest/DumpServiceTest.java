package at.ac.tuwien.finder.service.unittest;

import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.TestTripleStore;
import at.ac.tuwien.finder.service.dump.factory.DumpServiceFactory;
import at.ac.tuwien.finder.service.dump.service.DumpService;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

/**
 * This class tests the {@link DumpServiceFactory} and {@link DumpService}.
 *
 * @author Kevin Haller
 */
public class DumpServiceTest {

    @Rule
    public TestTripleStore testTripleStore = new TestTripleStore();

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactory(testTripleStore.getTripleStoreManager());
    }

    @Test
    public void getsRDFDump_ok() throws IRIUnknownException, IRIInvalidException, ServiceException {
        Dto dump = serviceFactory.getService(getPathScanner("dump")).execute();
        assertThat(dump.getModel().subjects(), hasItems(Iterations.stream(
            testTripleStore.getTripleStoreManager().getConnection().getStatements(null, null, null))
            .map(Statement::getSubject).collect(Collectors.toSet()).toArray(new Resource[0])));
    }

    @Test(expected = IRIInvalidException.class)
    public void getsRDFDump_throwsIRIInvalidException()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        serviceFactory.getService(getPathScanner("dump/na")).execute();
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
