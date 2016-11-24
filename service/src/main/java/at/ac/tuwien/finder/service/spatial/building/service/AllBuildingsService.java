package at.ac.tuwien.finder.service.spatial.building.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleDtoCollectionDto;
import at.ac.tuwien.finder.dto.spatial.BuildingDto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.outofbits.opinto.RDFMapper;

/**
 * This class is an implementation of {@link IService} that returns a description of all known
 * buildings.
 *
 * @author Kevin Haller
 */
public class AllBuildingsService implements IService {

    private final static String ALL_BUILDING_QUERY = String.format(
        "DESCRIBE ?building ?geometry WHERE { ?building a <%s> . OPTIONAL { ?building <%s> ?geometry .} .}",
        TUVS.Building.stringValue(), GeoSPARQL.hasGeometry.stringValue());

    private TripleStoreManager tripleStoreManager;
    private IRI allBuildinsgIri;
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * Creates a new instance of {@link AllBuildingsService}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link AllBuildingsService}.
     */
    public AllBuildingsService(TripleStoreManager tripleStoreManager, String allBuildinsgIri) {
        assert tripleStoreManager != null;
        this.allBuildinsgIri = valueFactory.createIRI(allBuildinsgIri);
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model buildingListResponse = QueryResults.asModel(
                connection.prepareGraphQuery(QueryLanguage.SPARQL, ALL_BUILDING_QUERY).evaluate());
            buildingListResponse.add(allBuildinsgIri, RDFS.LABEL,
                valueFactory.createLiteral("All known buildings", "en"));
            buildingListResponse = RDFCollections
                .asRDF(buildingListResponse.filter(null, RDF.TYPE, TUVS.Building).subjects(),
                    allBuildinsgIri, buildingListResponse);
            return RDFMapper.create()
                .readValue(buildingListResponse, SimpleDtoCollectionDto.class, BuildingDto.class,
                    allBuildinsgIri);
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }

}
