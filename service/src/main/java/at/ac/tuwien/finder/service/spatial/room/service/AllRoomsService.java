package at.ac.tuwien.finder.service.spatial.room.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleDtoCollectionDto;
import at.ac.tuwien.finder.dto.spatial.RoomDto;
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

/**
 * This class is an implementation of {@link IService} that returns a description of all known
 * rooms.
 *
 * @author Kevin Haller
 */
public class AllRoomsService implements IService {

    private final static String ALL_ROOMS_QUERY =
        String.format("DESCRIBE ?room WHERE { ?room a <%s> . }", TUVS.Room.stringValue());

    private TripleStoreManager tripleStoreManager;
    private IRI roomsIri;
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * Creates a new instance of {@link AllRoomsService}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link AllRoomsService}.
     */
    public AllRoomsService(TripleStoreManager tripleStoreManager, String roomsIri) {
        assert tripleStoreManager != null;
        this.roomsIri = valueFactory.createIRI(roomsIri);
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model responseModel = QueryResults.asModel(
                connection.prepareGraphQuery(QueryLanguage.SPARQL, ALL_ROOMS_QUERY).evaluate());
            responseModel
                .add(roomsIri, RDFS.LABEL, valueFactory.createLiteral("All known rooms", "en"));
            responseModel = RDFCollections
                .asRDF(responseModel.filter(null, RDF.TYPE, TUVS.Room).subjects(), roomsIri,
                    responseModel);
            return RDFMapper.create()
                .readValue(responseModel, SimpleDtoCollectionDto.class, RoomDto.class, roomsIri);
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }

}
