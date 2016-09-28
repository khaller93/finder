package exception;

import at.ac.tuwien.finder.dto.SerializationFormat;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class represents an exception that will be thrown, if the requested serialization format is
 * not supported.
 *
 * @author Kevin Haller
 */
public class SerializationMediaTypeException extends Exception {

    private static String getErrorMessage(String requestedFormat) {
        return String.format(
            "Given accepted media types (%s) are not supported. Supported media types are %s.",
            requestedFormat, String.join(", ", Arrays.stream(SerializationFormat.values()).map(
                format -> String
                    .format("%s: %s", format.name(), String.join(",", format.getMimeTypes())))
                .collect(Collectors.toList())));

    }

    public SerializationMediaTypeException(String requestedFormat) {
        super(getErrorMessage(requestedFormat));
    }

    public SerializationMediaTypeException(String requestedFormat, Throwable cause) {
        super(getErrorMessage(requestedFormat), cause);
    }
}
