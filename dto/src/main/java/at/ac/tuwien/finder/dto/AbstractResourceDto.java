package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.Property;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This abstract class is an implementation of a {@link Dto} that describes a entity.
 *
 * @author Kevin Haller
 */
public abstract class AbstractResourceDto extends AbstractDto implements ResourceDto {

    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    @Override
    public Set<Property> getProperties() {
        return getModel().filter(valueFactory.createIRI(getIRI().rawIRI()), null, null).predicates()
            .stream().map(predicate -> new Property(predicate,
                getModel().filter(valueFactory.createIRI(getIRI().rawIRI()), predicate, null)
                    .objects())).collect(Collectors.toSet());
    }

    @Override
    public Set<at.ac.tuwien.finder.dto.rdf.Resource> getTypes() {
        return getModel().filter(valueFactory.createIRI(getIRI().rawIRI()), RDF.TYPE, null)
            .objects().stream().filter(value -> value instanceof Resource)
            .map(value -> at.ac.tuwien.finder.dto.rdf.Resource.createResource((Resource) value))
            .collect(Collectors.toSet());
    }
}
