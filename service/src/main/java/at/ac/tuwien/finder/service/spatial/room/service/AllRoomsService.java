package at.ac.tuwien.finder.service.spatial.room.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.SimpleQueryService;
import at.ac.tuwien.finder.vocabulary.TUVS;

/**
 * This class is an implementation of {@link IService} that returns a description of all known
 * rooms.
 *
 * @author Kevin Haller
 */
public class AllRoomsService extends SimpleQueryService {

    private static String allRoomsQuery =
        String.format("DESCRIBE ?building WHERE { ?building a <%s> . }", TUVS.Room.toString());

    /**
     * Creates a new instance of {@link AllRoomsService}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link AllRoomsService}.
     */
    public AllRoomsService(TripleStoreManager tripleStoreManager) {
        super(tripleStoreManager);
    }

    @Override
    public String getQuery() {
        return allRoomsQuery;
    }
}
