package at.ac.tuwien.finder.service.search.factory;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.search.service.FreeRoomsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link  FreeRoomsServiceFactory} that manages the access to
 * the free room service. This service has multiple parameters. The start and end date are
 * required and are followed by a ISO 8601 date. Additional optional parameters are nearby and
 * the type of rooms that shall be selected.
 *
 * @author Kevin Haller
 */
public class FreeRoomsServiceFactory implements IServiceFactory {

    private TripleStoreManager tripleStoreManager;
    private SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * Creates a new instance of {@link FreeRoomsServiceFactory}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for this service
     *                           factory.
     */
    public FreeRoomsServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public IService getService(IResourceIdentifier parentIRI, Scanner pathScanner,
        Map<String, String> parameterMap) throws IRIInvalidException, IRIUnknownException {
        Date startDate = null;
        Date endDate = null;
        IResourceIdentifier currentIResourceIdentifier = parentIRI;
        while (pathScanner.hasNext()) {
            String pathSegment = pathScanner.next();
            currentIResourceIdentifier = currentIResourceIdentifier.resolve(pathSegment);
            switch (pathSegment) {
                case "startDate":
                    if (pathScanner.hasNext()) {
                        String startDateSegment = pathScanner.next();
                        currentIResourceIdentifier =
                            currentIResourceIdentifier.resolve(startDateSegment);
                        startDate = parseDate(startDateSegment, currentIResourceIdentifier);
                    } else {
                        throw new IRIInvalidException(String.format(
                            "The start date must be given in the following segment of '%s'. The format is ISO 8601.",
                            parentIRI.resolve(pathSegment)));
                    }
                    break;
                case "endDate":
                    if (pathScanner.hasNext()) {
                        String endDateString = pathScanner.next();
                        currentIResourceIdentifier =
                            currentIResourceIdentifier.resolve(endDateString);
                        endDate = parseDate(endDateString, currentIResourceIdentifier);
                    } else {
                        throw new IRIInvalidException(String.format(
                            "The end date must be given in the following segment of '%s'. The format is ISO 8601.",
                            parentIRI.resolve(pathSegment)));
                    }
                    break;
                default:
                    throw new IRIInvalidException(String
                        .format("The request '%s' is not valid.", currentIResourceIdentifier));
            }
        }
        if (startDate == null || endDate == null) {
            throw new IRIInvalidException(String
                .format("Start date and end date must be given by '%s'.",
                    currentIResourceIdentifier.rawIRI()));
        }
        return new FreeRoomsService(tripleStoreManager, currentIResourceIdentifier.rawIRI(),
            startDate, endDate);
    }

    /**
     * Parses the given string that shall contain a date in ISO 8601 format.
     *
     * @param dateString String of the date that shall be parsed.
     * @param path       the path to the segment.
     * @return the date of the given string.
     * @throws IRIInvalidException if the date cannot be parsed.
     */
    private Date parseDate(String dateString, IResourceIdentifier path) throws IRIInvalidException {
        try {
            return iso8601DateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new IRIInvalidException(String
                .format("The given date '%s' must be formatted as ISO 8601 after '%s'.", dateString,
                    path.rawIRI()));
        }
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "freerooms";
    }

}
