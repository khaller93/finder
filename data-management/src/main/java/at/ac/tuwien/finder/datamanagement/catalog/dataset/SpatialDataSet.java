package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * This class represents a {@link DataSet} concerning spatial data about the Vienna University of
 * Technology.
 *
 * @author Kevin Haller
 */
public class SpatialDataSet implements DataSet {

    public static final IRI NS;

    static {
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        NS = valueFactory.createIRI(TripleStoreManager.BASE.stringValue(), "spatial");
    }

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link SpatialDataSet}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that manages the store for this data
     *                           source.
     */
    public SpatialDataSet(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public IRI name() {
        return NS;
    }

    @Override
    public synchronized Model dataSetDescription() {
        return null;
    }

}
