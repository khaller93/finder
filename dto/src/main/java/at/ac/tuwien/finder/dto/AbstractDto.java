package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.util.RDFUtils;
import at.ac.tuwien.finder.vocabulary.SCHEMA;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.StringWriter;

/**
 * This abstract class is an implementation of {@link Dto} that implements the generic methods
 * like getting the label of a resource or the description.
 *
 * @æuthor Kevin Haller
 */
public abstract class AbstractDto implements Dto {

    /**
     * ******************************************************************************************
     * Resource identifier
     * ******************************************************************************************
     */
    private IResourceIdentifier resourceIdentifier;

    @Override
    public Resource id() {
        return resourceIdentifier.iriValue();
    }

    @Override
    public void id(Resource theResource) {
        this.resourceIdentifier = new IResourceIdentifier(theResource.stringValue());
    }

    @Override
    public IResourceIdentifier getIRI() {
        return resourceIdentifier;
    }

    /**
     * ******************************************************************************************
     * Memento - Model
     * ******************************************************************************************
     */
    private Model model;

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public String transformTo(SerializationFormat format) throws IOException {
        try (StringWriter stringWriter = new StringWriter()) {
            Rio.write(getModel(), stringWriter, format.getRDFFormat());
            return stringWriter.toString();
        }
    }

    /**
     * ******************************************************************************************
     * Label, Description, Type
     * ******************************************************************************************
     */
    private ValueFactory valueFactory = SimpleValueFactory.getInstance();

    private static final IRI[] LABEL_PROPERTIES =
        new IRI[] {RDFS.LABEL, DCTERMS.TITLE, DC.TITLE, SCHEMA.name};

    private static final IRI[] COMMENT_PROPERTIES =
        new IRI[] {RDFS.COMMENT, DCTERMS.DESCRIPTION, DCTERMS.ABSTRACT, DC.DESCRIPTION,
            SCHEMA.description};

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

}
