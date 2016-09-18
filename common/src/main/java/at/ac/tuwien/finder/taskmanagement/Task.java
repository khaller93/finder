package at.ac.tuwien.finder.taskmanagement;

/**
 * Instances of this interface represents a task that can run concurrently with other
 * tasks.
 *
 * @author Kevin Haller
 */
public interface Task extends Runnable, AutoCloseable {

    /**
     * The parent task manager of this task. This will be set after each call of
     * {@code taskmanager.submitTask(Task task)} for the given task.
     *
     * @param taskManager sets the parent {@link TaskManager} of this task.
     */
    void setParentTaskManager(TaskManager taskManager);

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
