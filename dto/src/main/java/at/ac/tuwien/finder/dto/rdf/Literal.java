package at.ac.tuwien.finder.dto.rdf;

import at.ac.tuwien.finder.dto.util.Namespaces;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 * This class represents a literal of a RDF statement.
 *
 * @author Kevin Haller
 */
public class Literal implements Object {

    private org.eclipse.rdf4j.model.Literal literal;

    public Literal(org.eclipse.rdf4j.model.Literal literal) {
        this.literal = literal;
    }

    public String getValue() {
        return literal.getLabel();
    }

    public String getLanguage() {
        return literal.getLanguage().orElse(null);
    }

    public Resource getDatatype() {
        return new IResource(literal.getDatatype());
    }

    @Override
    public String toString() {
        if (RDF.LANGSTRING.equals(literal.getDatatype())) {
            return String.format("\"%s\"@%s", getValue(), literal.getLanguage().get());
        } else if (literal.getDatatype() == null || XMLSchema.STRING
            .equals(literal.getDatatype())) {
            return "\"" + getValue() + "\"";
        } else {
            return String.format("\"%s\"^^%s", getValue(), Namespaces
                .format(literal.getDatatype().stringValue(), literal.getDatatype().getNamespace(),
                    literal.getDatatype().getLocalName()));
        }
    }
}
