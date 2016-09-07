package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.taskmanagement.ReturnValueTask;
import at.ac.tuwien.finder.taskmanagement.TaskManager;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
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
public abstract class Mediator implements ReturnValueTask<Model> {

    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    private TaskManager taskManager = TaskManager.getInstance();
    private Collection<DataAcquirer> dataAcquirers;

    /**
     * Creates a new mediator that gathers information about a specific entity by using the given
     * list of {@link DataAcquirer}.
     *
     * @param dataAcquirers list of {@link DataAcquirer}.
     */
    public Mediator(Collection<DataAcquirer> dataAcquirers) {
        this.dataAcquirers = dataAcquirers;
    }

    /**
     * Gets the graph name of the model, which is managed by the mediator.
     *
     * @return the graph name of the model, which is managed by the mediator.
     */
    public abstract String graphName();

    @Override
    public Model call() throws Exception {
        CompletionService<Model> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (DataAcquirer dataAcquirer : dataAcquirers) {
            completionService.submit(new MediatorTask(dataAcquirer));
        }
        // Gather the results.
        Model model = new LinkedHashModel();
        for (int n = 0; n < dataAcquirers.size(); n++) {
            Future<Model> retrievedModel = completionService.take();
            try {
                model.addAll(retrievedModel.get());
            } catch (ExecutionException | CancellationException | InterruptedException e) {
                //TODO: Try a second execution for callable that caused an InterruptedException.
                logger.error("A data acquirer failed. {}", e);
            }
        }
        return model;
    }

    @Override
    public void close() throws Exception {
        taskManager.close();
    }
}
