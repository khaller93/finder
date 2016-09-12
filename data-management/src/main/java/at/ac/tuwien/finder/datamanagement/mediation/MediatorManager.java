package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.integration.exception.IntegrationException;
import at.ac.tuwien.finder.taskmanagement.TaskManager;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

/**
 * This class represents a mediator manager that manages mediation plans and start them.
 *
 * @author Kevin Haller
 */
public class MediatorManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MediatorManager.class);

    private TaskManager taskManager = TaskManager.getInstance();

    /**
     * Starts the given mediation plan.
     *
     * @param mediationPlan the mediation, which shall be started.
     */
    public void startMediation(MediationPlan mediationPlan) throws IntegrationException {
        if (mediationPlan == null) {
            throw new IllegalArgumentException("The mediation plan must not be null.");
        }
        logger.debug("startMediation({})", mediationPlan);
        CompletionService<Model> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        mediationPlan.mediators().forEach(completionService::submit);
        Model mediationModel = new LinkedHashModel();
        for (int n = 0; n < mediationPlan.mediators().size(); n++) {
            try {
                mediationModel.addAll(completionService.take().get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("One mediation failed with {}", e);
            }
        }
        mediationPlan.integrator().integrate(mediationModel);
    }

    @Override
    public void close() throws Exception {
        taskManager.close();
    }

}
