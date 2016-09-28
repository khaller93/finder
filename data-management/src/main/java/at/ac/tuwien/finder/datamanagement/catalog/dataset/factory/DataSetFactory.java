package at.ac.tuwien.finder.datamanagement.catalog.dataset.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.*;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.factory.exception.DataSetFactoryException;
import org.eclipse.rdf4j.model.IRI;

/**
 * This class creates an instance of the corresponding {@link DataSet} to a given namespace
 * {@link IRI}.
 *
 * @author Kevin Haller
 */
public class DataSetFactory {

    /**
     * Gets the corresponding {@link DataSet} for the given namespace {@link IRI}.
     *
     * @param namespace          namespace {@link IRI} for which the corresponding {@link DataSet}
     *                           shall be returned.
     * @param tripleStoreManager {@link TripleStoreManager} that shall contain the returned
     *                           {@link DataSet}.
     * @return he corresponding {@link DataSet} for the given namespace {@link IRI}.
     * @throws DataSetFactoryException if the
     */
    public static DataSet createDataSet(IRI namespace, TripleStoreManager tripleStoreManager)
        throws DataSetFactoryException {
        if (SpatialDataSet.NS.equals(namespace)) {
            return new SimpleSpatialDataSet(tripleStoreManager);
        } else if (OrganizationalDataSet.NS.equals(namespace)) {
            return new SimpleOrganizationalDataSet(tripleStoreManager);
        } else if (EventDataSet.NS.equals(namespace)) {
            return new SimpleEventDataSet(tripleStoreManager);
        } else {
            throw new DataSetFactoryException(
                String.format("No data set is registered for the given namespace '%s'", namespace));
        }
    }

}
