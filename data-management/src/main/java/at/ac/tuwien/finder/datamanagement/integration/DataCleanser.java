package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;

/**
 * Instances of this interface represent data cleanser that cleans the linked data of a specific
 * {@link DataSet} or globally.
 *
 * @author Kevin Haller
 */
public interface DataCleanser extends AutoCloseable {

    /**
     * Cleans the graph, for which this data cleanser is responsible.
     */
    void clean();
}
