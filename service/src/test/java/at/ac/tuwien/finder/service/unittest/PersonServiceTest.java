package at.ac.tuwien.finder.service.unittest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.organizational.PersonDto;
import at.ac.tuwien.finder.dto.spatial.RoomDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.TestTripleStore;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ResourceNotFoundException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.service.organizational.person.PersonServiceFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Scanner;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * This class tests {@link PersonServiceFactory}.
 *
 * @author Kevin Haller
 */
public class PersonServiceTest {

    private static IRI BASE = TripleStoreManager.BASE;
    private static IResourceIdentifier BASE_IRI;

    public PersonServiceTest() throws URISyntaxException {
        BASE_IRI = new IResourceIdentifier(BASE.stringValue());
    }

    @Rule
    public TestTripleStore testTripleStore = new TestTripleStore();

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactory(testTripleStore.getTripleStoreManager());
    }

    //Steffen,Amsel,SteffenAmsel@einrot.com,0676 659 72 54,male

    @Test
    public void getPersonWithId_ok()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        Dto responseDto =
            serviceFactory.getService(getPathScanner("/organizational/person/id/OID17043309"))
                .execute();
        assertThat("The returned DTO must be an instance of PersonDto.", responseDto,
            instanceOf(PersonDto.class));
        PersonDto personDto = (PersonDto) responseDto;
        assertThat("The family name of the returned person must be 'Amsel'.",
            personDto.getFamilyName(), is("Amsel"));
        assertThat("The given name of the returned person must be 'Steffen'.",
            personDto.getGivenName(), is("Steffen"));
        assertThat("The gender of the returned person must be female.", personDto.getGender(),
            is("male"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPersonWithUnknownId_ok()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        serviceFactory.getService(getPathScanner("/organizational/person/id/OIDINVALID")).execute();
    }

    @Test
    public void getPersonWithIdAndAssignedRoom_ok()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        Dto responseDto =
            serviceFactory.getService(getPathScanner("/organizational/person/id/OID754841"))
                .execute();
        assertThat("The returned DTO must be an instance of PersonDto.", responseDto,
            instanceOf(PersonDto.class));
        PersonDto personDto = (PersonDto) responseDto;
        assertThat(personDto.getIRI().rawIRI(),
            is(BASE_IRI.resolve("organizational/person/id/OID754841").rawIRI()));
        assertThat("The family name of the returned person must be 'Hartmann'.",
            personDto.getFamilyName(), is("Hartmann"));
        assertThat("The given name of the returned person must be 'Steffen'.",
            personDto.getGivenName(), is("Steffen"));
        assertThat("The gender of the returned person must be male.", personDto.getGender(),
            is("male"));
        Rio.write(responseDto.getModel(), System.out, RDFFormat.TURTLE);
        RoomDto roomDto = personDto.getRoom();
        assertNotNull(
            "The returned person is located in the room with the room code 'HG0111'. Therefore it must not be null.",
            roomDto);
        assertThat("The returned person is located in the room with the room code 'HG0111'.",
            roomDto.getRoomCode(), is("HG0111"));
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }
}
