package at.ac.tuwien.finder.dto.rdf;

import org.eclipse.rdf4j.model.BNode;

/**
 * This class represents a RDF blank node.
 *
 * @author Kevin Haller
 */
public class BlankNode extends Resource {

    private BNode blankNode;

    /**
     * Creates a new {@link BNode} wrapping the given {@link BNode}.
     *
     * @param blankNode {@link BNode} that shall be wrapped into this
     *                  {@link at.ac.tuwien.finder.dto.Dto}.
     */
    public BlankNode(BNode blankNode) {
        assert blankNode != null;
        this.blankNode = blankNode;
    }


    public String getIdentifier() {
        return blankNode.getID();
    }

    @Override
    public String toString() {
        return "_:" + this.getIdentifier();
    }
}
