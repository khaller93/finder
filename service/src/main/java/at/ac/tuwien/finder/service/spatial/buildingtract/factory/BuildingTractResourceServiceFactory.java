package at.ac.tuwien.finder.service.spatial.buildingtract.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.*;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s and {@link IService}s concerning building tract
 * resources.
 *
 * @author Kevin Haller
 */
class BuildingTractResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> buildingTractServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link BuildingTractResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link BuildingTractResourceServiceFactory}.
     */
    BuildingTractResourceServiceFactory(TripleStoreManager tripleStoreManager) {
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
        return buildingTractServiceFactoryMap;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner, Map<String, String> parameter)
        throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        IResourceIdentifier newParent = parent.resolve(resourceId);
        if (pathScanner.hasNext()) {
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameter, "id", newParent.rawIRI()));
        }
        return new SimpleDescribeResourceService(tripleStoreManager, newParent.rawIRI());
    }
}
