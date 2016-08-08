package at.ac.tuwien.lod.datamanagement.mediation.exception;

/**
 * This class represents an exception that will be thrown if the acquiring of the data was not
 * successful.
 *
 * @autor Kevin Haller
 */
public class DataAcquireException extends Exception {

    /**
     * {@inheritDoc}
     */
    public DataAcquireException() {
    }

    /**
     * {@inheritDoc}
     */
    public DataAcquireException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DataAcquireException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataAcquireException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataAcquireException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
