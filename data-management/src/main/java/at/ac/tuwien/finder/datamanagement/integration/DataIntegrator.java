package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import org.eclipse.rdf4j.model.Model;

/**
 * Instances of this interface represent data integrators that integrate the given model into a
 * given {@link DataSet}.
 *
 * @author Kevin Haller
 */
public interface DataIntegrator extends AutoCloseable {

    /**
     * Analyzes the model and integrates it into the graph, which is managed by this data integrator.
     *
     * @param model the model, which shall be analyzed and be integrated into the corresponding
     *              graph.
     */
    void integrate(Model model);

}
