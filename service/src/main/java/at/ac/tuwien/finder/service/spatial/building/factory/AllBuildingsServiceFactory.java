package at.ac.tuwien.finder.service.spatial.building.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;
import at.ac.tuwien.finder.service.spatial.building.service.AllBuildingsService;

import java.net.URI;
import java.util.Map;
import java.util.Scanner;

/**
 * This is an implementation of {@link at.ac.tuwien.finder.service.IServiceFactory} that manages
 * the access to {@link AllBuildingsService}.
 *
 * @author Kevin Haller
 */
public class AllBuildingsServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link BuildingServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link AllBuildingsServiceFactory}.
     */
    public AllBuildingsServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public IService getService(URI parent, Scanner pathScanner, Map<String, String> parameterMap)
        throws RDFSerializableException {
        if (!pathScanner.hasNext()) {
            return new AllBuildingsService(tripleStoreManager);
        } else {
            throw new IRIUnknownException(String
                .format("'%s' does not expect any further path segments. '%s' is not valid.",
                    parent.toString(), parent.resolve(pathScanner.next()).toString()));
        }
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "buildings";
    }

}
