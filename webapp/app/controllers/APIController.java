package controllers;

import at.ac.tuwien.finder.dto.ExceptionResourceDto;
import at.ac.tuwien.finder.dto.SerializationFormat;
import at.ac.tuwien.finder.dto.exception.FileExtensionUnknownException;
import at.ac.tuwien.finder.service.ServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import exception.SerializationMediaTypeException;
import exception.ServerInternalException;
import play.api.http.MediaRange;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.dataPage;
import views.html.resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This controller handles the HTTP request of resources and services.
 *
 * @author Kevin Haller
 */
public class APIController extends Controller {

    private ServiceFactory serviceFactory;

    /**
     * Creates a new instance of {@link APIController}.
     *
     * @throws ServiceException if the exception cannot be established.
     */
    public APIController() throws ServiceException {
        serviceFactory = new ServiceFactory();
    }

    /**
     * Carries out the service represented by the given path and returns the result in the format
     * indicated by the given {@code ACCEPTED-CONTENT} header of the HTTP request (Content negotiations).
     * If the given format is unknown, a error with status code <b>406</b> will be returned.
     *
     * @param path path that represents the service that shall be executed.
     * @return the result of the carried out service or exception.
     */
    public Result service(String path) {
        assert path != null;
        List<MediaRange> acceptedMediaRanges = request().acceptedTypes();
        if (acceptedMediaRanges.isEmpty()) {
            return redirect(controllers.routes.APIController.page(path));
        }
        for (MediaRange acceptedMediaRange : acceptedMediaRanges) {
            if (acceptedMediaRange.accepts("text/html") || acceptedMediaRange
                .accepts("application/xhtml+xml")) {
                return redirect(controllers.routes.APIController.page(path));
            } else {
                for (SerializationFormat format : SerializationFormat.values()) {
                    if (format.mimeTypes().stream().map(acceptedMediaRange::accepts)
                        .reduce(false, (a, b) -> a || b)) {
                        return computeService(format, path);
                    }
                }
            }
        }
        // Prepare the serialization format exception.
        SerializationMediaTypeException serializationMediaTypeException =
            new SerializationMediaTypeException(String.join(", ",
                acceptedMediaRanges.stream().map(MediaRange::toString)
                    .collect(Collectors.toList())));
        return status(406, resource
            .render(path, ExceptionResourceDto.getInstance(serializationMediaTypeException)));
    }

    /**
     * Carries out the service represented by the given path and returns the result in the format
     * indicated by the path ending. If the given format is unknown, a error with status code
     * <b>503</b> will be returned. {@code <spatial/building/id/A.ttl} returns a Turtle file.
     *
     * @param path path that represents the service that shall be executed.
     * @return the result of the carried out service or exception.
     */
    public Result serviceData(String path) {
        try {
            return computeService(SerializationFormat.getFormatOfFile(path.replaceAll(".*/", "")),
                path.replaceAll("\\.\\w+$", ""));
        } catch (FileExtensionUnknownException e) {
            return status(406, e.getMessage());
        }
    }

    /**
     * Carries out the service represented by the given path and returns the result in the given
     * format. If the given format is unknown, a error with status code <b>503</b> will be
     * returned.
     *
     * @param format the format of the response.
     * @param path   path that represents the service that shall be executed.
     * @return the result of the carried out service or exception.
     */
    public Result serviceWithFormat(String format, String path) {
        try {
            return computeService(SerializationFormat.valueOf(format.toUpperCase()), path);
        } catch (IllegalArgumentException e) {
            return status(406, String
                .format("The given format %s is not supported. Supported ones are %s.", format,
                    Arrays.stream(SerializationFormat.values()).map(SerializationFormat::name)
                        .reduce((a, b) -> a + ", " + b).orElse("none")));
        }
    }

    /**
     * Carries out the service represented by the given path and presents the result in a human
     * readable format (XHTML).
     *
     * @param path path that represents the service that shall be executed.
     * @return the page presenting the result of the given result of the requested service, or of
     * the exception that occurred.
     */
    public Result page(String path) {
        assert path != null;
        try {
            return ok(
                dataPage.render(path, serviceFactory.getService(getPathScanner(path)).execute()));
        } catch (ServiceException e) {
            return status(503, dataPage.render(path, ExceptionResourceDto.getInstance(e)));
        } catch (IRIUnknownException | IRIInvalidException i) {
            return status(404, dataPage.render(path, ExceptionResourceDto.getInstance(i)));
        }
    }

    /**
     * Computes the service given by the passed path and returns the result in the given format.
     *
     * @param format {@link SerializationFormat} that shall be returned.
     * @param path   path that represents the service that shall be executed.
     * @return the result of the computation of the given service.
     */
    private Result computeService(SerializationFormat format, String path) {
        assert format != null;
        assert path != null;
        try {
            return ok(serviceFactory.getService(getPathScanner(path)).execute().transformTo(format))
                .as(format.getDefaultMimeType());
        } catch (ServiceException e) {
            try {
                return status(503, ExceptionResourceDto.getInstance(e).transformTo(format));
            } catch (IOException u) {
                return status(503, e.getMessage());
            }
        } catch (IRIUnknownException | IRIInvalidException i) {
            try {
                return status(404, ExceptionResourceDto.getInstance(i).transformTo(format));
            } catch (IOException io) {
                return status(404, i.getMessage());
            }
        } catch (IOException io) {
            ServerInternalException serverInternalException = new ServerInternalException();
            try {
                return status(503,
                    ExceptionResourceDto.getInstance(serverInternalException).transformTo(format));
            } catch (IOException e) {
                return status(503, serverInternalException.getMessage());
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
