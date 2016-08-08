package at.ac.tuwien.lod.datamanagement.mediation;

import at.ac.tuwien.lod.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.lod.datamanagement.mediation.exception.DataTransformationException;

/**
 * Instances of this interface represents an acquirer of data for a specific entity. The structure
 * of the data can be messy. The acquirer offers a {@link DataTransformer} to transform the
 * (potential messy) data to linked data (rdf).
 *
 * @param <R> the type of the data, which has been acquired.
 * @author Kevin Haller
 */
public interface DataAcquirer<R> extends AutoCloseable {

    /**
     * Gets the transformer for the gathered data.
     *
     * @return the transformer for the gathered data.
     * @throws DataTransformationException if the setup of the transformation was not successful.
     */
    DataTransformer<R> transformer() throws DataTransformationException;

    /**
     * Acquires the data of the specific entity as string. The {@link DataTransformer} returned from
     * the transformer() method can be used to transform the (possibly messy) data into linked data
     * (rdf).
     *
     * @return the acquired data of the specific entity as string.
     * @throws DataAcquireException if the acquiring of the data failed.
     */
    R acquire() throws DataAcquireException;

}
