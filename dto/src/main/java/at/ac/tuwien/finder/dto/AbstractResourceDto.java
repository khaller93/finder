package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.rdf.Property;
import at.ac.tuwien.finder.dto.util.RDFUtils;
import at.ac.tuwien.finder.vocabulary.SCHEMA;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This abstract class is an implementation of a {@link Dto} that describes a entity.
 *
 * @author Kevin Haller
 */
public abstract class AbstractResourceDto implements ResourceDto {

    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private IResourceIdentifier resourceIRI;
    private Model model;

    private static final IRI[] LABEL_PROPERTIES =
        new IRI[] {RDFS.LABEL, DCTERMS.TITLE, DC.TITLE, SCHEMA.name};

    private static final IRI[] COMMENT_PROPERTIES =
        new IRI[] {RDFS.COMMENT, DCTERMS.DESCRIPTION, DCTERMS.ABSTRACT, DC.DESCRIPTION,
            SCHEMA.description};

    /**
     * Creates an instance of {@link AbstractResourceDto} with the {@link URI} describing the entity,
     * and {@link Model} that contains all statements.
     *
     * @param resourceIRI {@link IResourceIdentifier} identifying the resource that is described by
     *                    this dto.
     * @param model       {@link Model} that contains all statements describing the entity.
     */
    public AbstractResourceDto(IResourceIdentifier resourceIRI, Model model) {
        assert resourceIRI != null;
        assert model != null;
        this.resourceIRI = resourceIRI;
        this.model = model;
    }

    @Override
    public IResourceIdentifier getIRI() {
        return resourceIRI;
    }

    @Override
    public String getLabel(String preferredLanguageCode) {
        return RDFUtils.getFirstLiteralFor(getModel(), valueFactory.createIRI(getIRI().rawIRI()),
            preferredLanguageCode, LABEL_PROPERTIES).orElse(null);
    }

    @Override
    public String getDescription(String preferredLanguageCode) {
        return RDFUtils.getFirstLiteralFor(getModel(), valueFactory.createIRI(getIRI().rawIRI()),
            preferredLanguageCode, COMMENT_PROPERTIES).orElse(null);
    }

    @Override
    public Set<Property> getProperties() {
        return model.filter(valueFactory.createIRI(getIRI().rawIRI()), null, null).predicates()
            .stream().map(predicate -> new Property(predicate,
                model.filter(valueFactory.createIRI(getIRI().rawIRI()), predicate, null)
                    .objects())).collect(Collectors.toSet());
    }

    @Override
    public Set<at.ac.tuwien.finder.dto.rdf.Resource> getTypes() {
        return model.filter(valueFactory.createIRI(getIRI().rawIRI()), RDF.TYPE, null).objects()
            .stream().filter(value -> value instanceof Resource)
            .map(value -> at.ac.tuwien.finder.dto.rdf.Resource.createResource((Resource) value))
            .collect(Collectors.toSet());
    }

    @Override
    public Model getModel() {
        return model;
    }
}
