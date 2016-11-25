package at.ac.tuwien.finder.service.dump.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.dump.service.DumpService;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that provides access to
 * {@link DumpService}.
 *
 * @author Kevin Haller
 */
public class DumpServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new {@link DumpServiceFactory}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used.
     */
    public DumpServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "dump";
    }

    @Override
    public IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (pathScanner.hasNext()) {
            throw new IRIInvalidException("");
        }
        return new DumpService(parentIRI, tripleStoreManager);
    }
}
