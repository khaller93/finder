package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.integration.IntegrationPlan;

import java.util.Arrays;
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
     * {@link SpatialDataIntegrator} ...
     */
    public SimpleSpatialIntegrationPlan() {
        super(new SpatialDataIntegrator(), Collections.emptyList(), null);
    }
}
