package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.mediation.DataAcquirer;

/**
 * This interface is a marker interface for {@link DataAcquirer}, which is gathering spatial data
 * about the Vienna University of Technology.
 *
 * @param <T> the type of the data, which is gathered by the {@link DataAcquirer}.
 */
public interface SpatialDataAcquirer<T> extends DataAcquirer<T> {

}
