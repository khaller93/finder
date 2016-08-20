package at.ac.tuwien.finder.datamanagement.mediation;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.MediatorException;
import at.ac.tuwien.lod.taskmanagement.ReturnValueTask;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a mediator task with a specific {@link DataAcquirer}.
 *
 * @author Kevin Haller
 */
public class MediatorTask implements ReturnValueTask<Model> {

    private static final Logger logger = LoggerFactory.getLogger(MediatorTask.class);

    private DataAcquirer dataAcquirer;

    /**
     * Creates a new mediator with the given {@link DataAcquirer} and the given
     * {@link DataTransformer}.
     *
     * @param dataAcquirer a {@link DataAcquirer}, which
     */
    public MediatorTask(DataAcquirer dataAcquirer) {
        this.dataAcquirer = dataAcquirer;
    }

    /**
     * Gathers the data with the given {@link DataAcquirer} and transforms this data into linked
     * data with the given {@link DataTransformer}. The result of the transformation is returned.
     *
     * @return the transformation of the data, which was gathered by the given {@link DataAcquirer}.
     * @throws MediatorException if the mediation failed.
     */
    public Model mediate() throws MediatorException {
        try {
            DataTransformer transformer = dataAcquirer.transformer();
            return transformer.transform(dataAcquirer.acquire());
        } catch (DataTransformationException | DataAcquireException e) {
            logger.error("mediate() -> {}", e);
            throw new MediatorException(e);
        }
    }

    @Override
    public Model call() throws Exception {
        Model responseModel = mediate();
        close();
        return responseModel;
    }

    @Override
    public void close() throws Exception {
        dataAcquirer.close();
    }
}
