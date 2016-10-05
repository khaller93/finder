package at.ac.tuwien.finder.service.catalog.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.DataCatalog;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.GraphDatasetService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IService} that handles access to the data catalog.
 *
 * @author Kevin Haller
 */
public class CatalogServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link CatalogServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link CatalogServiceFactory}.
     */
    public CatalogServiceFactory(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            return new GraphDatasetService(tripleStoreManager, DataCatalog.NS.stringValue());
        } else {
            throw new IRIUnknownException(String
                .format("'%s' does not expect any further path segments. '%s' is not valid.",
                    parent.rawIRI(), parent.resolve(pathScanner.next()).rawIRI()));
        }
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "catalog";
    }

}
