package at.ac.tuwien.lod.datamanagement.integration;

import at.ac.tuwien.lod.datamanagement.TripleStoreManager;
import at.ac.tuwien.lod.datamanagement.integration.exception.IntegrationException;
import at.ac.tuwien.lod.taskmanagement.TaskManager;
import org.apache.jena.arq.querybuilder.ConstructBuilder;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represents a integrator manager that starts one or multiple integration processes for
 * a given model.
 *
 * @author Keivn Haller
 */
public class IntegratorManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(IntegratorManager.class);

    private static IntegratorManager integratorManager;
    private static ReadWriteLock integratorManagerLock = new ReentrantReadWriteLock();
    private static Map<String, Integrator> integratorMap = new HashMap<>();
    private static Map<String, Query> separationQuery = new HashMap<>();

    static {
        // Todo: Integration map from
        /* Creates the separation queries */
        for (String graphName : integratorMap.keySet()) {
            try {
                separationQuery.put(graphName,
                    new ConstructBuilder().addConstruct("?s", "?p", "?o").addWhere("?s", "?p", "?o")
                        .addFilter(String.format("REGEX(str(?s), '^%s.*$')", graphName)).build());
            } catch (ParseException e) {
                logger.error("{}", e);
            }
        }
    }

    private TaskManager taskManager = TaskManager.getInstance();
    private AtomicBoolean isClosed = new AtomicBoolean(false);

    /**
     * Returns a new integration manager, if there is not already one existing, otherwise the
     * already existing integration manager.
     *
     * @return a new integration manager, if there is not already one existing, otherwise the
     * already existing integration manager.
     */
    public static IntegratorManager getInstance() {
        integratorManagerLock.writeLock().lock();
        try {
            if (integratorManager == null) {
                integratorManager = new IntegratorManager();
            }
            return integratorManager;
        } finally {
            integratorManagerLock.writeLock().unlock();
        }
    }

    /**
     * Starts an integration process (in an thread) for the given model.
     *
     * @param model the model, for which the integration shall be started.
     * @throws IllegalArgumentException if the model is null.
     */
    public void startIntegration(Model model) throws IntegrationException {
        if (model == null) {
            throw new IllegalArgumentException("The given model must not be null.");
        }
        integratorManagerLock.readLock().lock();
        logger.debug("startIntegration({})", model);
        try {
            if (isClosed.get()) {
                logger.error(
                    "startIntegration({}) fails, because integration manager has been closed",
                    model);
                throw new IntegrationException("This integration manager has been closed.");
            }
            for (Map.Entry<String, Query> queryEntry : separationQuery.entrySet()) {
                QueryExecution queryExecution =
                    QueryExecutionFactory.create(queryEntry.getValue(), model);
                Model queriedModel = queryExecution.execConstruct();
                if (queriedModel.size() != 0) {
                    model = model.difference(queriedModel);
                    taskManager.submitTask(
                        new IntegrationTask(integratorMap.get(queryEntry.getKey()), queriedModel));
                }
            }
        } finally {
            integratorManagerLock.readLock().unlock();
        }
    }

    @Override
    public void close() throws Exception {
        integratorManagerLock.writeLock().lock();
        try {
            for (Integrator integrator : integratorMap.values()) {
                integrator.close();
            }
            isClosed.set(true);
            integratorManager = null;
            taskManager.close();
        } finally {
            integratorManagerLock.writeLock().unlock();
        }
    }
}
