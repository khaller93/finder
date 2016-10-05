package at.ac.tuwien.finder.service.spatial.building.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.spatial.building.service.UnitsOfBuildingService;
import at.ac.tuwien.finder.vocabulary.TUVS;

import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that manages the {@link IService}s
 * concerning floors in a specific building.
 *
 * @author Kevin Haller
 */
public class BuildingResourceFloorsServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link BuildingResourceTractsServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link BuildingResourceFloorsServiceFactory}.
     */
    public BuildingResourceFloorsServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "floors";
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            if (parameterMap == null || !parameterMap.containsKey("id")) {
                throw new IRIUnknownException("Id is not given.");
            }
            return new UnitsOfBuildingService(tripleStoreManager, parent.rawIRI(), "Floor",
                parameterMap.get("id"), TUVS.Floor.stringValue());
        } else {
            throw new IRIUnknownException(String
                .format("'%s' does not expect any further path segments. '%s' is not valid.",
                    parent.rawIRI(), parent.resolve(pathScanner.next()).rawIRI()));
        }
    }
}
