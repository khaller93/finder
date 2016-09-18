package at.ac.tuwien.finder.datamanagement.mediation.organizational;

import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.mediation.Mediator;
import at.ac.tuwien.finder.taskmanagement.TaskCloseHandler;
import at.ac.tuwien.finder.taskmanagement.TaskFailedHandler;

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
     * @param dataAcquirers  the list of data acquirers, which shall be executed.
     * @param dataIntegrator {@link DataIntegrator} for integrating the acquired information.
     */
    public OrganizationalMediator(Collection<OrganizationalDataAcquirer> dataAcquirers,
        DataIntegrator dataIntegrator) {
        super(new LinkedList<>(dataAcquirers), dataIntegrator);
    }

}
