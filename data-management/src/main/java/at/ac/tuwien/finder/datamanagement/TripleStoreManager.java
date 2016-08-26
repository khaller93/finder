package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.vocabulary.VocabularyManager;
import at.ac.tuwien.finder.vocabulary.exception.OntologyAccessException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * This class represents a triple store manager that manages details about the triple store, the
 * structure of the data (graphs) and access to it.
 *
 * @author Kevin Haller
 */
public class TripleStoreManager {

    private static final Logger logger = LoggerFactory.getLogger(TripleStoreManager.class);

    private static final String TDB_ASSEMBLY_FILE = "config/tdb-assembler.ttl";
    public static final String BASE_NAMED_GRAPH = "http://finder.tuwien.ac.at";
    public static final String SPATIAL_NAMED_GRAPH = BASE_NAMED_GRAPH + "/spatial";

    private static TripleStoreManager tripleStoreManager;
    private static Semaphore tripleStoreReference = new Semaphore(-1);

    private VocabularyManager vocabularyManager;

    /**
     * Gets an instance of the triple store manager and increments the reference counter. If the
     * reference counter was 0, a new instance will be created, otherwise the already existing
     * triple store instance will be returned.
     *
     * @return an instance of the triple store manager.
     */
    public synchronized static TripleStoreManager getInstance() {
        tripleStoreReference.release();
        if (tripleStoreManager == null) {
            tripleStoreManager = new TripleStoreManager();
        }
        return tripleStoreManager;
    }

    private Dataset dataset;

    /**
     * Creates a new triple store manager. The default reasoning for the base model is RDFS.
     */
    private TripleStoreManager() {
        this(ReasonerRegistry.getRDFSReasoner());
    }

    /**
     * Creates a new triple store manager based on the given dataset. The default reasoning for the
     * base model is RDFS.
     *
     * @param dataset that shall be used as base for the {@link TripleStoreManager}.
     */
    public TripleStoreManager(Dataset dataset) {
        this(ReasonerRegistry.getRDFSReasoner());
    }

    /**
     * Creates a new triple store manager that bases on a local, pure triple store (TDB).
     *
     * @param reasoner the reasoner, which shall be used for reasoning over the base model.
     */
    private TripleStoreManager(Reasoner reasoner) {
        this(TDBFactory.assembleDataset(TDB_ASSEMBLY_FILE), reasoner);
    }

    /**
     * Creates a new triple store manager that bases on the given dataset and uses the given
     * reasoner to reason over the base model.
     *
     * @param reasoner the reasoner, which shall be used for reasoning over the base model.
     */
    public TripleStoreManager(Dataset dataset, Reasoner reasoner) {
        try {
            this.vocabularyManager = VocabularyManager.getInstance();
        } catch (OntologyAccessException o) {
            logger.error("The access to the vocabulary manager failed. {}", o);
        }
        this.dataset = new CachedInfDatasetImpl(dataset, coreOntology(), reasoner);
    }

    /**
     * Gets the core ontology. If the core ontology could not be accessed, an empty {@link OntModel}
     * will be returned.
     *
     * @return the core ontology or an empty {@link OntModel}, if the core ontology could not be
     * accessed.
     */
    public OntModel coreOntology() {
        if (vocabularyManager == null) {
            return ModelFactory.createOntologyModel();
        }
        return vocabularyManager.getCoreOntology();
    }

    /**
     * Gets the base model of the triple store, which includes all known facts and entailed facts.
     *
     * @return the base model.
     */
    public Model getBaseModel() {
        dataset.begin(ReadWrite.READ);
        try {
            return dataset.getNamedModel("urn:x-arq:UnionGraph");
        } finally {
            dataset.end();
        }
    }

    /**
     * Gets the dataset of the triple store.
     *
     * @return the dataset of the triple store for this application.
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Closes this triple store manager, if the reference counter is 0.
     */
    public synchronized void close() {
        close(false);
    }

    /**
     * Closes this triple store manager, if the reference counter is 0 or if force is true (then
     * the reference counter will be ignored).
     *
     * @param force true, if the reference counter shall be ignored and
     */
    public synchronized void close(boolean force) {
        if (tripleStoreReference.tryAcquire() && !force) {
            return;
        }
        dataset.close();
        tripleStoreReference = new Semaphore(-1);
        tripleStoreReference = null;
    }
}
