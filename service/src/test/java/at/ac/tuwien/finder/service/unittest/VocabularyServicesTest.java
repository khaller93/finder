package at.ac.tuwien.finder.service.unittest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.IResourceIdentifier;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.VocabularyManager;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests the implementation of {@link ServiceFactory} and all affiliated
 * {@link at.ac.tuwien.finder.service.IServiceFactory} and {@link at.ac.tuwien.finder.service.IService}
 * concerning vocabulary services.
 *
 * @author Kevin Haller
 */
public class VocabularyServicesTest {

    private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private static IResourceIdentifier BASE_IRI;

    /**
     * Creates a new instance of {@link VocabularyServicesTest}.
     */
    public VocabularyServicesTest() throws URISyntaxException {
        BASE_IRI = new IResourceIdentifier(TripleStoreManager.BASE.stringValue());
    }

    private static Model wineVocabularyModel;

    @BeforeClass
    public static void setUpClass() throws IOException {
        wineVocabularyModel = Rio.parse(VocabularyServicesTest.class.getClassLoader()
            .getResourceAsStream("vocabulary/wine.rdf"), "", RDFFormat.RDFXML);
    }

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() {
        // TripleStoreManager acting like a dummy
        TripleStoreManager tripleStoreManager = mock(TripleStoreManager.class);
        // Vocabulary manager
        VocabularyManager vocabularyManager = mock(VocabularyManager.class);
        when(vocabularyManager.getSpatialOntology()).thenReturn(wineVocabularyModel);
        VocabularyManager.setVocabularyManager(vocabularyManager);
        serviceFactory = new ServiceFactory(tripleStoreManager);
    }

    @Test
    public void getSpatialOntology_ok() throws Exception {
        IRI rootResource =
            valueFactory.createIRI("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine");
        IRI wineResource =
            valueFactory.createIRI("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine");
        Model spatialOntology =
            serviceFactory.getService(BASE_IRI, getPathScanner("vocab/spatial"), null).execute()
                .getModel();
        assertThat(String.format("The returned model must contain the root resource '%s'.",
            rootResource.stringValue()), spatialOntology.subjects(), hasItem(rootResource));
        assertThat(
            "The class 'Wine' of the returned dummy wine ontology must be part of the raw model.",
            spatialOntology.subjects(), hasItem(wineResource));
        assertThat(
            "The class 'Wine' of the returned dummy wine ontology must have two rdfs:label, 'wine' and 'vin'",
            spatialOntology.filter(wineResource, RDFS.LABEL, null).objects().stream()
                .filter(value -> value instanceof Literal).map(value -> (Literal) value)
                .map(Literal::getLabel).collect(Collectors.toList()),
            containsInAnyOrder("wine", "vin"));
    }

    @Test(expected = IRIUnknownException.class)
    public void getUnknownOntology_throwsIRIUnknownException()
        throws IRIInvalidException, IRIUnknownException, ServiceException {
        serviceFactory.getService(BASE_IRI, getPathScanner("vocab/na"), null).execute();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        VocabularyManager.setVocabularyManager(null);
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
