package at.ac.tuwien.lod.taskmanagement.exception;

/**
 * This exception will be thrown, if the {@link at.ac.tuwien.lod.taskmanagement.TaskManager} has
 * already been closed and the requested action therefore can not be executed.
 *
 * @author Kevin Haller
 */
public class TaskManagerClosedException extends Exception {

    /**
     * {@inheritDoc}
     */
    public TaskManagerClosedException() {
    }

    /**
     * {@inheritDoc}
     */
    public TaskManagerClosedException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public TaskManagerClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public TaskManagerClosedException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public TaskManagerClosedException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
