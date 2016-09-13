package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract class is a partial implementation of {@link at.ac.tuwien.finder.service.IService}
 * for services that can be expressed in a pure SPARQL query. The part that must be implemented by
 * subclasses is {@code getQuery()}. This method returns the SPARQL query for the specific service.
 *
 * @author Kevin Haller
 */
public abstract class SimpleQueryService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleQueryService.class);

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link SimpleQueryService}.
     *
     * @param tripleStoreManager the triple store that manages the triple store that shall build the
     *                           knowledge base for this {@link SimpleQueryService}
     */
    public SimpleQueryService(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    /**
     * Gets the SPARQL Query that expresses this service.
     *
     * @return the SPARQL Query that expresses this service.
     */
    public abstract String getQuery();

    @Override
    public Model execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model resultModel = new LinkedHashModel();
            connection.prepareGraphQuery(QueryLanguage.SPARQL, getQuery())
                .evaluate(new StatementCollector(resultModel));
            return resultModel;
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }

}
