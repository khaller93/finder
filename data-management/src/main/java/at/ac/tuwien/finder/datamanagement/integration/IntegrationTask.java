package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.taskmanagement.Task;
import at.ac.tuwien.finder.taskmanagement.TaskCloseHandler;
import at.ac.tuwien.finder.taskmanagement.TaskFailedHandler;
import org.openrdf.model.Model;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represents an integration task.
 *
 * @author Kevin Haller
 */
public class IntegrationTask implements Task {

    private ConcurrentLinkedQueue<TaskCloseHandler> taskCloseHandlers =
        new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<TaskFailedHandler> taskFailedHandlers =
        new ConcurrentLinkedQueue<>();

    private Integrator integrator;
    private Model model;

    /**
     * Creates a new integration task on the given model.
     *
     * @param integrator the integrator, which shall be wrapped into a task.
     * @param model      the model, on which the integration shall be executed.
     */
    public IntegrationTask(Integrator integrator, Model model) {
        this.integrator = integrator;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            integrator.integrate(model);
        } catch (Exception e) {
            taskFailedHandlers.forEach(taskFailedHandler -> taskFailedHandler.failed(e));
        }
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
        taskCloseHandlers.forEach(TaskCloseHandler::closed);
    }
}
