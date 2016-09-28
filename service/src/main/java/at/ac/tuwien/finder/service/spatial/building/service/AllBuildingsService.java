package at.ac.tuwien.finder.service.spatial.building.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.ResourceCollectionDto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;

import java.util.stream.Collectors;

/**
 * This class is an implementation of {@link IService} that returns a description of all known
 * buildings.
 *
 * @author Kevin Haller
 */
public class AllBuildingsService implements IService {

    private final static String ALL_BUILDING_QUERY =
        String.format("SELECT ?building WHERE { ?building a <%s> . }", TUVS.Building.stringValue());

    private TripleStoreManager tripleStoreManager;
    private IRI buildingIri;
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * Creates a new instance of {@link AllBuildingsService}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link AllBuildingsService}.
     */
    public AllBuildingsService(TripleStoreManager tripleStoreManager, String buildingIRI) {
        assert tripleStoreManager != null;
        this.buildingIri = valueFactory.createIRI(buildingIRI);
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model responseModel = new LinkedHashModel();
            responseModel.add(buildingIri, RDFS.LABEL,
                valueFactory.createLiteral("All known buildings", "en"));
            return new ResourceCollectionDto(buildingIri, RDFCollections.asRDF(Iterations.stream(
                connection.prepareTupleQuery(QueryLanguage.SPARQL, ALL_BUILDING_QUERY).evaluate())
                .map(bindings -> bindings.getBinding("building").getValue())
                .filter(value -> value instanceof IRI).map(value -> (IRI) value)
                .collect(Collectors.toList()), buildingIri, responseModel));
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }

}
