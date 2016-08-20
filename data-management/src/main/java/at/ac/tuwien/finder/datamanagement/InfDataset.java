package at.ac.tuwien.finder.datamanagement;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.reasoner.Reasoner;

/**
 * Instances of this interface represents a {@link Dataset} that can infer over the models.
 *
 * @author Kevin Haller
 */
public interface InfDataset extends Dataset {

    /**
     * Gets the reasoner of this dataset, which will be used to infer over the models.
     *
     * @return the reasoner of this dataset.
     */
    Reasoner reasoner();

    /**
     * Gets the inference model of the default model, where the specified reasoner is used.
     *
     * @return the inference model of the default model, where the specified reasoner is used.
     */
    InfModel getInferenceDefaultModel();


    /**
     * Gets the inference model of the model, which have the given name.
     *
     * @return the inference model of the model, which have the given name.
     */
    InfModel getNamedInferenceModel(String uri);

}
