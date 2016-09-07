package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.datamanagement.integration.Integrator;
import at.ac.tuwien.finder.datamanagement.integration.spatial.SimpleSpatialIntegrationPlan;
import at.ac.tuwien.finder.datamanagement.mediation.MediationPlan;
import at.ac.tuwien.finder.datamanagement.mediation.MediatorManager;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.GUTBuildingInformationAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.SpatialMediator;
import at.ac.tuwien.finder.taskmanagement.TaskManager;

import java.util.Arrays;

public class Main {

    private static TaskManager taskManager = TaskManager.getInstance();

    public static void main(String[] args) throws Exception {
        try (MediatorManager mediatorManager = new MediatorManager();
            MediationPlan mediationPlan = new MediationPlan(Arrays
                .asList(new SpatialMediator(Arrays.asList(new GUTBuildingInformationAcquirer()))),
                new Integrator(TripleStoreManager.SPATIAL_NAMED_GRAPH.toString(),
                    new SimpleSpatialIntegrationPlan()))) {
            mediatorManager.startMediation(mediationPlan);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        taskManager.close();
    }
}
