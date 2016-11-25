package at.ac.tuwien.finder.service.spatial.room.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.spatial.AddressDto;
import at.ac.tuwien.finder.service.QueryService;
import at.ac.tuwien.finder.service.exception.ResourceNotFoundException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.outofbits.opinto.RDFMapper;
import org.outofbits.opinto.RDFMappingException;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IService} that computes the
 * address for a room.
 *
 * @author Kevin Haller
 */
public class RoomAddressService implements QueryService {

    private IResourceIdentifier roomIRI;
    private IResourceIdentifier addressIRI;
    private TripleStoreManager tripleStoreManager;


    public RoomAddressService(IResourceIdentifier roomIRI, IResourceIdentifier addressIRI,
        TripleStoreManager tripleStoreManager) {
        assert roomIRI != null;
        assert addressIRI != null;
        assert tripleStoreManager != null;
        this.roomIRI = roomIRI;
        this.addressIRI = addressIRI;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            if (!connection.hasStatement(roomIRI.iriValue(), null, null, true)) {
                throw new ResourceNotFoundException(
                    String.format("The room <%s> cannot be found.", roomIRI.rawIRI()),
                    roomIRI.rawIRI());
            }
            GraphQuery roomAddressQuery =
                connection.prepareGraphQuery(QueryLanguage.SPARQL, getQuery());
            roomAddressQuery.setBinding("room", roomIRI.iriValue());
            roomAddressQuery.setBinding("roomAddressIRI", addressIRI.iriValue());
            try {
                return RDFMapper.create()
                    .readValue(QueryResults.asModel(roomAddressQuery.evaluate()), AddressDto.class,
                        addressIRI.iriValue());
            } catch (RDFMappingException r) {
                throw new ServiceException(
                    String.format("This resource <%s> could not be mapped.", addressIRI), r);
            }
        }
    }

    @Override
    public String getQuery() {
        return "PREFIX tuvs: <http://finder.tuwien.ac.at/vocab/spatial#> \n"
            + "PREFIX locn: <http://www.w3.org/ns/locn#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "CONSTRUCT { ?roomAddressIRI a locn:Address ;  locn:addressId ?roomAddressIRI; locn:fullAddress ?roomFullAddress ; locn:locatorDesignator ?roomLocatorDesignator; locn:locatorName ?roomLocatorName ; ?addressProperty ?addressObject . } where { \n"
            + "    ?room tuvs:roomCode ?roomCode .\n"
            + "    ?building a tuvs:Building ; (tuvs:containsBuildingUnit)+ ?room .\n"
            + "    ?building locn:address ?address .\n"
            + "    ?address ?addressProperty ?addressObject ;\n"
            + "        locn:locatorDesignator ?locatorDesignator ;\n"
            + "        locn:thoroughfare ?thoroughfare ;\n" + "        locn:postCode ?postCode ;\n"
            + "        locn:postName ?postNameDE .\n" + "    OPTIONAL {\n"
            + "        ?floor a tuvs:Floor ;\n"
            + "            (tuvs:containsBuildingUnit)+ ?room ;\n"
            + "            rdfs:label ?floorLabel .\n" + "    }\n" + "    OPTIONAL { \n"
            + "        ?bt a tuvs:BuildingTract ;\n"
            + "            (tuvs:containsBuildingUnit)+ ?room ;\n"
            + "            rdfs:label ?btLabel .\n" + "    }\n"
            + "    BIND(concat(?locatorDesignator, IF(BOUND(?floorLabel), concat(\"/\", ?floorLabel), \"\"), IF(BOUND(?btLabel), concat(\"/Tract \", ?btLabel), \"\"), \"\") as ?roomLocatorDesignator) .\n"
            + "    BIND(concat(str(?thoroughfare), \" \", str(?roomLocatorDesignator), \", \", str(?postCode), \" \", str(?postNameDE), \", Österreich\") as ?roomFullAddress) .\n"
            + "    FILTER(?addressProperty != locn:fullAddress && ?addressProperty != locn:locatorDesignator && ?addressProperty != locn:addressId).\n"
            + "    FILTER(lang(?postNameDE) = \"de\") .\n" + "    OPTIONAL {\n"
            + "        ?room rdfs:label ?roomLabel .\n" + "    }\n"
            + "    BIND(IF(BOUND(?roomLabel), concat(str(?roomLabel), \" (\", str(?roomCode), \")\"), ?roomCode) as ?roomLocatorName)\n"
            + "}";
    }
}
