package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.catalog.factory.CatalogServiceFactory;
import at.ac.tuwien.finder.service.event.EventServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.service.organizational.OrganizationalServiceFactory;
import at.ac.tuwien.finder.service.spatial.SpatialServiceFactory;
import at.ac.tuwien.finder.service.vocabulary.VocabularyServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that manages the root of the path
 * tree.
 *
 * @author Kevin Haller
 */
public class ServiceFactory extends InternalTreeNodeServiceFactory implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    private TripleStoreManager tripleStoreManager;
    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link ServiceFactory}.
     *
     * @throws ServiceException if this service factory cannot be established.
     */
    public ServiceFactory() throws ServiceException {
        try {
            this.tripleStoreManager = TripleStoreManager.getInstanceOrThrowException();
            setupServiceFactoryMap();
        } catch (TripleStoreManagerException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Creates a new {@link ServiceFactory} that shall use the given
     *
     * @param tripleStoreManager that shall be used for this service (e.g. for unit-testing).
     */
    public ServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        setupServiceFactoryMap();
    }

    /**
     * Sets-up the factory map for the services.
     */
    private void setupServiceFactoryMap() {
        serviceFactoryMap.put(SpatialServiceFactory.getManagedPathName(),
            new SpatialServiceFactory(tripleStoreManager));
        serviceFactoryMap.put(OrganizationalServiceFactory.getManagedPathName(),
            new OrganizationalServiceFactory(tripleStoreManager));
        serviceFactoryMap.put(EventServiceFactory.getManagedPathName(),
            new EventServiceFactory(tripleStoreManager));
        serviceFactoryMap
            .put(VocabularyServiceFactory.getManagedPathName(), new VocabularyServiceFactory());
        serviceFactoryMap.put(CatalogServiceFactory.getManagedPathName(),
            new CatalogServiceFactory(tripleStoreManager));
        logger.debug("Factory map of services initialized: ../{}",
            String.join(", ../ ", serviceFactoryMap.keySet()));
    }

    /**
     * Gets the {@link IService} that is responsible for handling the requested IRI, if the IRI is
     * valid, otherwise a {@link IRIUnknownException} will be thrown. The given parent path is the
     * part of the IRI that has already been parsed and the given {@link Scanner} points to the next
     * path segment. The base IRI before the path will be {@code TripleStoreManager.BASE_NAMED_GRAPH}
     * per default.
     *
     * @param pathScanner  the scanner that is pointing to the next path segment.
     * @param parameterMap the parameter map that contains given parameters.
     * @return the {@link IService} that is responsible for handling the requested URI
     * @throws IRIUnknownException if no service is assigned to the given IRI.
     * @throws IRIInvalidException if the given IRI is not valid.
     */
    public IService getService(Scanner pathScanner, Map<String, String> parameterMap)
        throws IRIInvalidException, IRIUnknownException {
        return super
            .getService(new IResourceIdentifier(TripleStoreManager.BASE.stringValue()), pathScanner,
                parameterMap);
    }

    /**
     * Gets the {@link IService} that is responsible for handling the requested IRI, if the IRI is
     * valid, otherwise a {@link IRIUnknownException} will be thrown. The given parent path is the
     * part of the IRI that has already been parsed and the given {@link Scanner} points to the next
     * path segment. The base IRI before the path will be {@code TripleStoreManager.BASE_NAMED_GRAPH}
     * per default.
     *
     * @param pathScanner the scanner that is pointing to the next path segment.
     * @return the {@link IService} that is responsible for handling the requested URI
     * @throws IRIUnknownException if no service is assigned to the given IRI.
     * @throws IRIInvalidException if the given IRI is not valid.
     */
    public IService getService(Scanner pathScanner)
        throws IRIInvalidException, IRIUnknownException {
        return super
            .getService(new IResourceIdentifier(TripleStoreManager.BASE.stringValue()), pathScanner,
                null);
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }

    @Override
    public void close() throws Exception {
        tripleStoreManager.close();
    }

}
