package at.ac.tuwien.finder.service.spatial.room.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s and {@link IService}s concerning room
 * resources.
 *
 * @author Kevin Haller
 */
public class RoomResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> floorServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link RoomResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link RoomResourceServiceFactory}.
     */
    public RoomResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
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
        return floorServiceFactoryMap;
    }

    @Override
    public IService getService(URI parent, Scanner pathScanner, Map<String, String> parameter)
        throws RDFSerializableException {
        String resourceId = pathScanner.next();
        if (pathScanner.hasNext()) {
            URI newParent = parent.resolve(resourceId + "/");
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameter, "id", newParent.toString()));
        }
        return new DescribeResourceService(tripleStoreManager,
            parent.resolve(resourceId).toString());
    }
}
