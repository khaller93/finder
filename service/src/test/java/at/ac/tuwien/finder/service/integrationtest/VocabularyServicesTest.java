package at.ac.tuwien.finder.service.integrationtest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;

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
     * Creates a new instance of {@link at.ac.tuwien.finder.service.unittest.VocabularyServicesTest}.
     */
    public VocabularyServicesTest() throws URISyntaxException {
        BASE_IRI = new IResourceIdentifier(TripleStoreManager.BASE.stringValue());
    }

    private ServiceFactory serviceFactory;

    @Before
    public void setUp() {
        // TripleStoreManager acting like a dummy
        TripleStoreManager tripleStoreManager = mock(TripleStoreManager.class);
        serviceFactory = new ServiceFactory(tripleStoreManager);
    }

    @Test
    public void getSpatialOntology_ok() throws Exception {
        IRI rootSpatialOntology =
            valueFactory.createIRI(TripleStoreManager.BASE.stringValue(), "vocab/spatial#");
        Model spatialOntology =
            serviceFactory.getService(BASE_IRI, getPathScanner("vocab/spatial"), null).execute()
                .getModel();
        assertThat(String.format("The returned model must contain the root resource '%s'.",
            rootSpatialOntology.stringValue()), spatialOntology.subjects(),
            hasItem(rootSpatialOntology));
        assertThat(
            "The class 'Building' must be part of the raw model of the returned spatial ontology.",
            spatialOntology.subjects(), hasItem(TUVS.Building));
        assertThat("The class 'Building' must have two rdfs:label 'Building' and 'Gebäude'.",
            spatialOntology.filter(TUVS.Building, RDFS.LABEL, null).objects().stream()
                .filter(value -> value instanceof Literal).map(value -> (Literal) value)
                .map(Literal::getLabel).collect(Collectors.toList()),
            containsInAnyOrder("Building", "Gebäude"));
    }

    @Test(expected = IRIUnknownException.class)
    public void getUnknownOntology_throwsIRIUnknownException()
        throws ServiceException, IRIInvalidException, IRIUnknownException {
        serviceFactory.getService(BASE_IRI, getPathScanner("vocab/na"), null).execute();
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }
}
