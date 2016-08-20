package at.ac.tuwien.finder.taskmanagement;
/**
 * Instances of this interface represents a close handler that is called, if the corresponding task
 * has been closed.
 *
 * @author Kevin Haller
 */
@FunctionalInterface
public interface TaskCloseHandler {

    /**
     * This method is called, if a corresponding task has been closed.
     */
    void closed();
}
