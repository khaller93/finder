package at.ac.tuwien.lod.datamanagement.integration;

import at.ac.tuwien.lod.datamanagement.integration.exception.IntegrationException;
import at.ac.tuwien.lod.taskmanagement.TaskManager;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

/**
 * This class represent an integrator with a specific {@link IntegrationPlan} that
 * integrate a given model into the specified graph.
 *
 * @author Kevin Haller
 */
public class Integrator implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(Integrator.class);

    private IntegratorManager integratorManager = IntegratorManager.getInstance();
    private TaskManager taskManager = TaskManager.getInstance();

    private IntegrationPlan integrationPlan;
    private String graphName;

    /**
     * Creates the integrator with the given graph name and integration paln.
     *
     * @param graphName       the name of the graph, into which the models shall be integrated.
     * @param integrationPlan the integration plan, which shall be used.
     */
    public Integrator(String graphName, IntegrationPlan integrationPlan) {
        this.graphName = graphName;
        this.integrationPlan = integrationPlan;
    }

    /**
     * Gets the name of the graph, for which this data updater is responsible.
     *
     * @return the name of the graph, for which this data updater is responsible.
     */
    public String graphName() {
        return this.graphName;
    }

    /**
     * Integrates the given model.
     *
     * @param model the model, which shall be integrated.
     */
    public void integrate(Model model) {
        logger.debug("integrate({})", model);
        /* Starts data updating */
        DataIntegrator dataIntegrator = integrationPlan.getDataIntegrator();
        if (dataIntegrator != null) {
            dataIntegrator.integrate(model);
        }
        /* Starts linking */
        if (integrationPlan.getDataLinkers() != null || integrationPlan.getDataLinkers()
            .isEmpty()) {
            CompletionService<Model> completionService =
                new ExecutorCompletionService<>(taskManager.threadPool());
            for (DataLinker dataLinker : integrationPlan.getDataLinkers()) {
                completionService.submit(() -> dataLinker.link());
            }
            for (int n = 0; n < integrationPlan.getDataLinkers().size(); n++) {
                try {
                    integratorManager.startIntegration(completionService.take().get());
                } catch (InterruptedException | ExecutionException | IntegrationException e) {
                    logger.error("integrate({}) throws {}", model, e);
                }
            }
        }
        /* Starts cleansing */
        DataCleanser dataCleanser = integrationPlan.getDataCleanser();
        if (dataCleanser != null) {
            dataCleanser.clean();
        }
    }

    @Override
    public void close() throws Exception {
        taskManager.close();
        if (integrationPlan != null) {
            integrationPlan.close();
        }
    }
}
