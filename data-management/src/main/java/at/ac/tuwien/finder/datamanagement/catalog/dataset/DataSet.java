package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;

/**
 * Instances of this class represents a dataset that contains statements that concern a certain,
 * common topic.
 *
 * @author Kevin Haller
 */
public interface DataSet {

    /**
     * Returns the name of the {@link DataSet}, which must be a valid IRI.
     *
     * @return the name of the {@link DataSet}, which must be a valid IRI.
     */
    IRI name();

    /**
     * Returns the description of this {@link DataSet}, means all {@link Statement}s about this
     * data set in form of a {@link Model}
     *
     * @return the description of this {@link DataSet}, means all {@link Statement}s about this
     * data set in form of a {@link Model}.
     */
    Model dataSetDescription();

}
