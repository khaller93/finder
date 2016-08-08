package at.ac.tuwien.lod.taskmanagement;
/**
 * Instances of this interface represents a failed handler that is called, if the corresponding task
 * has failed with an exception.
 *
 * @author Kevin Haller
 */
@FunctionalInterface
public interface TaskFailedHandler {

    /**
     * This method is called, if the task has been failed with the exception.
     *
     * @param e the exception, which caused the failure.
     */
    void failed(Exception e);

}
