package at.ac.tuwien.finder.dto;

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
public class ResourceCollectionDto implements CollectionDto<Resource> {

    private Model model;
    private IRI headIRI;
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private static final IRI[] LABEL_PROPERTIES =
        new IRI[] {RDFS.LABEL, DCTERMS.TITLE, DC.TITLE, SCHEMA.name};

    private static final IRI[] COMMENT_PROPERTIES =
        new IRI[] {RDFS.COMMENT, DCTERMS.DESCRIPTION, DCTERMS.ABSTRACT, DC.DESCRIPTION,
            SCHEMA.description};

    /**
     * Creates a new instance of {@link CollectionDto}.
     *
     * @param model {@link Model} that contains the RDF collection.
     */
    public ResourceCollectionDto(IRI headIRI, Model model) {
        this.model = model;
        this.headIRI = headIRI;
    }

    @Override
    public List<Resource> asList() {
        return RDFCollections.asValues(model, headIRI, new LinkedList<>()).stream()
            .filter(value -> value instanceof org.eclipse.rdf4j.model.Resource)
            .map(value -> Resource.createResource((org.eclipse.rdf4j.model.Resource) value))
            .collect(Collectors.toList());
    }

    @Override
    public IResourceIdentifier getIRI() {
        return new IResourceIdentifier(headIRI.stringValue());
    }

    @Override
    public String getLabel(String preferredLanguageCode) {
        return RDFUtils.getFirstLiteralFor(getModel(), valueFactory.createIRI(getIRI().toString()),
            preferredLanguageCode, LABEL_PROPERTIES).orElse(null);
    }

    @Override
    public String getDescription(String preferredLanguageCode) {
        return RDFUtils.getFirstLiteralFor(getModel(), valueFactory.createIRI(getIRI().toString()),
            preferredLanguageCode, COMMENT_PROPERTIES).orElse(null);
    }

    @Override
    public Model getModel() {
        return model;
    }

}
