package at.ac.tuwien.finder.service.spatial.geometry.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleResourceDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.spatial.LocationPointDto;
import at.ac.tuwien.finder.dto.spatial.PolygonShapeDto;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.RDFMapper;
import org.outofbits.opinto.RDFMappingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IService} that manages access to the a geometry
 * resource and services based on the resource.
 *
 * @author Kevin Haller
 */
class GeometryResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> geometryServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link GeometryResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link GeometryResourceServiceFactory}.
     */
    GeometryResourceServiceFactory(TripleStoreManager tripleStoreManager) {
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
        return geometryServiceFactoryMap;
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
        return new DescribeResourceService(tripleStoreManager,
            parent.resolve(resourceId).rawIRI()) {
            @Override
            public Dto wrapResult(Model model) throws ServiceException {
                try {
                    if (resourceId.startsWith("point:")) {
                        return RDFMapper.create().readValue(model, LocationPointDto.class,
                            resourceIdentifier().iriValue());
                    } else if (resourceId.startsWith("polygon:")) {
                        return RDFMapper.create().readValue(model, PolygonShapeDto.class,
                            resourceIdentifier().iriValue());
                    }
                } catch (RDFMappingException r) {
                    throw new ServiceException(
                        String.format("Resource <%s> could not be mapped.", parent.rawIRI()), r);
                } return new SimpleResourceDto(resourceIdentifier(), model);
            }
        };
    }
}
