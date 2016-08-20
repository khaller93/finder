package at.ac.tuwien.finder.datamanagement;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.shared.Lock;
import org.apache.jena.sparql.ARQException;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.util.Context;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is an implementation of {@link Dataset} which caches inference models.
 *
 * @author Kevin Haller
 */
public class CachedInfDatasetImpl implements InfDataset {

    private InfModelCache defaultInferenceModelCache;
    private Map<String, InfModelCache> cachedInfModels = new ConcurrentHashMap<>();
    private ReadWriteLock cachedLock = new ReentrantReadWriteLock();

    private Dataset dataset;
    private OntModel coreOntology;
    private Reasoner reasoner;

    /**
     * Creates a new dataset with inference and caching. Using a rdfs reasoner for inference.
     *
     * @param dataset the dataset, which shall be wrapped into this dataset.
     */
    public CachedInfDatasetImpl(Dataset dataset, OntModel coreOntology) {
        this(dataset, coreOntology, ReasonerRegistry.getRDFSReasoner());
    }

    /**
     * Creates a new dataset with inference and caching.
     *
     * @param dataset the dataset, which shall be wrapped into this dataset.
     */
    public CachedInfDatasetImpl(Dataset dataset, OntModel coreOntology, Reasoner reasoner) {
        this.dataset = dataset;
        this.coreOntology = coreOntology;
        this.reasoner = reasoner;
        dataset.begin(ReadWrite.READ);
        this.defaultInferenceModelCache =
            new InfModelCache(dataset.getDefaultModel(), coreOntology, reasoner);
        dataset.end();
    }

    @Override
    public Reasoner reasoner() {
        return reasoner;
    }

    @Override
    public InfModel getInferenceDefaultModel() {
        cachedLock.readLock().lock();
        try {
            return defaultInferenceModelCache.getInferenceModel();
        } finally {
            cachedLock.readLock().unlock();
        }
    }

    @Override
    public InfModel getNamedInferenceModel(String uri) {
        if (uri == null) {
            throw new ARQException("The uri must not be null!");
        }
        cachedLock.readLock().lock();
        try {
            if (cachedInfModels.containsKey(uri)) {
                return cachedInfModels.get(uri).getInferenceModel();
            }
        } finally {
            cachedLock.readLock().unlock();
        }
        cachedLock.writeLock().lock();
        try {
            InfModelCache inferenceModelCache =
                new InfModelCache(dataset.getNamedModel(uri), coreOntology, reasoner);
            cachedInfModels.put(uri, inferenceModelCache);
            return inferenceModelCache.getInferenceModel();
        } finally {
            cachedLock.writeLock().unlock();
        }
    }

    @Override
    public Model getDefaultModel() {
        return dataset.getDefaultModel();
    }

    @Override
    public void setDefaultModel(Model model) {
        cachedLock.writeLock().lock();
        try {
            dataset.setDefaultModel(model);
            defaultInferenceModelCache =
                new InfModelCache(dataset.getDefaultModel(), coreOntology, reasoner);
        } finally {
            cachedLock.writeLock().unlock();
        }
    }

    @Override
    public Model getNamedModel(String uri) {
        return dataset.getNamedModel(uri);
    }

    @Override
    public boolean containsNamedModel(String uri) {
        return dataset.containsNamedModel(uri);
    }

    @Override
    public void addNamedModel(String uri, Model model) {
        dataset.addNamedModel(uri, model);
    }

    @Override
    public void removeNamedModel(String uri) {
        cachedLock.writeLock().lock();
        try {
            dataset.removeNamedModel(uri);
            cachedInfModels.remove(uri);
        } finally {
            cachedLock.writeLock().unlock();
        }
    }

    @Override
    public void replaceNamedModel(String uri, Model model) {
        cachedLock.writeLock().lock();
        try {
            dataset.replaceNamedModel(uri, model);
            cachedInfModels.replace(uri, new InfModelCache(model, coreOntology, reasoner));
        } finally {
            cachedLock.writeLock().unlock();
        }
    }

    @Override
    public Iterator<String> listNames() {
        return dataset.listNames();
    }

    @Override
    public Lock getLock() {
        return dataset.getLock();
    }

    @Override
    public Context getContext() {
        return dataset.getContext();
    }

    @Override
    public boolean supportsTransactions() {
        return dataset.supportsTransactions();
    }

    @Override
    public boolean supportsTransactionAbort() {
        return dataset.supportsTransactionAbort();
    }

    @Override
    public void begin(ReadWrite readWrite) {
        dataset.begin(readWrite);
    }

    @Override
    public void commit() {
        dataset.commit();
    }

    @Override
    public void abort() {
        dataset.abort();
    }

    @Override
    public boolean isInTransaction() {
        return dataset.isInTransaction();
    }

    @Override
    public void end() {
        dataset.end();
    }

    @Override
    public DatasetGraph asDatasetGraph() {
        return dataset.asDatasetGraph();
    }

    @Override
    public void close() {
        dataset.close();
    }
}
