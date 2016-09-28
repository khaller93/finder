package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.dto.IResourceIdentifier;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;

import java.util.Map;
import java.util.Scanner;

/**
 * Different services can be requested by using the corresponding IRI. These IRIs are designed in a
 * hierarchical manner and every path segment of the IRI is represented by a {@link IServiceFactory}.
 * This {@link IServiceFactory} manages all other {@link IServiceFactory}s representing other
 * path segments that can be entered from the current position. At the end of this hierarchical
 * tree the corresponding {@link IService} will be returned. If the given URI is a not valid path
 * (no service is assigned to it), then a {@link IRIUnknownException} will be thrown.
 *
 * @author Kevin Haller
 */
@FunctionalInterface
public interface IServiceFactory {

    /**
     * Gets the {@link IService} that is responsible for handling the requested IRI, if the IRI is
     * valid, otherwise a {@link IRIUnknownException} will be thrown. The given parent path is the
     * part of the IRI that has already been parsed and the given {@link Scanner} points to the next
     * path segment.
     *
     * @param parentIRI    the path of the IRI that has already been parsed.
     * @param pathScanner  the scanner that is pointing to the next path segment.
     * @param parameterMap the parameter map that contains given parameters.
     * @return the {@link IService} that is responsible for handling the requested URI
     * @throws IRIUnknownException if no service is assigned to the given IRI.
     * @throws IRIInvalidException if the given IRI is not valid.
     */
    IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException;
}
