package at.ac.tuwien.finder.service.spatial.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IService} that manages access to the a geometry
 * services.
 *
 * @author Kevin Haller
 */
public class GeometryServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeometryServiceFactory.class);

    private Map<String, IServiceFactory> geometryServiceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link GeometryServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link GeometryServiceFactory}.
     */
    public GeometryServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        geometryServiceFactoryMap.put("id", new GeometryResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of geometry services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", geometryServiceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "event";
    }


    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return geometryServiceFactoryMap;
    }
}
