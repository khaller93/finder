package at.ac.tuwien.finder.service.search.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.search.service.FreeRoomsService;
import at.ac.tuwien.finder.service.search.service.RoutesService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link  RouteServiceFactory} that manages the access to
 * the route service that searches for routes to a given entity.
 *
 * @author Kevin Haller
 */
public class RouteServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link RouteServiceFactory}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for this service
     *                           factory.
     */
    public RouteServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        return new RoutesService(tripleStoreManager, parentIRI.rawIRI());
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "routeTo";
    }

}
