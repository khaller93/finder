package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.datamanagement.catalog.dataset.EventDataSet;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.OrganizationalDataSet;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.datamanagement.integration.ComplexDataIntegrator;
import at.ac.tuwien.finder.datamanagement.integration.SimpleDataIntegrator;
import at.ac.tuwien.finder.datamanagement.integration.spatial.RoomBuildingTractLinker;
import at.ac.tuwien.finder.datamanagement.integration.spatial.RoomFloorSectionLinker;
import at.ac.tuwien.finder.datamanagement.integration.spatial.SimpleSpatialIntegrationPlan;
import at.ac.tuwien.finder.datamanagement.mediation.MediationManager;
import at.ac.tuwien.finder.datamanagement.mediation.MediationPlan;
import at.ac.tuwien.finder.datamanagement.mediation.organizational.OrganizationalMediator;
import at.ac.tuwien.finder.datamanagement.mediation.organizational.RestTISSPersonCrawler;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.GUTBuildingInformationAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.SeleniumTISSFacilityAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.SpatialMediator;
import at.ac.tuwien.finder.datamanagement.mediation.spatial.WKTIndoorPlanAcquirer;
import at.ac.tuwien.finder.datamanagement.util.TaskManager;
import org.eclipse.rdf4j.model.IRI;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static TaskManager taskManager = TaskManager.getInstance();

    public static void main(String[] args) throws Exception {
        Calendar now = new GregorianCalendar(2016, Calendar.OCTOBER, 1);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_MONTH, 21);
        Map<IRI, IRI> dataSetMapping = new HashMap<>();
        dataSetMapping.put(SpatialDataSet.NS, SpatialDataSet.NS);
        dataSetMapping.put(OrganizationalDataSet.NS, OrganizationalDataSet.NS);
        dataSetMapping.put(EventDataSet.NS, EventDataSet.NS);
        MediationManager mediatorManager = new MediationManager(taskManager);
        try (GUTBuildingInformationAcquirer gutBuildingAcquirer = new GUTBuildingInformationAcquirer();
            SeleniumTISSFacilityAcquirer seleniumTISSFacilityAcquirer = new SeleniumTISSFacilityAcquirer(
                now.getTime(), end.getTime(), new FirefoxDriver());
            RestTISSPersonCrawler restTISSPersonCrawler = new RestTISSPersonCrawler();
            SimpleSpatialIntegrationPlan simpleSpatialIntegrationPlan = new SimpleSpatialIntegrationPlan(
                new SimpleDataIntegrator(SpatialDataSet.NS, dataSetMapping),
                new RoomBuildingTractLinker(), new RoomFloorSectionLinker());
            ComplexDataIntegrator complexDataIntegrator = new ComplexDataIntegrator(taskManager,
                simpleSpatialIntegrationPlan);) {
            //Mediator for spatial data.
            SpatialMediator spatialMediator =
                new SpatialMediator(taskManager, complexDataIntegrator, gutBuildingAcquirer,
                    new WKTIndoorPlanAcquirer(), seleniumTISSFacilityAcquirer);
            //Mediator for organizational data
            OrganizationalMediator organizationalMediator = new OrganizationalMediator(taskManager,
                new SimpleDataIntegrator(OrganizationalDataSet.NS, dataSetMapping),
                restTISSPersonCrawler);
            //mediatorManager
            //    .startMediation(new MediationPlan(spatialMediator, organizationalMediator));
            mediatorManager.startMediation(new MediationPlan(spatialMediator));
        }
        taskManager.close();
    }

}
