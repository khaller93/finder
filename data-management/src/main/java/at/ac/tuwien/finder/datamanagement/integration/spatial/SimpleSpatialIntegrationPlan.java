package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.integration.*;

import java.util.Arrays;
import java.util.Collection;
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
     * {@link SimpleDataIntegrator}, no {@link DataLinker} and no {@link DataCleanser}. For the
     * {@link SimpleDataIntegrator} is no namespace mapping given.
     */
    public SimpleSpatialIntegrationPlan() {
        super(new SimpleDataIntegrator(SpatialDataSet.NS), Collections.emptyList(), null);
    }

    /**
     * Creates a new simple facility integration plan. The plan includes a
     * {@link SimpleDataIntegrator}, the given {@link DataLinker}s and no {@link DataCleanser}.
     *
     * @param dataLinkers {@link DataLinker} that shall be used.
     */
    public SimpleSpatialIntegrationPlan(Collection<DataLinker> dataLinkers) {
        super(new SimpleDataIntegrator(SpatialDataSet.NS), dataLinkers, null);
    }

    /**
     * Creates a new simple facility integration plan. The plan includes the given
     * {@link SimpleDataIntegrator} and {@link DataLinker}, but no {@link DataCleanser}.For the
     * {@link SimpleDataIntegrator} is no namespace mapping given.
     *
     * @param dataIntegrator {@link DataIntegrator} that shall be used.
     * @param dataLinkers    {@link DataLinker} that shall be used.
     */
    public SimpleSpatialIntegrationPlan(DataIntegrator dataIntegrator,
        Collection<DataLinker> dataLinkers) {
        super(dataIntegrator, dataLinkers, null);
    }

    /**
     * Creates a new simple facility integration plan. The plan includes a
     * {@link SimpleDataIntegrator}, given {@link DataLinker}s and no {@link DataCleanser}.
     *
     * @param dataLinkers {@link DataLinker} that shall be used.
     */
    public SimpleSpatialIntegrationPlan(DataLinker... dataLinkers) {
        super(new SimpleDataIntegrator(SpatialDataSet.NS), Arrays.asList(dataLinkers), null);
    }

    /**
     * Creates a new simple facility integration plan. The plan includes a
     * {@link SimpleDataIntegrator}, given {@link DataLinker}s and no {@link DataCleanser}.
     *
     * @param dataIntegrator {@link DataIntegrator} that shall be used.
     * @param dataLinkers    {@link DataLinker} that shall be used.
     */
    public SimpleSpatialIntegrationPlan(DataIntegrator dataIntegrator, DataLinker... dataLinkers) {
        super(dataIntegrator, Arrays.asList(dataLinkers), null);
    }

}
