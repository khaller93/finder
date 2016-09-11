package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of {@link DataIntegrator} that integrates linked spatial data about
 * the Vienna University of Technology.
 *
 * @author Kevin Haller
 */
public class SpatialDataIntegrator implements DataIntegrator {

    private static final Logger logger = LoggerFactory.getLogger(SpatialDataIntegrator.class);

    private TripleStoreManager tripleStoreManager;

    public SpatialDataIntegrator() {
        try {
            tripleStoreManager = TripleStoreManager.getInstance();
        } catch (TripleStoreManagerException e) {
            logger.debug("{}", e);
        }
    }

    @Override
    public String graphName() {
        return SpatialDataSet.NS.stringValue();
    }

    @Override
    public void integrate(Model model) {
        logger.debug("update({})", model);
        if (model == null) {
            throw new IllegalArgumentException("The given model must not be null.");
        }
        RepositoryConnection connection = null;
        try {
            connection = tripleStoreManager.getConnection();
            connection.add(model, SpatialDataSet.NS);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        tripleStoreManager.close();
    }
}