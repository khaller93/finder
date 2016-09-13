package at.ac.tuwien.finder.service.spatial.building.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s and {@link IService}s concerning building
 * resources.
 *
 * @author Kevin Haller
 */
class BuildingResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(BuildingServiceFactory.class);

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link BuildingResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link BuildingResourceServiceFactory}.
     */
    BuildingResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        serviceFactoryMap.put(BuildingResourceTractsServiceFactory.getManagedPathName(),
            new BuildingResourceTractsServiceFactory(tripleStoreManager));
        serviceFactoryMap.put(BuildingResourceFloorsServiceFactory.getManagedPathName(),
            new BuildingResourceFloorsServiceFactory(tripleStoreManager));
        logger.debug("Factory map of building resource services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", serviceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "id";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }

    @Override
    public IService getService(URI parent, Scanner pathScanner, Map<String, String> parameter)
        throws RDFSerializableException {
        String resourceId = pathScanner.next();
        if (pathScanner.hasNext()) {
            return super.getService(parent.resolve(resourceId + "/"), pathScanner,
                super.pushParameter(parameter, "id", parent.resolve(resourceId).toString()));
        }
        return new DescribeResourceService(tripleStoreManager,
            parent.resolve(resourceId).toString());
    }
}
