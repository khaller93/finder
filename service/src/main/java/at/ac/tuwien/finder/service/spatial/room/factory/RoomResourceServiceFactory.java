package at.ac.tuwien.finder.service.spatial.room.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.SimpleDescribeResourceService;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

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

    private Map<String, IServiceFactory> roomServiceFactoryMap = new HashMap<>();
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
        roomServiceFactoryMap.put(AddressOfRoomServiceFactory.getManagedPathName(),
            new AddressOfRoomServiceFactory(tripleStoreManager));
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
        return roomServiceFactoryMap;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameter) throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        if (pathScanner.hasNext()) {
            IResourceIdentifier newParent = parent.resolve(resourceId);
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameter, "id", newParent.rawIRI()));
        }
        return new SimpleDescribeResourceService(tripleStoreManager,
            parent.resolve(resourceId).rawIRI());
    }
}
