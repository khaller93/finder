package at.ac.tuwien.finder.service.spatial.building.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning data about buildings.
 *
 * @author Kevin Haller
 */
public class BuildingServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(BuildingServiceFactory.class);

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link BuildingServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link BuildingServiceFactory}.
     */
    public BuildingServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(BuildingResourceServiceFactory.getManagedPathName(),
            new BuildingResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of building services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", serviceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "building";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
