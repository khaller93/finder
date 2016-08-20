package at.ac.tuwien.finder.vocabulary.exception;

/**
 * This class is an exception, that shall be thrown, if the ontology can not be accessed.
 *
 * @author Kevin Haller
 */
public class OntologyAccessException extends Exception {

    /**
     * {@inheritDoc}
     */
    public OntologyAccessException() {
    }

    /**
     * {@inheritDoc}
     */
    public OntologyAccessException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public OntologyAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public OntologyAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public OntologyAccessException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
