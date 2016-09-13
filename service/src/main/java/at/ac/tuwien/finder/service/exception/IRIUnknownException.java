package at.ac.tuwien.finder.service.exception;

/**
 * This class represents an exception that will be thrown, if the requested IRI is unknown.
 *
 * @author Kevin Haller
 */
public class IRIUnknownException extends RDFSerializableException {

    /**
     * {@inheritDoc}
     */
    public IRIUnknownException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public IRIUnknownException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public IRIUnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public IRIUnknownException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    protected IRIUnknownException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int statusCode() {
        return 404;
    }

}
