package at.ac.tuwien.finder.service.vocabulary;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.spatial.SpatialServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} that manages knowledge about
 * local vocabularies.
 *
 * @author Kevin Haller
 */
public class VocabularyServiceFactory extends InternalTreeNodeServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpatialServiceFactory.class);

    private Map<String, IServiceFactory> serviceFactoryMap = new HashMap<>();

    /**
     * Creates a new instance of {@link VocabularyServiceFactory}.
     */
    public VocabularyServiceFactory() {
        serviceFactoryMap.put("spatial", new DescribeVocabularyServiceFactory("spatial"));
        logger.debug("Initializes vocabulary services for: ../{}",
            String.join(", ../", serviceFactoryMap.keySet()));
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return serviceFactoryMap;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIUnknownException, IRIInvalidException {
        if (pathScanner.hasNext()) {
            return super.getService(parent, pathScanner, parameterMap);
        } else {
            throw new IRIUnknownException(
                String.format("Not implemented yet ! (%s)", parent.rawIRI()));
        }
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "vocab";
    }
}
