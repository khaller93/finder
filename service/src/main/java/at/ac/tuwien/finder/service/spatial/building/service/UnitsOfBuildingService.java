package at.ac.tuwien.finder.service.spatial.building.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleDtoCollectionDto;
import at.ac.tuwien.finder.dto.spatial.BuildingUnitDto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of {@link IService} that returns a description of all
 * building units of a given type in the given {@code TuViennaSpatialOntology.Building}.
 *
 * @author Kevin Haller
 */
public class UnitsOfBuildingService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(UnitsOfBuildingService.class);

    private ValueFactory valueFactory = SimpleValueFactory.getInstance();
    private String unitsOfBuildingQuery;
    private TripleStoreManager tripleStoreManager;
    private String unitName;
    private IRI unitsOfBuildingsIRI;

    /**
     * Creates a new instance of {@link UnitsOfBuildingService}.
     *
     * @param tripleStoreManager  the {@link TripleStoreManager} that manages the triple store that
     *                            shall be used as knowledge base for this
     *                            {@link UnitsOfBuildingService}.
     * @param unitsOfBuildingsUri the uri of the request that builds the head of the returned rdf
     *                            collection.
     * @param unitName            the name of the units that are requested.
     * @param resourceUri         the URI of the building of which the building units shall be
     *                            returned.
     * @param buildingUnitTypeUri the URI of the building unit type that shall be returned.
     */
    public UnitsOfBuildingService(TripleStoreManager tripleStoreManager, String unitsOfBuildingsUri,
        String unitName, String resourceUri, String buildingUnitTypeUri) {
        assert unitName != null;
        assert resourceUri != null;
        this.tripleStoreManager = tripleStoreManager;
        this.unitName = unitName;
        this.unitsOfBuildingsIRI = valueFactory.createIRI(unitsOfBuildingsUri);
        this.unitsOfBuildingQuery = String
            .format("DESCRIBE ?unit WHERE { <%s> a <%s> ; <%s> ?unit . ?unit a <%s> . }",
                resourceUri, TUVS.Building.stringValue(), TUVS.containsBuildingUnit.stringValue(),
                buildingUnitTypeUri);
        logger.debug("Query for getting {} of buildings: {}", buildingUnitTypeUri,
            unitsOfBuildingQuery);
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model responseModel = QueryResults.asModel(
                connection.prepareGraphQuery(QueryLanguage.SPARQL, unitsOfBuildingQuery)
                    .evaluate());
            responseModel.add(unitsOfBuildingsIRI, RDFS.LABEL, valueFactory
                .createLiteral(String.format("All known %ss", unitName.toLowerCase()), "en"));
            responseModel = RDFCollections
                .asRDF(responseModel.filter(null, RDF.TYPE, TUVS.BuildingUnit).subjects(),
                    unitsOfBuildingsIRI, responseModel);
            return RDFMapper.create()
                .readValue(responseModel, SimpleDtoCollectionDto.class, BuildingUnitDto.class,
                    unitsOfBuildingsIRI);
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }
}
