package at.ac.tuwien.finder.datamanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a manager that holds a {@link ExecutorService} to handle tasks.
 *
 * @æuthor Kevin Haller
 */
public final class TaskManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private static TaskManager taskManager;

    private ExecutorService threadPool = Executors.newFixedThreadPool(100);
    private static Semaphore instanceSemaphore = new Semaphore(-1);
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    /**
     * Gets a new task manager, if there is not already existing one, otherwise the existing one
     * will be returned.
     *
     * @return a new task manager, if there is not already existing one, otherwise the existing one
     * will be returned.
     */
    public synchronized static TaskManager getInstance() {
        instanceSemaphore.release();
        if (taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
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
    public synchronized void close() throws Exception {
        if (instanceSemaphore.tryAcquire()) {
            return;
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException i) {
            logger.error(
                "The task manager waiting for the termination of spawned threads was interrupted. {}",
                i);
        }
    }
}
