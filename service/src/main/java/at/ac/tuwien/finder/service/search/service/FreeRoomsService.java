package at.ac.tuwien.finder.service.search.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleDtoCollectionDto;
import at.ac.tuwien.finder.dto.spatial.RoomDto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.SCHEMA;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.outofbits.opinto.RDFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is an implementation of {@link IService} that search for free rooms in a given time
 * range.
 *
 * @author Kevin Haller
 */
public class FreeRoomsService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(FreeRoomsService.class);

    private SimpleDateFormat readableDateFormat =
        new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");

    private TripleStoreManager tripleStoreManager;
    private Date startDate;
    private Date endDate;

    private String requestIRIString;
    private String query;

    /**
     * Creates a new instance of {@link FreeRoomsService} for the given time range.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for this service.
     * @param requestIRIString   the {@link IRI} of the search request.
     * @param startDate          the start date of the time range.
     * @param endDate            the end date of the time range.
     */
    public FreeRoomsService(TripleStoreManager tripleStoreManager, String requestIRIString,
        Date startDate, Date endDate) {
        assert tripleStoreManager != null;
        assert startDate != null && endDate != null;
        assert startDate.before(endDate);
        this.tripleStoreManager = tripleStoreManager;
        this.requestIRIString = requestIRIString;
        this.startDate = startDate;
        this.endDate = endDate;
        this.query = String.format("DESCRIBE ?room where { ?room a <%s> . FILTER NOT EXISTS {\n"
            + "?event a <%s> ; <%s> ?room ; <%s> ?startDate ; <%s> ?endDate .\n"
            + "FILTER(!((?startDate < ?tfBegin && ?tfBegin >= ?endDate) || (?startDate >= ?tfEnd && ?tfEnd < ?endDate))) .\n"
            + "}}", TUVS.Room, SCHEMA.Event, SCHEMA.location, SCHEMA.startDate, SCHEMA.endDate);
        logger.debug("Free rooms query: {}", query);
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            ValueFactory valueFactory = SimpleValueFactory.getInstance();
            GraphQuery freeRoomsQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
            freeRoomsQuery.setBinding("tfBegin", valueFactory.createLiteral(startDate));
            freeRoomsQuery.setBinding("tfEnd", valueFactory.createLiteral(endDate));
            IRI requestIRI = valueFactory.createIRI(requestIRIString);
            Model responseModel = QueryResults.asModel(freeRoomsQuery.evaluate());
            responseModel.add(requestIRI, RDFS.LABEL, valueFactory.createLiteral(String
                .format("All free rooms from %s to %s.", readableDateFormat.format(startDate),
                    readableDateFormat.format(endDate)), "en"));
            return RDFMapper.create().readValue(responseModel, SimpleDtoCollectionDto.class,
                RoomDto.class, requestIRI);
        }
    }
}
