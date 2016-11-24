package at.ac.tuwien.finder.service.spatial.floor.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.spatial.FloorDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.*;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.RDFMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s and {@link IService}s concerning floor
 * resources.
 *
 * @author Kevin Haller
 */
class FloorResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(FloorSectionsServiceFactory.class);

    private Map<String, IServiceFactory> floorServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link FloorResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link FloorResourceServiceFactory}.
     */
    FloorResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        floorServiceFactoryMap.put(FloorSectionServiceFactory.getManagedPathName(),
            new FloorSectionServiceFactory(tripleStoreManager));
        floorServiceFactoryMap.put(FloorSectionsServiceFactory.getManagedPathName(),
            new FloorSectionsServiceFactory(tripleStoreManager));
        logger.debug("Factory map of floor resource services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", floorServiceFactoryMap.keySet()));
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
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameter) throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        if (pathScanner.hasNext()) {
            IResourceIdentifier newParent = parent.resolve("/" + resourceId);
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameter, "id", parent.resolve(resourceId).rawIRI()));
        }
        return new DescribeResourceService(tripleStoreManager,
            parent.resolve(resourceId).rawIRI()) {

            @Override
            public String getQuery() {
                return String
                    .format("DESCRIBE <%s> ?geometry WHERE {OPTIONAL { <%s> <%s> ?geometry .}}",
                        resourceIdentifier().rawIRI(), resourceIdentifier().rawIRI(),
                        GeoSPARQL.hasGeometry);
            }

            @Override
            public Dto wrapResult(Model model) throws ServiceException {
                return RDFMapper.create()
                    .readValue(model, FloorDto.class, resourceIdentifier().iriValue());
            }
        };
    }
}
