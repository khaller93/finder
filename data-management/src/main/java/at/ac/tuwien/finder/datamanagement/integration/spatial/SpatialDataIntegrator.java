package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SimpleSpatialDataSet;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.catalog.exception.DataCatalogException;
import at.ac.tuwien.finder.datamanagement.integration.DataIntegrator;
import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
            logger.error("{}", e);
        }
    }

    @Override
    public String graphName() {
        return SimpleSpatialDataSet.NS.stringValue();
    }

    @Override
    public void integrate(Model model) {
        logger.debug("update({})", model);
        if (model == null) {
            throw new IllegalArgumentException("The given model must not be null.");
        }
        try (RepositoryConnection connection = tripleStoreManager.getConnection();) {

            connection.add(model, SpatialDataSet.NS);
            try {
                tripleStoreManager.getDataCatalog().get(SpatialDataSet.NS).modifiedAt(new Date());
            } catch (DataCatalogException e) {
                logger
                    .error("Modification update for the dataset ({}) failed. {}", SpatialDataSet.NS,
                        e);
            }
        } catch (RepositoryException e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void close() throws Exception {
        tripleStoreManager.close();
    }
}
