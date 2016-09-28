package at.ac.tuwien.finder.service.organizational.person;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning data about persons.
 *
 * @author Kevin Haller
 */
public class PersonServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceFactory.class);

    private Map<String, IServiceFactory> personServiceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link PersonServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link PersonServiceFactory}.
     */
    public PersonServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        personServiceFactoryMap.put(PersonResourceServiceFactory.getManagedPathName(),
            new PersonResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of spatial services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", personServiceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "person";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return personServiceFactoryMap;
    }
}
