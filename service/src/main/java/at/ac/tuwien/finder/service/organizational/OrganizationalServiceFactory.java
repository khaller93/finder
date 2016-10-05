package at.ac.tuwien.finder.service.organizational;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.OrganizationalDataSet;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.GraphDatasetService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.organizational.person.PersonServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning organizational data.
 *
 * @author Kevin Haller
 */
public class OrganizationalServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger =
        LoggerFactory.getLogger(OrganizationalServiceFactory.class);

    private Map<String, IServiceFactory> organizationalServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new {@link OrganizationalServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link OrganizationalServiceFactory}.
     */
    public OrganizationalServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        organizationalServiceFactoryMap.put(PersonServiceFactory.getManagedPathName(),
            new PersonServiceFactory(tripleStoreManager));
        logger.debug("Factory map of organizational services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", organizationalServiceFactoryMap.keySet()));
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            return new GraphDatasetService(tripleStoreManager,
                OrganizationalDataSet.NS.stringValue());
        }
        return super.getService(parent, pathScanner, parameterMap);
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "organizational";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return organizationalServiceFactoryMap;
    }
}
