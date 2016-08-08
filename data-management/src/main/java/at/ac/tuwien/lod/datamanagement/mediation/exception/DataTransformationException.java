package at.ac.tuwien.lod.datamanagement.mediation.exception;

/**
 * This class represents an exception, which indicates that the data transformation or the setup
 * was not successful.
 *
 * @author Kevin Haller
 */
public class DataTransformationException extends Exception {

    /**
     * {@inheritDoc}
     */
    public DataTransformationException() {
    }

    /**
     * {@inheritDoc}
     */
    public DataTransformationException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DataTransformationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataTransformationException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataTransformationException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
