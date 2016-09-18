package at.ac.tuwien.finder.datamanagement.util.exception;

/**
 * This {@link Exception} will be thrown, if the request to the TISS API failed.
 *
 * @author Kevin Haller
 */
public class TISSApiRequestFailedException extends Exception {

    /**
     * {@inheritDoc}
     */
    public TISSApiRequestFailedException() {
    }

    /**
     * {@inheritDoc}
     */
    public TISSApiRequestFailedException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public TISSApiRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public TISSApiRequestFailedException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public TISSApiRequestFailedException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
