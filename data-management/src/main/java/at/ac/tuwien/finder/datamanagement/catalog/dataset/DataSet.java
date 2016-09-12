package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;

import java.util.Date;

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
    IRI namespace();

    /**
     * Informs this {@link DataSet} that it has been changed. If there are multiple updates at the
     * same time, the most recent modification update will be taken, the others will be ignored.
     *
     * @param modificationDate {@link Date} at which this {@link DataSet} was changed.
     */
    void modifiedAt(Date modificationDate);

    /**
     * Returns the description of this {@link DataSet}, means all {@link Statement}s about this
     * data set in form of a {@link Model}
     *
     * @return the description of this {@link DataSet}, means all {@link Statement}s about this
     * data set in form of a {@link Model}.
     */
    Model description();

}
