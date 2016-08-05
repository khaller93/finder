package at.ac.tuwien.lod.datamanagement;

import at.ac.tuwien.lod.vocabulary.VocabularyManager;
import at.ac.tuwien.lod.vocabulary.exception.OntologyAccessException;
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

import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * This class represents a triple store manager, which manages the dataset for the application.
 * This includes the management of the core ontology and the base model.
 *
 * @author Kevin Haller
 */
public final class TripleStoreManager {

    private static final Logger logger = LoggerFactory.getLogger(TripleStoreManager.class);

    private static final String TDB_ASSEMBLY_FILE = "config/tdb-assembler.ttl";

    public static final String BASE_NAMED_GRAPH = "http://lod.tuwien.ac.at";
    public static final String FACILITY_NAMED_GRAPH = BASE_NAMED_GRAPH + "/facility";
    public static final String EVENT_NAMED_GRAPH = BASE_NAMED_GRAPH + "/event";
    public static final String ORGANIZATION_NAMED_GRAPH = BASE_NAMED_GRAPH + "/organization";

    private static TripleStoreManager tripleStoreManager;
    private static Semaphore tripleStoreReference = new Semaphore(-1);

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

    private CachedInfDatasetImpl infDataset;
    private OntModel coreOntology;

    /**
     * Creates a new triple store manager.
     */
    private TripleStoreManager() {
        this(ReasonerRegistry.getRDFSReasoner());
    }

    /**
     * Creates a new triple store manager.
     *
     * @param reasoner the reasoner, which shall be used for reasoning over the base model.
     */
    public TripleStoreManager(Reasoner reasoner) {
        Dataset dataset = TDBFactory.assembleDataset(TDB_ASSEMBLY_FILE);
        this.coreOntology = initCoreOntology(dataset);
        this.infDataset = new CachedInfDatasetImpl(dataset, coreOntology(), reasoner);
    }

    /**
     * Gets the core ontology.
     *
     * @return the core ontology.
     */
    public OntModel coreOntology() {
        return coreOntology;
    }

    /**
     * Initialize the core ontology, which is a union of all core ontologies managed by the
     * {@link VocabularyManager}.
     *
     * @param dataset the dataset, of which the ontology shall be fetched.
     * @return the core ontology.
     */
    private OntModel initCoreOntology(Dataset dataset) {
        OntModel coreOntology = ModelFactory.createOntologyModel();
        try {
            Map<String, OntModel> coreOntologies = VocabularyManager.getCoreOntologies();
            for (Map.Entry<String, OntModel> coreOntologyEntry : coreOntologies.entrySet()) {
                logger.debug(coreOntologyEntry.getValue().toString());
                dataset.begin(ReadWrite.READ);
                if (!dataset.containsNamedModel(coreOntologyEntry.getKey())) {
                    dataset.end();
                    dataset.begin(ReadWrite.WRITE);
                    dataset.addNamedModel(coreOntologyEntry.getKey(), coreOntologyEntry.getValue());
                    dataset.commit();
                }
                dataset.end();
                coreOntology.add(coreOntologyEntry.getValue());
            }
        } catch (OntologyAccessException e) {
            logger.error("Update of the core ontologies failed.{}", e);
        }
        return coreOntology;
    }

    /**
     * Gets the base model of the triple store, which includes all known facts and entailed facts.
     *
     * @return the base model.
     */
    public Model getBaseModel() {
        infDataset.begin(ReadWrite.READ);
        try {
            return infDataset.getNamedModel("urn:x-arq:UnionGraph");
        } finally {
            infDataset.end();
        }
    }

    /**
     * Gets the dataset of the triple store.
     *
     * @return the dataset of the triple store for this application.
     */
    public CachedInfDatasetImpl getInfDataset() {
        return infDataset;
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
        infDataset.close();
        tripleStoreReference = new Semaphore(-1);
        tripleStoreReference = null;
    }
}
