package at.ac.tuwien.finder.service.spatial.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IService} that manages access to the a geometry
 * resource and services based on the resource.
 *
 * @author Kevin Haller
 */
public class GeometryResourceServiceFactory implements IServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeometryServiceFactory.class);

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new {@link GeometryServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this {@link GeometryServiceFactory}.
     */
    public GeometryResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "id";
    }


    @Override
    public IService getService(URI parent, Scanner pathScanner, Map<String, String> parameterMap)
        throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            if (parameterMap == null || !parameterMap.containsKey("id")) {
                throw new IRIUnknownException("Id must be given.");
            }
            return new DescribeResourceService(tripleStoreManager, parameterMap.get("id"));
        } else {
            throw new IRIUnknownException(String
                .format("'%s' does not expect any further path segments. '%s' is not valid.",
                    parent.toString(), parent.resolve(pathScanner.next())));
        }
    }
}
