package at.ac.tuwien.finder.dto.rdf;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;

/**
 * This class represents a resource of a RDF statement, which can either be a {@link BlankNode} or
 * a {@link IResourceIdentifier}.
 *
 * @author Kevin Haller
 */
public abstract class Resource implements Object {

    /**
     * Creates either a {@link BlankNode} or an {@link IResourceIdentifier}.
     *
     * @param resource the resource that shall be wrapped.
     * @return {@link Resource} wrapping the given {@link org.eclipse.rdf4j.model.Resource}, or null
     * if the type of the resource is unknown.
     */
    public static Resource createResource(org.eclipse.rdf4j.model.Resource resource) {
        if (resource instanceof IRI) {
            return new IResourceIdentifier((IRI) resource);
        } else if (resource instanceof BNode) {
            return new BlankNode((BNode) resource);
        } else {
            return null;
        }
    }

}
