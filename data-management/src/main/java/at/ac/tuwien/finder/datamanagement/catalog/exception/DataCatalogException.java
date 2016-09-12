package at.ac.tuwien.finder.datamanagement.catalog.exception;

/**
 * This exception shall be thrown, if the data catalog cannot be managed/accessed correctly.
 *
 * @author Kevin Haller
 */
public class DataCatalogException extends Exception {

    /**
     * {@inheritDoc}
     */
    public DataCatalogException() {
    }

    /**
     * {@inheritDoc}
     */
    public DataCatalogException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DataCatalogException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataCatalogException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public DataCatalogException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
