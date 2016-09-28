package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.mediation.Mediator;
import at.ac.tuwien.finder.datamanagement.util.TaskManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This is an implementation of {@link Mediator} that gathers spatial data about Vienna University
 * of Technology.
 *
 * @author Kevin Haller
 */
public class SpatialMediator extends Mediator {

    /**
     * Creates a new facility mediator, whch gathers data with the given list of
     * {@link SpatialDataAcquirer}.
     *
     * @param taskManager    {@link TaskManager} that shall be used for the mediation.
     * @param dataIntegrator the {@link DataIntegrator} for the integrating the acquired data.
     * @param dataAcquirers  the list of data acquirers, which shall be executed.
     */
    public SpatialMediator(TaskManager taskManager, DataIntegrator dataIntegrator,
        Collection<SpatialDataAcquirer> dataAcquirers) {
        super(taskManager, new LinkedList<>(dataAcquirers), dataIntegrator);
    }

    /**
     * Creates a new facility mediator, whch gathers data with the given list of
     * {@link SpatialDataAcquirer}.
     *
     * @param taskManager    {@link TaskManager} that shall be used for the mediation.
     * @param dataAcquirers  the list of data acquirers, which shall be executed.
     * @param dataIntegrator the {@link DataIntegrator} for the integrating the acquired data.
     */
    public SpatialMediator(TaskManager taskManager, DataIntegrator dataIntegrator,
        SpatialDataAcquirer... dataAcquirers) {
        super(taskManager, Arrays.asList(dataAcquirers), dataIntegrator);
    }

}
