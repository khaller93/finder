package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import org.openrdf.model.Model;

/**
 * This class is an implementation of {@link DataTransformer} that passes the given model without
 * any transformation.
 *
 * @author Kevin Haller
 */
public class NOPTransformer implements DataTransformer<Model> {
    @Override
    public Model transform(Model data) throws DataTransformationException {
        return data;
    }

    @Override
    public void close() throws Exception {
        /* Nothing to do */
    }
}
