package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.DataCatalog;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * This class represents a {@link DataSet} concerning spatial data about the Vienna University of
 * Technology.
 *
 * @author Kevin Haller
 */
public class SpatialDataSet implements DataSet {

    public static final URI NS;

    static {
        ValueFactory valueFactory = ValueFactoryImpl.getInstance();
        NS = valueFactory.createURI(DataCatalog.BASE_NAMED_GRAPH.stringValue(), "spatial");
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
    public URI name() {
        return NS;
    }

    @Override
    public synchronized Model dataSetDescription() {
        return null;
    }

}
