package at.ac.tuwien.finder.datamanagement;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.listeners.ChangedListener;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;

/**
 * This class represents a cache for inference models.
 *
 * @author Kevin Haller
 */
public class InfModelCache {

    private Model baseModel;
    private ChangedListener baseModelChangedListener;

    private InfModel infModel;
    private OntModel ontology;
    private Reasoner reasoner;

    /**
     * Creates a new cache for inference models. The given ontology and reasoner is used for
     * reasoning.
     *
     * @param baseModel the model, which shall be cached.
     * @param ontology  the ontology, which shall be used for reasoning.
     * @param reasoner  the reasoner, which shall be used for reasoning
     */
    public InfModelCache(Model baseModel, OntModel ontology, Reasoner reasoner) {
        this.baseModel = baseModel;
        this.ontology = ontology;
        this.reasoner = reasoner;
        this.baseModelChangedListener = new ChangedListener();
        this.baseModel.register(baseModelChangedListener);
    }

    /**
     * The cached inference model.
     *
     * @return the inference model.
     */
    public InfModel getInferenceModel() {
        if (infModel == null || baseModelChangedListener.hasChanged()) {
            infModel = ModelFactory.createInfModel(reasoner, ontology, baseModel);
        }
        return infModel;
    }
}
