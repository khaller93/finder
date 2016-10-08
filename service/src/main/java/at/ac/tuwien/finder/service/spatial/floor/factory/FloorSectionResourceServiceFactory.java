package at.ac.tuwien.finder.service.spatial.floor.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.SimpleDescribeResourceService;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that manages access to a floor
 * section resource.
 *
 * @author Kevin Haller
 */
class FloorSectionResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link FloorSectionResourceServiceFactory}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for services.
     */
    FloorSectionResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return Collections.emptyMap();
    }

    @Override
    public IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        if (pathScanner.hasNext()) {
            IResourceIdentifier newParent = parentIRI.resolve(resourceId + "/");
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameterMap, "id", newParent.toString()));
        }
        return new SimpleDescribeResourceService(tripleStoreManager,
            parentIRI.resolve(resourceId).rawIRI());
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "id";
    }
}
