package at.ac.tuwien.finder.service.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SimpleSpatialDataSet;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.GraphDatasetService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.spatial.address.factory.AddressServiceFactory;
import at.ac.tuwien.finder.service.spatial.building.factory.AllBuildingsServiceFactory;
import at.ac.tuwien.finder.service.spatial.building.factory.BuildingServiceFactory;
import at.ac.tuwien.finder.service.spatial.buildingtract.factory.BuildingTractServiceFactory;
import at.ac.tuwien.finder.service.spatial.floor.factory.FloorServiceFactory;
import at.ac.tuwien.finder.service.spatial.geometry.factory.GeometryServiceFactory;
import at.ac.tuwien.finder.service.spatial.room.factory.AllRoomsServiceFactory;
import at.ac.tuwien.finder.service.spatial.room.factory.RoomServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s concerning spatial data.
 *
 * @author Kevin Haller
 */
public class SpatialServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpatialServiceFactory.class);

    private Map<String, IServiceFactory> spatialServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new {@link SpatialServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link SpatialServiceFactory}.
     */
    public SpatialServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        spatialServiceFactoryMap.put(BuildingServiceFactory.getManagedPathName(),
            new BuildingServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(AllBuildingsServiceFactory.getManagedPathName(),
            new AllBuildingsServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(BuildingTractServiceFactory.getManagedPathName(),
            new BuildingTractServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(FloorServiceFactory.getManagedPathName(),
            new FloorServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(RoomServiceFactory.getManagedPathName(),
            new RoomServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(AllRoomsServiceFactory.getManagedPathName(),
            new AllRoomsServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(AddressServiceFactory.getManagedPathName(),
            new AddressServiceFactory(tripleStoreManager));
        spatialServiceFactoryMap.put(GeometryServiceFactory.getManagedPathName(),
            new GeometryServiceFactory(tripleStoreManager));
        logger.debug("Factory map of spatial services ({}): ../{}.", getManagedPathName(),
            String.join(", ../", spatialServiceFactoryMap.keySet()));
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner, Map<String, String> parameterMap)
        throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            return new GraphDatasetService(tripleStoreManager,
                SimpleSpatialDataSet.NS.stringValue());
        }
        return super.getService(parent, pathScanner, parameterMap);
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "spatial";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return spatialServiceFactoryMap;
    }
}
