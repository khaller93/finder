package at.ac.tuwien.finder.service.event;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning data about events.
 *
 * @author Kevin Haller
 */
public class EventServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceFactory.class);

    private Map<String, IServiceFactory> eventServiceFactoryMap = new HashMap<>();

    /**
     * Creates a new {@link EventServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link EventServiceFactory}.
     */
    public EventServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        eventServiceFactoryMap.put(EventResourceServiceFactory.getManagedPathName(),
            new EventResourceServiceFactory(tripleStoreManager));
        logger.debug("Factory map of event services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", eventServiceFactoryMap.keySet()));
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "event";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return eventServiceFactoryMap;
    }
}
