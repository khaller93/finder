package controllers;

import at.ac.tuwien.finder.datamanagement.integration.exception.TripleStoreManagerException;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import play.api.http.MediaRange;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This controller handles the HTTP request of resources and services.
 *
 * @author Kevin Haller
 */
public class APIController extends Controller {

    private ServiceFactory serviceFactory;

    private static Map<String, RDFFormat> mediaTypeLangMap = new HashMap<>();

    static {
        mediaTypeLangMap.put("application/rdf+xml", RDFFormat.RDFXML);
        mediaTypeLangMap.put("text/turtle", RDFFormat.TURTLE);
        mediaTypeLangMap.put("application/ld+json", RDFFormat.JSONLD);
    }

    public APIController() throws TripleStoreManagerException {
        serviceFactory = new ServiceFactory();
    }

    public Result service(String path) {
        List<MediaRange> acceptedMediaRanges = request().acceptedTypes();
        if (acceptedMediaRanges.isEmpty()) {
            return redirect(controllers.routes.APIController.page(path));
        }
        String mediaType = null;
        for (MediaRange acceptedMediaRange : acceptedMediaRanges) {
            if (acceptedMediaRange.accepts("text/html") || acceptedMediaRange
                .accepts("application/xhtml+xml")) {
                return redirect(controllers.routes.APIController.page(path));
            } else if (acceptedMediaRange.accepts("application/rdf+xml")) {
                mediaType = "application/rdf+xml";
            } else if (acceptedMediaRange.accepts("text/turtle")) {
                mediaType = "text/turtle";
            } else if (acceptedMediaRange.accepts("application/ld+json") || acceptedMediaRange
                .accepts("application/json")) {
                mediaType = "application/ld+json";
            }
        }
        if (mediaType != null) {
            return computeService(mediaType, path);
        } else {
            return status(406, String.format(
                "Given accepted media types (%s) are not supported. Supported media types are %s.",
                String.join(", ", acceptedMediaRanges.stream().map(MediaRange::toString)
                    .collect(Collectors.toList())), String.join(", ", Arrays
                    .asList("text/html", "application/xhtml+xml", "application/rdf+xml",
                        "text/turtle", "application/ld+json"))));
        }
    }

    public Result page(String path) {
        return computeService("text/html", path);
    }

    /**
     * Computes the service given by the passed path and returns the result in the given format.
     *
     * @param mediaType media type of the result.
     * @param path      path that represents the service that shall be executed.
     * @return the result of the computation of the given service.
     */
    private Result computeService(String mediaType, String path) {
        assert mediaType != null;
        assert path != null;
        try {
            Model resultModel = serviceFactory.getService(getPathScanner(path)).execute();
            if (mediaType.equals("text/html")) {
                return ok(resultModel.toString());
            } else {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    Rio.write(resultModel, out, mediaTypeLangMap.get(mediaType));
                    return ok(new String(out.toByteArray(), StandardCharsets.UTF_8)).as(mediaType);
                } catch (IOException | RDFHandlerException e) {
                    return status(503, "Internal error.");
                }
            }
        } catch (RDFSerializableException e) {
            if (mediaType.equals("text/html")) {
                return status(e.statusCode(), e.getModel().toString());
            } else {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    Rio.write(e.getModel(), out, mediaTypeLangMap.get(mediaType));
                    return status(e.statusCode(),
                        new String(out.toByteArray(), StandardCharsets.UTF_8)).as(mediaType);
                } catch (IOException | RDFHandlerException e1) {
                    return status(503, "Internal error.");
                }
            }
        }
    }

    /**
     * Returns a {@link Scanner} for the given path that has '/' as delimiter so that the path is
     * split into path segments.
     *
     * @param path path for which the path scanner shall be created.
     * @return a {@link Scanner} for the given path that has '/' as delimiter.
     */
    private static Scanner getPathScanner(String path) {
        Scanner pathScanner = new Scanner(path);
        pathScanner.useDelimiter("/");
        return pathScanner;
    }

}
