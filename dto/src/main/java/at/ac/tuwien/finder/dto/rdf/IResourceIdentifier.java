package at.ac.tuwien.finder.dto.rdf;

import at.ac.tuwien.finder.dto.util.Namespaces;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * This class represents an {@link IRI}.
 *
 * @author Kevin Haller
 * @see <a href="https://tools.ietf.org/html/rfc3987">Internationalized Resource Identifiers</a>
 */
public class IResourceIdentifier extends Resource {

    private IRI value;

    /**
     * Creates a new instance of {@link IResourceIdentifier} from the given string.
     *
     * @param iriValue the string from which a {@link IResourceIdentifier} shall be
     *                 created.
     */
    public IResourceIdentifier(String iriValue) {
        this.value = SimpleValueFactory.getInstance().createIRI(iriValue);
    }

    /**
     * Creates a new instance of {@link IResourceIdentifier} from the given string.
     *
     * @param iri {@link IRI} from which a {@link IResourceIdentifier} shall be
     *            created.
     */
    public IResourceIdentifier(IRI iri) {
        this.value = iri;
    }

    /**
     * Resolves the given path against this IRI.
     *
     * @param path the path that shall be resolved against this IRI.
     * @return the result of resolving the given path against this IRI.
     */
    public IResourceIdentifier resolve(String path) {
        return new IResourceIdentifier(SimpleValueFactory.getInstance()
            .createIRI(value.stringValue().replaceAll("(/)+$", "") + "/",
                path.replaceAll("^(/)*", "")));
    }

    /**
     * Gets the wrapped {@link IRI} value.
     *
     * @return the wrapped {@link IRI} value.
     */
    public IRI iriValue() {
        return value;
    }

    /**
     * Gets the raw {@link IRI} string of this {@link IResourceIdentifier}.
     *
     * @return the raw {@link IRI} string of this {@link IResourceIdentifier}.
     */
    public String rawIRI() {
        return value.stringValue();
    }

    @Override
    public String toString() {
        return Namespaces.format(value.stringValue(), value.getNamespace(), value.getLocalName());
    }
}
