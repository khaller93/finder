package at.ac.tuwien.finder.service.exception;

/**
 * This class represents an exception that will be thrown, if an IRI is not valid and has violations.
 *
 * @author Kevin Haller
 */
public class IRIInvalidException extends RDFSerializableException {

    /**
     * {@inheritDoc}
     */
    public IRIInvalidException() {
    }

    /**
     * {@inheritDoc}
     */
    public IRIInvalidException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public IRIInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public IRIInvalidException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public IRIInvalidException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
