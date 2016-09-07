package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.integration.Integrator;

import java.util.Collection;

/**
 * This class represents a plan for the mediation. A mediation plan can consist of multiple
 * {@link Mediator} that acquire data from certain sources.
 *
 * @author Kevin Haller
 */
public class MediationPlan implements AutoCloseable {

    private Integrator integrator;
    private Collection<Mediator> mediators;

    /**
     * Creates a new mediation plan with the given mediators and integrator.
     *
     * @param mediators  the mediators, which shall be used in this mediation plan.
     * @param integrator the integrator that shall be used to integrate the result of the mediation
     *                   into the local triple store.
     */
    public MediationPlan(Collection<Mediator> mediators, Integrator integrator) {
        assert integrator != null;
        assert mediators != null;
        this.mediators = mediators;
        this.integrator = integrator;
    }

    /**
     * Gets the mediators of this plan.
     *
     * @return the mediators of this plan.
     */
    public Collection<Mediator> mediators() {
        return mediators;
    }

    /**
     * Gets the integrator of this plan.
     *
     * @return the integrator of this plan.
     */
    public Integrator integrator() {
        return integrator;
    }

    @Override
    public void close() throws Exception {
        if (mediators != null) {
            for (Mediator mediator : mediators) {
                mediator.close();
            }
        }
        integrator.close();
    }
}
