package at.ac.tuwien.finder.service.spatial.room.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.spatial.room.service.RoomAddressService;

import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

/**
 * This is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} that provides
 * access to the {@link RoomAddressService}.
 *
 * @author Kevin Haller
 */
public class AddressOfRoomServiceFactory extends InternalTreeNodeServiceFactory {

    private TripleStoreManager tripleStoreManager;

    public AddressOfRoomServiceFactory(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return Collections.emptyMap();
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
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (pathScanner.hasNext()) {
            return super.getService(parent, pathScanner, parameterMap);
        }
        if (!parameterMap.containsKey("id")) {
            throw new IRIInvalidException("The id of the room must be given, but is not.");
        }
        return new RoomAddressService(new IResourceIdentifier(parameterMap.get("id")), parent,
            tripleStoreManager);

    }
}
