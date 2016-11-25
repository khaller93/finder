package at.ac.tuwien.finder.service.integrationtest;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.ExceptionResourceDto;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.TestTripleStore;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Scanner;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * This class tests if the available dump of the spatial data fulfils the
 * quality criteria for being accepted in the LOD Cloud.
 *
 * @author Kevin Haller
 */
public class LODCloudQualityTest {

    private ServiceFactory serviceFactory;

    @Rule
    public TestTripleStore testTripleStore = new TestTripleStore();

    @Before
    public void setUp() {
        serviceFactory = new ServiceFactory(testTripleStore.getTripleStoreManager());
    }

    /**
     * There must be resolvable http:// (or https://) URIs.
     * <p>
     * They must resolve, with or without content negotiation, to RDF data in one of the
     * popular RDF formats (RDFa, RDF/XML, Turtle, N-Triples)
     */
    @Test
    public void areAllLocalResourcesResolvable()
        throws IRIUnknownException, IRIInvalidException, ServiceException {
        try (RepositoryConnection connection = testTripleStore.getTripleStoreManager().
            getConnection()) {
            for (Resource resource : Iterations
                .stream(connection.getStatements(null, null, null, SpatialDataSet.NS)).filter(
                    statement -> statement.getSubject().stringValue()
                        .startsWith(TripleStoreManager.BASE.stringValue()))
                .map(Statement::getSubject).collect(Collectors.toSet())) {
                try {
                    Dto response = serviceFactory.getService(getPathScanner(
                        resource.stringValue().replace(TripleStoreManager.BASE.stringValue(), "")))
                        .execute();
                    assertThat("The access of the resource must not return an exception.", response,
                        not(instanceOf(ExceptionResourceDto.class)));
                } catch (IRIUnknownException | IRIInvalidException | ServiceException e) {
                    fail(String.format("The resource %s must be resolvable, but is not.",
                        resource.stringValue()));
                }
            }
        }
    }

    /**
     * The dataset must contain at least 1000 triples.
     */
    @Test
    public void hasSpatialDatasetEnoughStatements() throws Exception {
        try (RepositoryConnection connection = testTripleStore.getTripleStoreManager().
            getConnection()) {
            assertThat("he dataset must contain at least 1000 triples.",
                Iterations.asList(connection.getStatements(null, null, null, SpatialDataSet.NS)),
                hasSize(greaterThan(1000)));
        }
    }

    /**
     * The dataset must be connected via RDF links to a dataset that is already in the
     * diagram. This means, either your dataset must use URIs from the other dataset,
     * or vice versam. We arbitrarily require at least 50 links.
     */
    @Test
    public void hasSpatialDatasetMoreThan50ExternalLinks() {
        try (RepositoryConnection connection = testTripleStore.getTripleStoreManager().
            getConnection()) {
            assertThat(
                "The dataset must be connected via RDF links to a dataset that is already in the diagram. This means, either your dataset must use URIs from the other dataset, or vice versam. We arbitrarily require at least 50 links.",
                Iterations.stream(connection.getStatements(null, null, null, SpatialDataSet.NS))
                    .map(Statement::getObject).filter(value -> value instanceof IRI)
                    .map(value -> (IRI) value).filter(resource -> !resource.stringValue()
                    .startsWith(TripleStoreManager.BASE.stringValue())).collect(Collectors.toSet()),
                hasSize(greaterThan(50)));
        }
    }

    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
