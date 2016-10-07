package at.ac.tuwien.finder.service.unittest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.ResourceCollectionDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests search services.
 *
 * @author Kevin Haller
 */
public class SearchServicesTest {

    private static IRI BASE = TripleStoreManager.BASE;
    private static IResourceIdentifier BASE_IRI;

    private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    public SearchServicesTest() throws URISyntaxException {
        BASE_IRI = new IResourceIdentifier(BASE.stringValue());
    }

    /* Test data for the triple store */
    private static Model dbTestData;

    @BeforeClass
    public static void setUpClass() throws IOException {
        dbTestData = Rio.parse(
            SpatialServicesTest.class.getClassLoader().getResourceAsStream("dbTestDump.trig"), "",
            RDFFormat.TRIG);
    }

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        TripleStoreManager tripleStoreManager = mock(TripleStoreManager.class);
        Repository repository = new SailRepository(new MemoryStore());
        repository.initialize();
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(dbTestData);
        }
        when(tripleStoreManager.getConnection()).thenReturn(repository.getConnection());
        serviceFactory = new ServiceFactory(tripleStoreManager);
    }

    @Test
    public void searchFreeRoomsForTimeRange_ok()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        Dto freeRoomsDto = serviceFactory.getService(getPathScanner(
            "/search/freerooms/startDate/2016-10-06T13:00:00+01:00/endDate/2016-10-06T14:00:00+01:00"))
            .execute();
        assertThat("The returned Dto must be a collection of room resources.", freeRoomsDto,
            instanceOf(ResourceCollectionDto.class));
        ResourceCollectionDto freeRoomsCollectionDto = (ResourceCollectionDto) freeRoomsDto;
        assertThat(
            "The returned rooms must not contain 'Semninarraum Gödel', which is not free at the given time.",
            freeRoomsCollectionDto.asList(),
            not(contains(valueFactory.createIRI(BASE_IRI.rawIRI(), "spatial/room/id/HBEG10"))));
        assertThat(
            "The returned rooms must not contain 'Seminarraum Gödel', which is not free at the given time.",
            freeRoomsCollectionDto.asList(),
            not(contains(valueFactory.createIRI(BASE_IRI.rawIRI(), "spatial/room/id/HBEG10"))));
        assertThat(
            "The returned rooms must not contain 'Informatiklabor Pong', which is free at the given time.",
            freeRoomsCollectionDto.asList(),
            not(contains(valueFactory.createIRI(BASE_IRI.rawIRI(), "spatial/room/id/HGEG05"))));
    }

    @Test(expected = IRIInvalidException.class)
    public void searchFreeRoomsForTimeRange_WrongDateFormat_throwsIRIInvalidException()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        serviceFactory.getService(getPathScanner(
            "/search/freerooms/startDate/2016100613001400/endDate/2016-10-06T14:00:00+01:00"))
            .execute();
    }

    @Test(expected = IRIInvalidException.class)
    public void searchFreeRoomsForTimeRange_MissingRequiredStartDate_throwsIRIInvalidException()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        serviceFactory.getService(getPathScanner(
            "/search/freerooms/endDate/2016-10-06T14:00:00+01:00"))
            .execute();
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
