package at.ac.tuwien.finder.datamanagement.integration;

import org.apache.jena.rdf.model.Model;

/**
 * Instances of this interface represent data integrators that integrate the given model into a
 * given graph.
 *
 * @author Kevin Haller
 */
public interface DataIntegrator extends AutoCloseable {

    /**
     * Gets the name of the graph, for which this data integrator is responsible.
     *
     * @return the name of the graph, for which this data updater is responsible.
     */
    String graphName();

    /**
     * Analyzes the model and integrates it into the graph, which is managed by this data integrator.
     *
     * @param model the model, which shall be analyzed and be integrated into the corresponding
     *              graph.
     */
    void integrate(Model model);

}
