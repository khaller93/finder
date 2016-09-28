package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.integration.exception.IntegrationException;
import at.ac.tuwien.finder.datamanagement.util.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

/**
 * This class represents a mediator manager that manages mediation plans and starts them.
 *
 * @author Kevin Haller
 */
public class MediationManager {

    private static final Logger logger = LoggerFactory.getLogger(MediationManager.class);

    private TaskManager taskManager;

    /**
     * Creates a new instance of {@link TaskManager}.
     *
     * @param taskManager {@link TaskManager} that shall be used by this {@link MediationManager}.
     */
    public MediationManager(TaskManager taskManager) {
        assert taskManager != null;
        this.taskManager = taskManager;
    }

    /**
     * Starts the given {@link MediationPlan} and blocks as long as the mediation
     *
     * @param mediationPlan the mediation, which shall be started.
     */
    public void startMediation(MediationPlan mediationPlan) throws IntegrationException {
        if (mediationPlan == null) {
            throw new IllegalArgumentException("The mediation plan must not be null.");
        }
        logger.debug("startMediation({})", mediationPlan);
        CompletionService<Void> completionService =
            new ExecutorCompletionService<Void>(taskManager.threadPool());
        for (Mediator mediatorTask : mediationPlan.mediators()) {
            completionService.submit(mediatorTask, (Void) null);
        }
        for (int n = 0; n < mediationPlan.mediators().size(); n++) {
            try {
                completionService.take();
            } catch (InterruptedException e) {
                logger.error("A task has not completed for mediation task {}. {}", this,
                    e.getMessage());
            }
        }
    }
}
