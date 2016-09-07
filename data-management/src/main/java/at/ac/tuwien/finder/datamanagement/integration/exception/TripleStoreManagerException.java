package at.ac.tuwien.finder.datamanagement.integration.exception;

/**
 * Created by haller on 27.08.16.
 */
public class TripleStoreManagerException extends Exception {
    public TripleStoreManagerException() {
    }

    public TripleStoreManagerException(String message) {
        super(message);
    }

    public TripleStoreManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TripleStoreManagerException(Throwable cause) {
        super(cause);
    }

    public TripleStoreManagerException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
