package at.ac.tuwien.finder.service.exception;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 * Instances of this interface are {@link Exception}s that have an representation in form of a RDF
 * {@link Model}.
 *
 * @author Kevin Haller
 */
public abstract class RDFSerializableException extends Exception {

    /**
     * {@inheritDoc}
     */
    public RDFSerializableException() {
    }

    /**
     * {@inheritDoc}
     */
    public RDFSerializableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public RDFSerializableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public RDFSerializableException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public RDFSerializableException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Gets the {@link Model} that contains information about this exception.
     *
     * @return the {@link Model} that contains information about this exception.
     */
    public Model getModel() {
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        Model exceptionModel = new LinkedHashModel();
        IRI exceptionResource = valueFactory.createIRI(":e");
        exceptionModel.add(exceptionResource, RDF.TYPE, valueFactory.createIRI(":Exception"));
        exceptionModel.add(exceptionResource, valueFactory.createIRI(":exceptionClass"),
            valueFactory.createLiteral(this.getClass().getName()));
        if (this.getMessage() != null) {
            exceptionModel
                .add(valueFactory.createIRI(":e"), valueFactory.createIRI(":errorMessage"),
                    valueFactory.createLiteral(getMessage()));
        }
        return exceptionModel;
    }

    /**
     * Gets the HTTP status code in which this exception fall into.
     *
     * @return the HTTP status code in which this exception fall into.
     */
    public abstract int statusCode();

}
