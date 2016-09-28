package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents a plan for the mediation. A mediation plan can consist of multiple
 * {@link Mediator} that acquire data from certain sources and integrate it into a given
 * {@link DataSet}.
 *
 * @author Kevin Haller
 */
public class MediationPlan {

    private Collection<Mediator> mediators;

    /**
     * Creates a new mediation plan with the given mediators and integrator.
     *
     * @param mediators {@link Mediator}s that shall be used in this mediation plan.
     */
    public MediationPlan(Collection<Mediator> mediators) {
        assert mediators != null;
        this.mediators = mediators;
    }

    /**
     * Creates a new mediation plan with the given mediators and integrator.
     *
     * @param mediators {@link Mediator}s that shall be used in this mediation plan.
     */
    public MediationPlan(Mediator... mediators) {
        assert mediators != null;
        this.mediators = Arrays.asList(mediators);
    }

    /**
     * Gets the mediators of this plan.
     *
     * @return the mediators of this plan.
     */
    public Collection<Mediator> mediators() {
        return mediators;
    }
}
