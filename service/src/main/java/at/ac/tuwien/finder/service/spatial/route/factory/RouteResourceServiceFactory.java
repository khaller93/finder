package at.ac.tuwien.finder.service.spatial.route.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.spatial.RouteDto;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.service.spatial.room.factory.RoomResourceServiceFactory;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.RDFMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} that
 * provides access to services for a specific route resource.
 *
 * @author Kevin Haller
 */
class RouteResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link RoomResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link RoomResourceServiceFactory}.
     */
    RouteResourceServiceFactory(TripleStoreManager tripleStoreManager) {
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
        return Collections.emptyMap();
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameter) throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        IResourceIdentifier newParent = parent.resolve(resourceId);
        if (pathScanner.hasNext()) {
            return super.getService(parent, pathScanner,
                super.pushParameter(parameter, "id", newParent.rawIRI()));
        }
        return new DescribeResourceService(tripleStoreManager, newParent.rawIRI()) {

            @Override
            protected Dto wrapResult(Model model) throws ServiceException {
                return RDFMapper.create().readValue(model, RouteDto.class, newParent.iriValue());
            }
        };
    }
}
