package at.ac.tuwien.finder.service.search.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.spatial.SpatialServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that manages all search services.
 *
 * @author Kevin Haller
 */
public class SearchServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpatialServiceFactory.class);

    private Map<String, IServiceFactory> searchServiceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link SpatialServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link SpatialServiceFactory}.
     */
    public SearchServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        searchServiceFactoryMap.put(FreeRoomsServiceFactory.getManagedPathName(),
            new FreeRoomsServiceFactory(tripleStoreManager));
        searchServiceFactoryMap.put(RouteServiceFactory.getManagedPathName(),
            new RouteServiceFactory(tripleStoreManager));
        logger.debug("Factory map of spatial services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", searchServiceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "search";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return searchServiceFactoryMap;
    }
}
