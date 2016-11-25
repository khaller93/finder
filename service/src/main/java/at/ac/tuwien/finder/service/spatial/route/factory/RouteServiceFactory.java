package at.ac.tuwien.finder.service.spatial.route.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that provide access to services
 * concerning route services.
 *
 * @author Kevin Haller
 */
public class RouteServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    public RouteServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(RouteResourceServiceFactory.getManagedPathName(),
            new RouteResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "route";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
