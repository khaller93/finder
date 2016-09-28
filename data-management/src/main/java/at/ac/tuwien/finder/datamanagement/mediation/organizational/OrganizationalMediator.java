package at.ac.tuwien.finder.datamanagement.mediation.organizational;

import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.mediation.Mediator;
import at.ac.tuwien.finder.datamanagement.util.TaskManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This is an implementation of {@link Mediator} that gathers organizational data about Vienna
 * University of Technology.
 *
 * @author Kevin Haller
 */
public class OrganizationalMediator extends Mediator {

    /**
     * Creates a new organizational mediator, which gathers data with the given list of
     * {@link OrganizationalDataAcquirer}.
     *
     * @param taskManager    {@link TaskManager} that shall be used for the organizational mediator.
     * @param dataAcquirers  the list of data acquirers, which shall be executed.
     * @param dataIntegrator {@link DataIntegrator} for integrating the acquired information.
     */
    public OrganizationalMediator(TaskManager taskManager, DataIntegrator dataIntegrator,
        Collection<OrganizationalDataAcquirer> dataAcquirers) {
        super(taskManager, new LinkedList<>(dataAcquirers), dataIntegrator);
    }

    /**
     * Creates a new organizational mediator, which gathers data with the given list of
     * {@link OrganizationalDataAcquirer}.
     *
     * @param taskManager    {@link TaskManager} that shall be used for the organizational mediator.
     * @param dataAcquirers  the list of data acquirers, which shall be executed.
     * @param dataIntegrator {@link DataIntegrator} for integrating the acquired information.
     */
    public OrganizationalMediator(TaskManager taskManager, DataIntegrator dataIntegrator,
        OrganizationalDataAcquirer... dataAcquirers) {
        super(taskManager, Arrays.asList(dataAcquirers), dataIntegrator);
    }

}
