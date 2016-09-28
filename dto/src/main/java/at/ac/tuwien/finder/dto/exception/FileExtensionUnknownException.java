package at.ac.tuwien.finder.dto.exception;

/**
 * This exception shall be thrown, if no format can be found that have a given file extension.
 *
 * @author Kevin Haller
 */
public class FileExtensionUnknownException extends Exception {

    public FileExtensionUnknownException(String message) {
        super(message);
    }
}
