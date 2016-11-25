package at.ac.tuwien.finder.service.spatial.accessunit.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This service implements {@link at.ac.tuwien.finder.service.ServiceFactory} that gives access to
 * services for access units (e.g. doors).
 *
 * @auhtor Kevin Haller
 */
public class AccessUnitServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    public AccessUnitServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(AccessUnitResourceServiceFactory.getManagedPathName(),
            new AccessUnitResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "accessunit";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
