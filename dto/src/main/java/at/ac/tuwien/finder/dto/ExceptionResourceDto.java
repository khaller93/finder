package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

/**
 * This class wraps an exception.
 *
 * @author Kevin Haller
 */
public class ExceptionResourceDto extends AbstractResourceDto {

    /**
     * Creates a new instance of {@link ExceptionResourceDto}.
     *
     * @param resourceIRI the resource {@link IRI} of the {@link Exception}.
     * @param model       {@link Model} that describes the exception.
     */
    public ExceptionResourceDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }

    /**
     * Gets an instance of a {@link ExceptionResourceDto} for the given {@link Exception}.
     *
     * @param exception {@link Exception} for which a {@link Dto} shall be created.
     * @return an instance of a {@link ExceptionResourceDto} for the given {@link Exception}.
     */
    public static ExceptionResourceDto getInstance(Exception exception) {
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        IRI exceptionIRI =
            valueFactory.createIRI("error://" + exception.getClass().getSimpleName());
        Model exceptionModel = new LinkedHashModel();
        exceptionModel.add(exceptionIRI, RDF.TYPE, valueFactory.createIRI("error://Exception"));
        exceptionModel.add(exceptionIRI, RDFS.LABEL,
            valueFactory.createLiteral(exception.getClass().getSimpleName()));
        if (exception.getMessage() != null) {
            exceptionModel.add(exceptionIRI, DCTERMS.DESCRIPTION,
                valueFactory.createLiteral(exception.getMessage()));
        }
        return new ExceptionResourceDto(new IResourceIdentifier(exceptionIRI.stringValue()),
            exceptionModel);
    }
}
