package at.ac.tuwien.finder.service.unittest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.ResourceCollectionDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.spatial.BuildingDto;
import at.ac.tuwien.finder.dto.spatial.FloorDto;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.TestTripleStore;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ResourceNotFoundException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.LOCN;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * THis class shall test the service structure concerning spatial data.
 *
 * @author Kevin Haller
 */
public class SpatialServicesTest {

    private static IRI BASE = TripleStoreManager.BASE;
    private static IResourceIdentifier BASE_IRI;

    private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    public SpatialServicesTest() throws URISyntaxException {
        BASE_IRI = new IResourceIdentifier(BASE.stringValue());
    }

    @Rule
    public TestTripleStore testTripleStore = new TestTripleStore();

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        serviceFactory = new ServiceFactory(testTripleStore.getTripleStoreManager());
    }

    @Test
    public void getBuildingWithId_ok()
        throws ServiceException, IRIUnknownException, IRIInvalidException {
        IRI buildingA = valueFactory.createIRI(BASE.stringValue(), "spatial/building/id/A");
        Dto responseDto =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/building/id/A"), null)
                .execute();
        assertNotNull("The response must not be null.", responseDto);
        assertThat("The response must be an instance of BuildingDto", responseDto,
            instanceOf(BuildingDto.class));
        BuildingDto buildingDto = (BuildingDto) responseDto;
        assertThat("The spatial identifier of the building must be the building code 'A'",
            buildingDto.getSpatialIdentifier(), is("A"));
        //Check for the model
        Model responseModel = responseDto.getModel();
        assertTrue(String.format("Resource <%s> must be part of the result.", buildingA.toString()),
            responseModel.subjects().contains(buildingA));
        assertThat(String.format(
            "Resource <%s> has two rdfs:label objects (Central building@en, Hauptgebäude@de).",
            buildingA.stringValue()),
            responseModel.filter(buildingA, RDFS.LABEL, null).objects().stream()
                .map(Value::stringValue).collect(Collectors.toList()),
            containsInAnyOrder("Central building", "Hauptgebäude"));
    }

    @Test
    public void getGeometryOfBuilding_ok() throws Exception {
        Dto resultDto =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/building/id/H"), null)
                .execute();
        assertThat("The result of requesting a building resource must be a BuildingDto.", resultDto,
            instanceOf(BuildingDto.class));
        BuildingDto buildingHDto = (BuildingDto) resultDto;
        assertThat(
            "The label of the building with id H must be 'Building of the informatics institute'.",
            buildingHDto.getLabel(), is("Building of the informatic institute"));
        assertThat("There must be at least a location point.", buildingHDto.getGeometryShapes(),
            hasSize(greaterThan(0)));
        assertThat(buildingHDto.getLocationPoint().getWkt(),
            is("POINT(16.3701504 48.19490159999999)"));
    }

    @Test
    @Ignore
    public void getRoomsOfBuildingWithID_H_ok() throws Exception {
        Dto responseDto =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/building/id/H"), null)
                .execute();
        assertNotNull("The response must not be null.", responseDto);
        assertThat("The response must be an instance of BuildingDto", responseDto,
            instanceOf(BuildingDto.class));
        BuildingDto buildingDto = (BuildingDto) responseDto;
        assertThat(buildingDto.getRooms().stream().map(roomDto -> roomDto.getIRI().rawIRI())
                .collect(Collectors.toList()),
            hasItems("http://finder.tuwien.ac.at/spatial/room/id/HBEG02",
                "http://finder.tuwien.ac.at/spatial/room/id/HA0318"));
    }

    @Test
    public void getFloorsOfBuildingWithID_H_ok() throws Exception {
        Dto responseDto =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/building/id/H"), null)
                .execute();
        assertNotNull("The response must not be null.", responseDto);
        assertThat("The response must be an instance of BuildingDto", responseDto,
            instanceOf(BuildingDto.class));
        BuildingDto buildingDto = (BuildingDto) responseDto;
        assertThat(buildingDto.getFloors().stream().map(floorDto -> floorDto.getIRI().rawIRI())
                .collect(Collectors.toList()),
            hasItems("http://finder.tuwien.ac.at/spatial/floor/id/H-EG",
                "http://finder.tuwien.ac.at/spatial/floor/id/H-01"));
    }

    @Test
    public void getTractsOfBuildingWithId_ok()
        throws ServiceException, IRIInvalidException, IRIUnknownException {
        Dto resultDto = serviceFactory
            .getService(BASE_IRI, getPathScanner("spatial/building/id/DABC/buildingtracts"), null)
            .execute();
        List<Resource> resultValues = RDFCollections
            .asValues(resultDto.getModel(), valueFactory.createIRI(resultDto.getIRI().toString()),
                new LinkedList<>()).stream().filter(value -> value instanceof Resource)
            .map(value -> (Resource) value).collect(Collectors.toList());
        System.out.println(resultValues);
        assertThat("The result must contain building tracts with the ids DA, DB, DC.", resultValues,
            hasItems(valueFactory.createIRI(BASE.stringValue(), "spatial/buildingtract/id/DA"),
                valueFactory.createIRI(BASE.stringValue(), "spatial/buildingtract/id/DB"),
                valueFactory.createIRI(BASE.stringValue(), "spatial/buildingtract/id/DC")));
        assertThat("The result model must contain only three building tracts.", resultValues,
            hasSize(3));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getBuildingWithUnknownId_throwsResourceNotFoundException()
        throws ServiceException, IRIUnknownException, IRIInvalidException {
        serviceFactory.getService(BASE_IRI, getPathScanner("spatial/building/id/ABC"), null)
            .execute();
    }

    @Test
    public void getBuildingtractWithId_ok()
        throws ServiceException, IRIInvalidException, IRIUnknownException {
        IRI buildingTractAA =
            valueFactory.createIRI(BASE.stringValue(), "spatial/buildingtract/id/AA");
        Model result =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/buildingtract/id/AA"), null)
                .execute().getModel();
        assertTrue(String
                .format("Resource <%s> must be part of the result.", buildingTractAA.stringValue()),
            result.subjects().contains(buildingTractAA));
        assertThat(String.format("Resource <%s> has the rdfs:label 'AA Haupttrakt'",
            buildingTractAA.stringValue()),
            result.filter(buildingTractAA, RDFS.LABEL, null).objects().iterator().next()
                .stringValue(), is("AA Haupttrakt"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getBuildingtractWithUnknownId_throwsResourceNotFoundException()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        serviceFactory.getService(BASE_IRI, getPathScanner("spatial/buildingtract/id/ABCD"), null)
            .execute();
    }

    @Test
    public void getFloorWithId_ok()
        throws ServiceException, IRIUnknownException, IRIInvalidException {
        IRI floorH_EG = valueFactory.createIRI(BASE.stringValue(), "spatial/floor/id/H-EG");
        Dto responseDto =
            serviceFactory.getService(BASE_IRI, getPathScanner("spatial/floor/id/H-EG"), null)
                .execute();
        assertThat(responseDto, instanceOf(FloorDto.class));
        FloorDto floorDto = (FloorDto) responseDto;
        assertThat("The returned floor must have the given IRI", floorDto.id().stringValue(),
            is(floorH_EG.stringValue()));
        assertTrue(
            String.format("Resource <%s> must be part of the model.", floorH_EG.stringValue()),
            floorDto.getModel().subjects().contains(floorH_EG));
        assertThat("The spatial identifier of the given IRI must be 'H-EG'",
            floorDto.getSpatialIdentifier(), is("H-EG"));
        assertThat("The returned floor dto must have rdfs:label 'EG'.", floorDto.getLabel(),
            is("EG"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getFloorWithUnknownId_throwsResourceNotFoundException()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        serviceFactory.getService(BASE_IRI, getPathScanner("spatial/floor/id/ABCD01"), null)
            .execute();
    }

    @Test
    public void getFloorSectionWithId_ok()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        IRI sectionHAEG =
            valueFactory.createIRI(BASE.stringValue(), "spatial/floor/id/H-EG/section/id/HAEG");
        Dto resultDto = serviceFactory
            .getService(BASE_IRI, getPathScanner("spatial/floor/id/H-EG/section/id/HAEG"), null)
            .execute();
        assertThat(
            "The result dto must contain the requested resource 'spatial/floor/id/H-EG/section/HAEG'.",
            resultDto.getModel().subjects(), hasItem(sectionHAEG));
        assertThat("The label of the returned dto must be 'Section HA in EG'.",
            resultDto.getLabel(), is("Section HA in EG"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getFloorSectionWithUnknownId_throwsResourceNotFoundException()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        serviceFactory
            .getService(BASE_IRI, getPathScanner("spatial/floor/id/H-EG/section/id/HZEG"), null)
            .execute();
    }

    @Test
    public void getFloorSectionsOfFloorWithId_ok()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        Dto resultDto = serviceFactory
            .getService(BASE_IRI, getPathScanner("spatial/floor/id/H-EG/sections"), null).execute();
        assertThat(resultDto, instanceOf(ResourceCollectionDto.class));
        ResourceCollectionDto resourcesList = (ResourceCollectionDto) resultDto;
        assertThat(resourcesList.asList().stream()
                .filter(resource -> resource instanceof IResourceIdentifier)
                .map(resource -> ((IResourceIdentifier) resource).rawIRI())
                .collect(Collectors.toList()),
            hasItems("http://finder.tuwien.ac.at/spatial/floor/id/H-EG/section/id/HAEG",
                "http://finder.tuwien.ac.at/spatial/floor/id/H-EG/section/id/HGEG",
                "http://finder.tuwien.ac.at/spatial/floor/id/H-EG/section/id/HBEG"));
        assertThat("Floor 'H-EG' has 8 sections.", resourcesList.asList(), hasSize(9));
    }

    @Test
    public void getAddressWithId_ok()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        IRI addressKarlsplatz = valueFactory.createIRI(BASE.stringValue(),
            "spatial/address/id/AT1040-1c56fcbcb8725edda11e2c76a1d21c77-13");
        Model result = serviceFactory.getService(BASE_IRI,
            getPathScanner("spatial/address/id/AT1040-1c56fcbcb8725edda11e2c76a1d21c77-13"), null)
            .execute().getModel();
        assertTrue(String
                .format("Resource <%s> must be part of the result.", addressKarlsplatz.toString()),
            result.subjects().contains(addressKarlsplatz));
        assertThat(
            String.format("Resource <%s> has locn:postCode '1040'.", addressKarlsplatz.toString()),
            Models.objectLiteral(result.filter(addressKarlsplatz, LOCN.fullAddress, null))
                .orElse(null).stringValue(), is("Karlsplatz 13, 1040 Wien, Österreich"));
        assertThat(
            String.format("Resource <%s> has locn:postCode '1040'.", addressKarlsplatz.toString()),
            Models.objectLiteral(result.filter(addressKarlsplatz, LOCN.postCode, null)).orElse(null)
                .stringValue(), is("1040"));
        assertThat(
            String.format("Resource <%s> has locn:poBox '13'.", addressKarlsplatz.toString()),
            Models.objectLiteral(result.filter(addressKarlsplatz, LOCN.locatorDesignator, null))
                .orElse(null).stringValue(), is("13"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getAddressWithUnknownId_throwsIRIUnknownException()
        throws ServiceException, IRIUnknownException, IRIInvalidException {
        serviceFactory.getService(BASE_IRI,
            getPathScanner("spatial/address/id/DE1100-1c56fcbcb8725edda11e2c76a1d21c77-na"), null)
            .execute();
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
