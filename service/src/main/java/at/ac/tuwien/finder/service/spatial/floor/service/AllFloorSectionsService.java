package at.ac.tuwien.finder.service.spatial.floor.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleDtoCollectionDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.spatial.FloorSectionDto;
import at.ac.tuwien.finder.service.QueryService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.outofbits.opinto.RDFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of {@link QueryService} that gets all the floor sections of a
 * given floor.
 *
 * @author Kevin Haller
 */
public class AllFloorSectionsService implements QueryService {

    private static final Logger logger = LoggerFactory.getLogger(AllFloorSectionsService.class);

    private TripleStoreManager tripleStoreManager;
    private IResourceIdentifier floorResource;
    private IResourceIdentifier requestIRI;
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * Creates a new {@link AllFloorSectionsService} for the given floor.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for this service.
     * @param floorResource      {@link IResourceIdentifier} for which all sections shall be returned.
     * @param requestIRI         {@link IResourceIdentifier} of the request that will be the head of
     *                           the returned list.
     */
    public AllFloorSectionsService(TripleStoreManager tripleStoreManager,
        IResourceIdentifier floorResource, IResourceIdentifier requestIRI) {
        this.tripleStoreManager = tripleStoreManager;
        this.floorResource = floorResource;
        this.requestIRI = requestIRI;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model floorSectionsModel = QueryResults.asModel(
                connection.prepareGraphQuery(QueryLanguage.SPARQL, getQuery()).evaluate());
            IRI head = valueFactory.createIRI(requestIRI.rawIRI());
            floorSectionsModel.add(head, RDFS.LABEL,
                valueFactory.createLiteral("All known floor sections.", "en"));
            return RDFMapper.create().readValue(RDFCollections
                    .asRDF(floorSectionsModel.filter(null, RDF.TYPE, TUVS.FloorSection).subjects(),
                        head, floorSectionsModel), SimpleDtoCollectionDto.class, FloorSectionDto.class,
                head);
        }
    }

    @Override
    public String getQuery() {
        return String.format("DESCRIBE ?fSection WHERE { <%s> a <%s> ; <%s> ?fSection . }",
            floorResource.rawIRI(), TUVS.Floor, TUVS.hasFloorSection);
    }
}
