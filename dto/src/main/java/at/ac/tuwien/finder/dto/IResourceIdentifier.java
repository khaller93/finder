package at.ac.tuwien.finder.dto;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * This class represents an {@link IRI}.
 *
 * @author Kevin Haller
 * @see <a href="https://tools.ietf.org/html/rfc3987">Internationalized Resource Identifiers</a>
 */
public class IResourceIdentifier {

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
     * Creates a new instance of {@link IResourceIdentifier}.
     *
     * @param value {@link org.eclipse.rdf4j.model.IRI} that shall
     */
    private IResourceIdentifier(IRI value) {
        this.value = value;
    }

    /**
     * Resolves the given path against this IRI.
     *
     * @param path the path that shall be resolved against this IRI.
     * @return the result of resolving the given path against this IRI.
     */
    public IResourceIdentifier resolve(String path) {
        return new IResourceIdentifier(
            SimpleValueFactory.getInstance().createIRI(value.stringValue(), path));
    }

    /**
     * Gets the wrapped {@link IRI} value.
     *
     * @return the wrapped {@link IRI} value.
     */
    IRI iriValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.stringValue();
    }
}
