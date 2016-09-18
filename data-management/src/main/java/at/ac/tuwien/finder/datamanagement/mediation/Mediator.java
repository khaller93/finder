package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.taskmanagement.Task;
import at.ac.tuwien.finder.taskmanagement.TaskCloseHandler;
import at.ac.tuwien.finder.taskmanagement.TaskFailedHandler;
import at.ac.tuwien.finder.taskmanagement.TaskManager;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * Instances of this interface represents a mediator for a specific entity. The mediator manages
 * one or multiple {@link DataAcquirer} and transforms all the results with the corresponding
 * {@link DataTransformer} into linked data models. This linked data models are united to a single
 * model (named with the specified graph name).
 *
 * @author Kevin Haller
 */
public abstract class Mediator implements Task {

    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    private ConcurrentLinkedQueue<TaskCloseHandler> taskCloseHandlers =
        new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<TaskFailedHandler> taskFailedHandlers =
        new ConcurrentLinkedQueue<>();

    private TaskManager taskManager;
    private Collection<DataAcquirer> dataAcquirers;
    private DataIntegrator dataIntegrator;

    /**
     * Creates a new mediator that gathers information about a specific entity by using the given
     * list of {@link DataAcquirer}.
     *
     * @param dataAcquirers list of {@link DataAcquirer}.
     */
    public Mediator(Collection<DataAcquirer> dataAcquirers, DataIntegrator dataIntegrator) {
        this.dataAcquirers = dataAcquirers;
        this.dataIntegrator = dataIntegrator;
    }

    /**
     * Gets the {@link DataIntegrator} of this mediator.
     *
     * @return the {@link DataIntegrator} of this mediator.
     */
    public DataIntegrator dataIntegrator() {
        return dataIntegrator;
    }

    @Override
    public void run() {
        if (taskManager == null) {
            taskFailedHandlers.forEach(taskFailedHandler -> taskFailedHandler.failed(
                new IllegalStateException(String
                    .format("The task manager for this mediator task %s was not set", this))));
        }
        CompletionService<Model> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (DataAcquirer dataAcquirer : dataAcquirers) {
            completionService.submit(new MediatorTask(dataAcquirer));
        }
        // Gather the results.
        Model model = new LinkedHashModel();
        for (int n = 0; n < dataAcquirers.size(); n++) {
            try {
                Future<Model> retrievedModel = completionService.take();
                try {
                    model.addAll(retrievedModel.get());
                } catch (ExecutionException | CancellationException | InterruptedException e) {
                    logger.error("A data acquirer failed. {}", e);
                }
            } catch (InterruptedException e) {
                logger.error("A data acquirer failed. {}", e);
            }
        }
        logger.debug("Start the integration process for {} with {}", model, dataIntegrator);
        dataIntegrator.integrate(model);
        taskCloseHandlers.forEach(TaskCloseHandler::closed);
    }

    @Override
    public void setParentTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void addClosedHandler(TaskCloseHandler taskCloseHandler) {
        taskCloseHandlers.add(taskCloseHandler);
    }

    @Override
    public void addFailedHandler(TaskFailedHandler taskFailedHandler) {
        taskFailedHandlers.add(taskFailedHandler);
    }

    @Override
    public void close() throws Exception {

    }
}
