package at.ac.tuwien.finder.taskmanagement;
/**
 * Instances of this interface represents a task that can run concurrently with other
 * tasks.
 *
 * @author Kevin Haller
 */
public interface Task extends Runnable, AutoCloseable {

    /**
     * Adds the given {@link TaskCloseHandler} to this task.
     *
     * @param taskCloseHandler the close handler, which shall be called, if the task has been
     *                         closed.
     */
    void addClosedHandler(TaskCloseHandler taskCloseHandler);

    /**
     * Adds the given {@link TaskFailedHandler} to this task.
     *
     * @param taskFailedHandler the failed handler, which shall be called, if the task has failed.
     */
    void addFailedHandler(TaskFailedHandler taskFailedHandler);
}
