package at.ac.tuwien.finder.dto.rdf;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a property of a RDF resource that can have one or multiple objects.
 *
 * @æuthor Kevin Haller
 */
public class Property extends IResource {

    private List<Object> values;

    public Property(IRI resourceIRI, Collection<Value> values) {
        super(resourceIRI);
        System.out.println(String.format("%s: %s", resourceIRI, values));
        this.values = new LinkedList<>();
        for (Value value : values) {
            if (value instanceof org.eclipse.rdf4j.model.Literal) {
                this.values.add(new Literal((org.eclipse.rdf4j.model.Literal) value));
            } else if (value instanceof org.eclipse.rdf4j.model.Resource) {
                this.values.add(Resource.createResource((org.eclipse.rdf4j.model.Resource) value));
            }
        }
    }

    public List<Object> getValues() {
        return values;
    }

}
