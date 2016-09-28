package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.dto.IResourceIdentifier;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This abstract class is a partial implementation of {@link IServiceFactory} that should abstract
 * the common parts for internal nodes in the path tree.
 *
 * @author Kevin Haller
 */
public abstract class InternalTreeNodeServiceFactory implements IServiceFactory {

    /**
     * Gets the map of {@link IServiceFactory}s that are managed by this {@link IServiceFactory}.
     *
     * @return the map of {@link IServiceFactory}s that are managed by this {@link IServiceFactory}.
     */
    public abstract Map<String, IServiceFactory> getServiceFactoryMap();

    /**
     * Pushes the parameter in form of the given key,value pair to the parameter map. If the
     * parameter map does not exist, a map will be created.
     *
     * @param key   the key of the parameter.
     * @param value the value of the parameter.
     * @return the parameter map that now contains the given pair (key, value).
     */
    public Map<String, String> pushParameter(Map<String, String> parameterMap, String key,
        String value) {
        if (parameterMap == null) {
            parameterMap = new HashMap<>();
        }
        parameterMap.put(key, value);
        return parameterMap;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        if (!pathScanner.hasNext()) {
            throw new IRIUnknownException(
                String.format("There is no service assigned to '%s'.", parent.toString()));
        }
        String pathSegment = pathScanner.next();
        IResourceIdentifier newParent = parent.resolve(pathSegment + "/");
        Map<String, IServiceFactory> serviceFactoryMap = getServiceFactoryMap();
        if (!serviceFactoryMap.containsKey(pathSegment)) {
            throw new IRIUnknownException(String.format(
                "The given IRI '%s' is not valid. Possible continuations of '%s' are ../%s.",
                newParent.toString(), parent.toString(),
                String.join(", ../", getServiceFactoryMap().keySet())));
        }
        return serviceFactoryMap.get(pathSegment).getService(newParent, pathScanner, parameterMap);
    }
}
