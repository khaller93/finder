package exception;

/**
 * This exception shall be thrown, if a server internal error that shall not be known to the
 * outside occurs.
 *
 * @author Kevin Haller
 */
public class ServerInternalException extends Exception {

    public ServerInternalException() {
        super("Internal error occurred.");
    }
}
