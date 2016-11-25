package at.ac.tuwien.finder.service.spatial.elevator.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that provides access to services
 * concerning elevators.
 *
 * @author Kevin Haller
 */
public class ElevatorServiceFactory extends InternalTreeNodeServiceFactory {
    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    public ElevatorServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(ElevatorResourceServiceFactory.getManagedPathName(),
            new ElevatorResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "elevator";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
