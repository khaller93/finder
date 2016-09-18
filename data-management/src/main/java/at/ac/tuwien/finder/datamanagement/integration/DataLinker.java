package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import org.eclipse.rdf4j.model.Model;

/**
 * Instances of this interface represent linkers that link the entities of a given graph with
 * other entities. This links will be integrated into the corresponding {@link DataSet} following
 * the usual process of update, linking and cleaning.
 *
 * @author Kevin Haller
 */
public interface DataLinker extends AutoCloseable {

    /**
     * Starts linking on the graph, for which this linker is responsible.
     *
     * @return the model of results of the linking.
     */
    Model link();

}
