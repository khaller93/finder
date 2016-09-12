package at.ac.tuwien.finder.datamanagement.catalog.dataset.factory.exception;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import org.eclipse.rdf4j.model.IRI;

/**
 * This exception will be thrown, if the corresponding {@link DataSet} to a given namespace
 * {@link IRI} cannot be created.
 *
 * @author Kevin Haller
 */
public class DataSetFactoryException extends Exception {

    /**
     * {@inheritDoc}
     */
    public DataSetFactoryException() {
    }

    /**
     * {@inheritDoc}
     */
    public DataSetFactoryException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DataSetFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataSetFactoryException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataSetFactoryException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
