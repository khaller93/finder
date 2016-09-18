package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.taskmanagement.TaskManager;
import org.eclipse.rdf4j.model.Model;
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
public class ComplexDataIntegrator implements DataIntegrator {

    private static final Logger logger = LoggerFactory.getLogger(ComplexDataIntegrator.class);

    private TaskManager taskManager = TaskManager.getInstance();

    private IntegrationPlan integrationPlan;

    /**
     * Creates the integrator with the given graph name and integration paln.
     *
     * @param integrationPlan the integration plan, which shall be used.
     */
    public ComplexDataIntegrator(IntegrationPlan integrationPlan) {
        this.integrationPlan = integrationPlan;
    }

    /**
     * Integrates the given model.
     *
     * @param model the model, which shall be integrated.
     */
    @Override
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
                completionService.submit(dataLinker::link);
            }
            for (int n = 0; n < integrationPlan.getDataLinkers().size(); n++) {
                try {
                    dataIntegrator.integrate(completionService.take().get());
                } catch (ExecutionException | InterruptedException e) {
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
