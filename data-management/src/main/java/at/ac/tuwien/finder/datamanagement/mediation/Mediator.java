package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.MediatorException;
import at.ac.tuwien.finder.datamanagement.util.TaskManager;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * Instances of this interface represents a mediator for acquiring specific information. The
 * mediator manages one or multiple {@link DataAcquirer} and transforms all the results with
 * the corresponding {@link DataTransformer} into Linked Data models. This Linked Data models are
 * united to a single model and integrated using the a given {@link DataIntegrator}.
 *
 * @author Kevin Haller
 */
public abstract class Mediator implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    private Collection<DataAcquirer> dataAcquirers;
    private DataIntegrator dataIntegrator;
    private TaskManager taskManager;

    /**
     * Creates a new {@link Mediator} that gathers information about a specific entity by using the
     * given list of {@link DataAcquirer}.
     *
     * @param taskManager    {@link TaskManager} that shall be used for this mediator.
     * @param dataAcquirers  list of {@link DataAcquirer}s.
     * @param dataIntegrator {@link DataAcquirer} that shall be used for the acquisitions.
     */
    public Mediator(TaskManager taskManager, Collection<DataAcquirer> dataAcquirers,
        DataIntegrator dataIntegrator) {
        assert taskManager != null;
        assert dataAcquirers != null;
        assert dataIntegrator != null;
        this.taskManager = taskManager;
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
        CompletionService<Model> completionService =
            new ExecutorCompletionService<>(taskManager.threadPool());
        for (DataAcquirer dataAcquirer : dataAcquirers) {
            completionService.submit(() -> {
                logger.debug("mediate({})", dataAcquirer);
                try {
                    DataTransformer transformer = dataAcquirer.transformer();
                    if (transformer == null) {
                        throw new MediatorException(String
                            .format("The returned transformer of %s was null.", dataAcquirer));
                    }
                    return transformer.transform(dataAcquirer.acquire());
                } catch (DataTransformationException | DataAcquireException e) {
                    logger.error("mediate() -> {}", e);
                    throw new MediatorException(e);
                }
            });
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
        dataIntegrator().integrate(model);
    }
}
