package at.ac.tuwien.finder.service.spatial.stairway.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that provides access to services for
 * a specific stairways.
 *
 * @author Kevin Haller
 */
public class StairwayServiceFactory extends InternalTreeNodeServiceFactory {
    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    public StairwayServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(StairwayResourceServiceFactory.getManagedPathName(),
            new StairwayResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "stairway";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
