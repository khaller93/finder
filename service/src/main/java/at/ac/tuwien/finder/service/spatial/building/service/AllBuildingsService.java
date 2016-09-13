package at.ac.tuwien.finder.service.spatial.building.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.SimpleQueryService;
import at.ac.tuwien.finder.vocabulary.TUVS;

/**
 * This class is an implementation of {@link IService} that returns a description of all known
 * buildings.
 *
 * @author Kevin Haller
 */
public class AllBuildingsService extends SimpleQueryService {

    private static String allBuildingsQuery = String
        .format("DESCRIBE ?building WHERE { ?building a <%s> . }", TUVS.Building.toString());

    /**
     * Creates a new instance of {@link AllBuildingsService}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link AllBuildingsService}.
     */
    public AllBuildingsService(TripleStoreManager tripleStoreManager) {
        super(tripleStoreManager);
    }

    @Override
    public String getQuery() {
        return allBuildingsQuery;
    }
}
