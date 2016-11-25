package at.ac.tuwien.finder.service.spatial.address.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning data about addresses.
 *
 * @author Kevin Haller
 */
public class AddressServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link AddressServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link AddressServiceFactory}.
     */
    public AddressServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(AddressResourceServiceFactory.getManagedPathName(),
            new AddressResourceServiceFactory(tripleStoreManager));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "address";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
