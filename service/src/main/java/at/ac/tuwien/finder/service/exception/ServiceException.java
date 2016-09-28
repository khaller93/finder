package at.ac.tuwien.finder.service.exception;


/**
 * This class represents an exception that shall be thrown, if an error occurred during the
 * execution of a {@link at.ac.tuwien.finder.service.IService}.
 *
 * @author Kevin Haller
 */
public class ServiceException extends Exception {

    /**
     * {@inheritDoc}
     */
    public ServiceException() {
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public ServiceException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
