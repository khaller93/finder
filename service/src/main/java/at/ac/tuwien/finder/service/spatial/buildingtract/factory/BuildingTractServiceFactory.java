package at.ac.tuwien.finder.service.spatial.buildingtract.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning data about building tracts.
 *
 * @author Kevin Haller
 */
public class BuildingTractServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(BuildingTractServiceFactory.class);

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link BuildingTractServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link BuildingTractServiceFactory}.
     */
    public BuildingTractServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        serviceFactoryMap.put(BuildingTractResourceServiceFactory.getManagedPathName(),
            new BuildingTractResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of building tract services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", serviceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "buildingtract";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }
}
