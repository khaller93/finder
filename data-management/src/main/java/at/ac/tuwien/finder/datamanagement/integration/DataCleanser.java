package at.ac.tuwien.finder.datamanagement.integration;

/**
 * Instances of this interface represent data cleanser that cleans the linked data of the specified
 * graph.
 *
 * @author Kevin Haller
 */
public interface DataCleanser extends AutoCloseable {

    /**
     * Gets the name of the graph, for which this data cleanser is responsible.
     *
     * @return the name of the graph, for which this data cleanser is responsible.
     */
    String graphName();


    /**
     * Cleans the graph, for which this data cleanser is responsible.
     */
    void clean();
}
