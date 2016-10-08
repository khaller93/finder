package at.ac.tuwien.finder.service.spatial.floor.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.spatial.floor.service.AllFloorSectionsService;

import java.util.Map;
import java.util.Scanner;

/**
 * This is an implementation of {@link IServiceFactory} that manages access to
 * {@link AllFloorSectionsService}.
 *
 * @author Kevin Haller
 */
class FloorSectionsServiceFactory extends InternalTreeNodeServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link AllFloorSectionsService}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for the spawned
     *                           services.
     */
    FloorSectionsServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return null;
    }

    @Override
    public IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (pathScanner.hasNext()) {
            throw new IRIUnknownException(String
                .format("'%s' does not expect any further path segments. '%s' is not valid.",
                    parentIRI.rawIRI(), parentIRI.resolve(pathScanner.next()).rawIRI()));
        }
        if (!parameterMap.containsKey("id")) {
            throw new IRIInvalidException(String
                .format("There must be a floor id set for the service '%s'.", parentIRI.rawIRI()));
        }
        return new AllFloorSectionsService(tripleStoreManager,
            new IResourceIdentifier(parameterMap.get("id")), parentIRI);
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "sections";
    }
}
