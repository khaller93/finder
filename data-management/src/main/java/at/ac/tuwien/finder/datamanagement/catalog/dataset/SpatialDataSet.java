package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * Instances of this interface represent a {@link DataSet} concerning spatial data about the
 * Vienna University of Technology.
 *
 * @author Kevin Haller
 */
public interface SpatialDataSet extends DataSet {

    IRI NS = SimpleValueFactory.getInstance()
        .createIRI(TripleStoreManager.BASE.stringValue(), "spatial");

}
