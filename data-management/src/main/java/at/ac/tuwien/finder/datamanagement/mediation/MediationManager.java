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
public class MediationManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(MediationManager.class);

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
        mediationPlan.mediators().forEach(taskManager::submitTask);
    }

    @Override
    public void close() throws Exception {
        taskManager.close();
    }

}
