package at.ac.tuwien.lod.datamanagement.mediation.exception;

/**
 * This exception shall be thrown, if an error occurred during the mediation.
 *
 * @author Kevin Haller
 */
public class MediatorException extends Exception {

    /**
     * {@inheritDoc}
     */
    public MediatorException() {
    }

    /**
     * {@inheritDoc}
     */
    public MediatorException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public MediatorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public MediatorException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public MediatorException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
