package at.ac.tuwien.lod.datamanagement.integration;

import org.apache.jena.rdf.model.Model;

/**
 * Instances of this interface represent linkers that link the entities of a given graph with
 * other entities. This links will be integrated into the corresponding graph following the
 * usual process of update, linking and cleaning.
 *
 * @author Kevin Haller
 */
public interface DataLinker extends AutoCloseable {

    /**
     * Gets the name of the graph, for which this data linker is responsible.
     *
     * @return the name of the graph, for which this data linker is responsible.
     */
    String graphName();

    /**
     * Starts linking on the graph, for which this linker is responsible.
     *
     * @return the model of results of the linking.
     */
    Model link();

}
