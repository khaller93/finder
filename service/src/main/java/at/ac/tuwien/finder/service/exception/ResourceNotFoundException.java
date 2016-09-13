package at.ac.tuwien.finder.service.exception;

/**
 * This class represents a exception that will be thrown, if a requested resource cannot be found.
 *
 * @author Kevin Haller
 */
public class ResourceNotFoundException extends RDFSerializableException {

    private String resourceUri;

    /**
     * {@inheritDoc}
     *
     * @param resourceUri the URI for which the resource cannot be found in the knowledge base.
     */
    public ResourceNotFoundException(String resourceUri) {
        super();
        this.resourceUri = resourceUri;
    }

    /**
     * {@inheritDoc}
     *
     * @param resourceUri the URI for which the resource cannot be found in the knowledge base.
     */
    public ResourceNotFoundException(String message, String resourceUri) {
        super(message);
        this.resourceUri = resourceUri;
    }

    /**
     * {@inheritDoc}
     *
     * @param resourceUri the URI for which the resource cannot be found in the knowledge base.
     */
    public ResourceNotFoundException(String message, Throwable cause, String resourceUri) {
        super(message, cause);
        this.resourceUri = resourceUri;
    }

    /**
     * {@inheritDoc}
     *
     * @param resourceUri the URI for which the resource cannot be found in the knowledge base.
     */
    public ResourceNotFoundException(Throwable cause, String resourceUri) {
        super(cause);
        this.resourceUri = resourceUri;
    }

    /**
     * {@inheritDoc}
     *
     * @param resourceUri the URI for which the resource cannot be found in the knowledge base.
     */
    public ResourceNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace, String resourceUri) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.resourceUri = resourceUri;
    }

    /**
     * Gets the URI for which the resource cannot be found in the knowledge base.
     *
     * @return the URI for which the resource cannot be found in the knowledge base.
     */
    public String getResourceUri() {
        return resourceUri;
    }


    @Override
    public int statusCode() {
        return 404;
    }
}
