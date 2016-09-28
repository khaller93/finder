package at.ac.tuwien.finder.dto.rdf;

import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.IResourceIdentifier;
import at.ac.tuwien.finder.dto.util.Namespaces;
import org.eclipse.rdf4j.model.IRI;

/**
 * This class wraps a resource that is represented by an IRI.
 *
 * @author Kevin Haller
 */
public class IResource extends Resource {

    private IRI resourceIRI;

    /**
     * Creates a new instance of {@link IResource} that
     *
     * @param resourceIRI
     */
    public IResource(IRI resourceIRI) {
        this.resourceIRI = resourceIRI;
    }

    /**
     * Gets the {@link IResourceIdentifier} of the entity that is described by this {@link Dto}.
     *
     * @return the {@link IResourceIdentifier} of the entity that is described by this
     * {@link Dto}.
     */
    public IResourceIdentifier getIRI() {
        return new IResourceIdentifier(resourceIRI.stringValue());
    }

    @Override
    public String toString() {
        return Namespaces.format(resourceIRI.stringValue(), resourceIRI.getNamespace(),
            resourceIRI.getLocalName());
    }

}
