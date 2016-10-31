package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.rdf.Resource;
import at.ac.tuwien.finder.dto.util.RDFUtils;
import at.ac.tuwien.finder.vocabulary.SCHEMA;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a simple implementation of {@link CollectionDto} containing simple resources.
 *
 * @author Kevin Haller
 */
public class ResourceCollectionDto extends AbstractDto implements CollectionDto<Resource> {

    /**
     * Creates a new instance of {@link CollectionDto}.
     *
     * @param model {@link Model} that contains the RDF collection.
     */
    public ResourceCollectionDto(IRI headIRI, Model model) {
        assert headIRI != null;
        assert model != null;
        super.id(headIRI);
        super.setModel(model);
    }

    @Override
    public List<Resource> asList() {
        return RDFCollections.asValues(getModel(), id(), new LinkedList<>()).stream()
            .filter(value -> value instanceof org.eclipse.rdf4j.model.Resource)
            .map(value -> Resource.createResource((org.eclipse.rdf4j.model.Resource) value))
            .collect(Collectors.toList());
    }
}
