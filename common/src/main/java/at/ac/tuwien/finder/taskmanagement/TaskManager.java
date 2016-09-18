package at.ac.tuwien.finder.taskmanagement;

import at.ac.tuwien.finder.taskmanagement.exception.TaskManagerClosedException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class represents a manager for concurrent tasks.
 *
 * @æuthor Kevin Haller
 */
public final class TaskManager implements AutoCloseable {

    private static TaskManager taskManager;

    private ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private ConcurrentLinkedQueue<Task> activeTasksList = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Future> activeFutureList = new ConcurrentLinkedQueue<>();

    private Lock taskSubmitLock = new ReentrantLock();
    private static Semaphore instanceSemaphore = new Semaphore(-1);
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    /**
     * Gets a new task manager, if there is not already existing one, otherwise the existing one
     * will be returned.
     *
     * @return a new task manager, if there is not already existing one, otherwise the existing one
     * will be returned.
     */
    public static TaskManager getInstance() {
        instanceSemaphore.release();
        if (taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }

    /**
     * Adds the given {@link Task} to this task manager. If the task manager has been closed, the
     * submitted task will be ignored.
     *
     * @param task the {@link Task}, which shall be added to the task manager.
     * @throws IllegalArgumentException if the {@link Task} is null.
     * @throws IllegalStateException    if the task manager has already been closed.
     */
    public void submitTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("The given persistence task must nit be null.");
        }
        taskSubmitLock.lock();
        if (!isClosed.get()) {
            task.setParentTaskManager(this);
            activeTasksList.add(task);
            task.addClosedHandler(() -> activeTasksList.remove(task));
            threadPool.submit(task);
        }
        taskSubmitLock.unlock();
    }

    /**
     * Adds the given {@link ReturnValueTask} to this task manager. The task manager must not be
     * closed.
     *
     * @param returnValueTask the {@link ReturnValueTask}, which shall be added to the task manager.
     * @return the {@link Future} of the submitted callable.
     */
    public <R> Future<R> submitCallable(ReturnValueTask<R> returnValueTask)
        throws TaskManagerClosedException {
        if (returnValueTask == null) {
            throw new IllegalArgumentException("The given persistent callable must not be null.");
        }
        taskSubmitLock.lock();
        try {
            if (!isClosed.get()) {
                Future<R> future = threadPool.submit(returnValueTask);
                activeFutureList.add(future);
                return future;
            } else {
                throw new TaskManagerClosedException("The task manager has been closed.");
            }
        } finally {
            taskSubmitLock.unlock();
        }
    }

    /**
     * Gets the thread pool of the task manager.
     *
     * @return the thread pool of the task manager.
     */
    public ExecutorService threadPool() {
        return threadPool;
    }

    /**
     * Checks if the task manager is closed.
     *
     * @return true, if the task manager is closed, otherwise false.
     */
    public boolean isClosed() {
        return this.isClosed.get();
    }

    @Override
    public void close() throws Exception {
        if (instanceSemaphore.tryAcquire()) {
            return;
        }
        taskSubmitLock.lock();
        try {
            isClosed.set(true);
            for (Task task : activeTasksList) {
                task.close();
            }
            for (Future future : activeFutureList) {
                future.cancel(true);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            taskSubmitLock.unlock();
        }
        threadPool.shutdown();
    }
}
