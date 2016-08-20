package at.ac.tuwien.finder.datamanagement.integration.exception;

/**
 * This is an exception that indicates that the integration failed.
 *
 * @author Kevin Haller
 */
public class IntegrationException extends Exception {

    /**
     * {@inheritDoc}
     */
    public IntegrationException() {
    }

    /**
     * {@inheritDoc}
     */
    public IntegrationException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public IntegrationException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public IntegrationException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
