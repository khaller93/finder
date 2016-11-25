package at.ac.tuwien.finder.service.spatial.pointofroute.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that provides access to services for
 * points of routes.
 *
 * @auhtor Kevin Haller
 */
public class PointOfRouteServiceFactory extends InternalTreeNodeServiceFactory {
    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    public PointOfRouteServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(PointOfRouteResourceServiceFactory.getManagedPathName(),
            new PointOfRouteResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "por";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
