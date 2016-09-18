package at.ac.tuwien.finder.datamanagement.integration.organizational;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.OrganizationalDataSet;
import at.ac.tuwien.finder.datamanagement.integration.IntegrationPlan;
import at.ac.tuwien.finder.datamanagement.integration.SimpleDataIntegrator;

import java.util.Collections;

/**
 * This class represents an integration plan for the integration of linked organizational data about
 * the Vienna University of Technology.
 *
 * @author Kevin Haller
 */
public class SimpleOrganizationalIntegrationPlan extends IntegrationPlan {

    /**
     * Creates a new simple organizational integration plan. The plan includes a
     * {@link SimpleDataIntegrator} ...
     */
    public SimpleOrganizationalIntegrationPlan() {
        super(new SimpleDataIntegrator(OrganizationalDataSet.NS), Collections.emptyList(), null);
    }
}
