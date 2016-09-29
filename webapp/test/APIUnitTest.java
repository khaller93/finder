import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.BuildingDto;
import at.ac.tuwien.finder.dto.IResourceIdentifier;
import at.ac.tuwien.finder.service.ServiceFactory;
import controllers.APIController;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
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
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import play.twirl.api.Content;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 */
public class APIUnitTest extends WithApplication {

    private static TripleStoreManager tripleStoreManager;

    private static Repository repository;

    @BeforeClass
    public static void setUpClass() throws IOException {
        tripleStoreManager = mock(TripleStoreManager.class);
        repository = new SailRepository(new MemoryStore());
        repository.initialize();
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(
                Rio.parse(APIUnitTest.class.getClassLoader().getResourceAsStream("dbTestDump.trig"),
                    "", RDFFormat.TRIG));
        }
    }

    private ValueFactory valueFactory = SimpleValueFactory.getInstance();
    private APIController apiController;

    /**
     * Gets all statements of the test triple store.
     *
     * @return {@link Model} containing all statements of the test triple store.
     */
    private Model getModel() {
        try (RepositoryConnection connection = repository.getConnection()) {
            return new LinkedHashModel(
                Iterations.asList(connection.getStatements(null, null, null)));
        }
    }

    @Before
    public void setUp() {
        when(tripleStoreManager.getConnection()).thenReturn(repository.getConnection());
        apiController = new APIController(new ServiceFactory(tripleStoreManager));
    }

    @Test
    public void renderBuildingTemplate_ok() {
        IRI buildingHIRI =
            valueFactory.createIRI("http://finder.tuwien.ac.at/spatial/building/id/H");
        BuildingDto buildingHDto =
            new BuildingDto(new IResourceIdentifier(buildingHIRI.stringValue()),
                getModel().filter(buildingHIRI, null, null));
        Content dataPageContent = views.html.dataPage.render("spatial/building/id/H", buildingHDto);
        assertEquals("text/html", dataPageContent.contentType());
        assertThat(
            "The building label with the code H must be 'Building of the informatic institute'.",
            dataPageContent.body(), containsString("Building of the informatics institute"));
        assertThat("Building must have the property value 'tuvs:Building'.", dataPageContent.body(),
            containsString("tuvs:Building"));
    }

    @Test
    public void renderDataPageTemplateOfBuildingResource_ok() {
        Result buildingResult = apiController.page("spatial/building/id/A");
        String buildingResultString = Helpers.contentAsString(buildingResult);
        assertThat("The result must indicate a successful processed request. Status Code: 200",
            buildingResult.status(), is(200));
        assertThat("The result must be of the format text/html.",
            buildingResult.contentType().orElse(null), is("text/html"));
        assertThat("The result must contain the name 'Central building' of the building.",
            buildingResultString, containsString("Central building"));
        assertThat(
            "The result most contain the resource 'buildingtract:AA' as being part of this building",
            buildingResultString, containsString("buildingtract:AA"));
    }

    @Test
    public void renderDataPageTemplateOfUnknownBuildingResource_notFoundException() {
        Result buildingResult = apiController.page("spatial/building/id/AB");
        assertThat(
            "The result must indicate that the requested resource could not be found. Status Code: 404",
            buildingResult.status(), is(404));
    }

    @Test
    public void renderDataPageTemplateOfUnknownBuildingResource_badRequestException() {
        Result buildingResult = apiController.page("building/id/AB");
        assertThat(
            "The result must indicate that the IRI is not assigned to any service. Status Code: 400",
            buildingResult.status(), is(400));
    }

}
