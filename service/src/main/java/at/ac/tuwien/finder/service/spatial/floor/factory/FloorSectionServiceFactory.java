package at.ac.tuwien.finder.service.spatial.floor.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} that manages the access to services
 * concerning floor sections.
 *
 * @author Kevin Haller
 */
class FloorSectionServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(FloorSectionServiceFactory.class);

    private Map<String, IServiceFactory> floorSectionFactoryMap = new HashMap<>();

    /**
     * Creates a new instance of {@link FloorSectionServiceFactory}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for services.
     */
    FloorSectionServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        floorSectionFactoryMap.put(FloorSectionResourceServiceFactory.getManagedPathName(),
            new FloorSectionResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of floor section services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", floorSectionFactoryMap.keySet()));
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return floorSectionFactoryMap;
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "section";
    }
}
