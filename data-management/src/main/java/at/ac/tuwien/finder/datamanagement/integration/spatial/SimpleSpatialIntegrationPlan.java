package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.integration.DataCleanser;
import at.ac.tuwien.finder.datamanagement.integration.DataLinker;
import at.ac.tuwien.finder.datamanagement.integration.IntegrationPlan;
import at.ac.tuwien.finder.datamanagement.integration.SimpleDataIntegrator;

import java.util.Collections;

/**
 * This class represents an integration plan for the integration of linked spatial data about the
 * Vienna University of Technology.
 *
 * @author Kevin Haller
 */
public class SimpleSpatialIntegrationPlan extends IntegrationPlan {

    /**
     * Creates a new simple facility integration plan. The plan includes a
     * {@link SimpleDataIntegrator}, no {@link DataLinker} and no {@link DataCleanser}.
     */
    public SimpleSpatialIntegrationPlan() {
        super(new SimpleDataIntegrator(SpatialDataSet.NS), Collections.emptyList(), null);
    }
}
