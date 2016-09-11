package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.mediation.Mediator;

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
     * @param dataAcquirers the list of data acquirers, which shall be executed.
     */
    public SpatialMediator(Collection<SpatialDataAcquirer> dataAcquirers) {
        super(new LinkedList<>(dataAcquirers));
    }

    @Override
    public String graphName() {
        return SpatialDataSet.NS.stringValue();
    }

}
