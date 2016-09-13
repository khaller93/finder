package at.ac.tuwien.finder.service.spatial.building.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.SimpleQueryService;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of {@link IService} that returns a description of all
 * building units of a given type in the given {@code TuViennaSpatialOntology.Building}.
 *
 * @author Kevin Haller
 */
public class UnitsOfBuildingService extends SimpleQueryService {

    private static final Logger logger = LoggerFactory.getLogger(UnitsOfBuildingService.class);

    private String tractsOfBuildingQuery;

    /**
     * Creates a new instance of {@link UnitsOfBuildingService}.
     *
     * @param tripleStoreManager  the {@link TripleStoreManager} that manages the triple store that
     *                            shall be used as knowledge base for this {@link UnitsOfBuildingService}.
     * @param resourceUri         the URI of the building of which the building units shall be
     *                            returned.
     * @param buildingUnitTypeUri the URI of the building unit type that shall be returned.
     */
    public UnitsOfBuildingService(TripleStoreManager tripleStoreManager, String resourceUri,
        String buildingUnitTypeUri) {
        super(tripleStoreManager);
        assert resourceUri != null;
        this.tractsOfBuildingQuery = String
            .format("DESCRIBE ?unit WHERE { <%s> a <%s> ; <%s> ?unit . ?unit a <%s> . }",
                resourceUri, TUVS.Building.toString(), TUVS.containsBuildingUnit.toString(),
                buildingUnitTypeUri);
        logger.debug("Query for getting {} of buildings: {}", buildingUnitTypeUri,
            tractsOfBuildingQuery);
    }

    @Override
    public String getQuery() {
        return tractsOfBuildingQuery;
    }

}
