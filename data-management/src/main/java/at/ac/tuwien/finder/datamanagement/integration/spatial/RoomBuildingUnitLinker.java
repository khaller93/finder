package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.integration.DataLinker;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This abstract class implements a {@link DataLinker} that links room to building units that fit
 * the identifier.
 *
 * @author Kevin Haller
 */
public class RoomBuildingUnitLinker implements DataLinker {

    private static final Logger logger = LoggerFactory.getLogger(RoomBuildingTractLinker.class);

    private TripleStoreManager tripleStoreManager = TripleStoreManager.getInstance();

    private IRI buildingUnitType;
    private IRI buildingUnitIdentifierProperty;

    /**
     * Creates a new {@link RoomBuildingTractLinker}.
     *
     * @param buildingUnitType               {@link IRI} of the type of building to which the rooms
     *                                       shall be linked.
     * @param buildingUnitIdentifierProperty {@link IRI} of the property that gets the identifier of
     *                                       the building unit that shall be used for linking.
     */
    public RoomBuildingUnitLinker(IRI buildingUnitType, IRI buildingUnitIdentifierProperty) {
        this.buildingUnitType = buildingUnitType;
        this.buildingUnitIdentifierProperty = buildingUnitIdentifierProperty;
    }

    @Override
    public Model link() {
        Model linkResultModel = new LinkedHashModel();
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Map<String, Resource> buildingTractMap = new HashMap<>();
            TupleQueryResult buildingTractResult = connection
                .prepareTupleQuery(QueryLanguage.SPARQL, String
                    .format("SELECT ?bTract ?tCode WHERE { ?bTract a <%s>; <%s> ?tCode . }",
                        buildingUnitType, buildingUnitIdentifierProperty)).evaluate();
            while (buildingTractResult.hasNext()) {
                BindingSet currentBS = buildingTractResult.next();
                buildingTractMap.put(((Literal) currentBS.getValue("tCode")).getLabel(),
                    (Resource) currentBS.getValue("bTract"));
            }
            connection.getStatements(null, RDF.TYPE, TUVS.BuildingTract);
            logger.debug("Build map of building tract linking: {}.", buildingTractMap);
            TupleQueryResult roomResult = connection.prepareTupleQuery(QueryLanguage.SPARQL, String
                .format("SELECT ?room ?rCode WHERE { ?room a <%s>; <%s> ?rCode . }", TUVS.Room,
                    TUVS.roomCode)).evaluate();
            while (roomResult.hasNext()) {
                BindingSet currentBS = roomResult.next();
                String roomCode = ((Literal) currentBS.getValue("rCode")).getLabel();
                Resource buildingTractToLink = null;
                for (int n = 1; n <= roomCode.length(); n++) {
                    Resource currentBToLink = buildingTractMap.get(roomCode.substring(0, n));
                    if (buildingTractToLink != null && currentBToLink == null) {
                        break;
                    }
                    buildingTractToLink = currentBToLink;
                }
                if (buildingTractToLink != null) {
                    linkResultModel.add(buildingTractToLink, TUVS.containsBuildingUnit,
                        currentBS.getValue("room"));
                }
            }
        }
        return linkResultModel;
    }

    @Override
    public void close() throws Exception {
        if (tripleStoreManager != null) {
            tripleStoreManager.close();
        }
    }

}
