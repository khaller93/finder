package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.exception.ResourceNotFoundException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/**
 * This abstract class is an implementation of {@link QueryService} that returns all
 * known information about a given resource contained in the triple store managed by the
 * given {@link TripleStoreManager}.
 *
 * @author Kevin Haller
 */
public abstract class DescribeResourceService implements QueryService {

    private IResourceIdentifier resourceIdentifier;
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link DescribeResourceService} for the given resource in the triple
     * store managed by {@link TripleStoreManager}. {@code execute()} can be used to get the
     * description of the given resource contained in the given triple store.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be the knowledge base for this {@link DescribeResourceService}.
     * @param resourceIri        the IRI of the resource for which the description can be requested
     *                           by calling {@code execute()}.
     */
    public DescribeResourceService(TripleStoreManager tripleStoreManager, String resourceIri) {
        assert tripleStoreManager != null;
        assert resourceIri != null;
        this.tripleStoreManager = tripleStoreManager;
        this.resourceIdentifier = new IResourceIdentifier(resourceIri);
    }

    /**
     * Gets the {@link IResourceIdentifier} of the resource that shall be described.
     *
     * @return the {@link IResourceIdentifier} of the resource that shall be described.
     */
    public IResourceIdentifier resourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public String getQuery() {
        return String.format("DESCRIBE <%s>", resourceIdentifier.rawIRI());
    }

    /**
     * Executes the query of this {@link QueryService}.
     *
     * @return the result of the execution of the query given by this {@link QueryService}.
     * @throws ServiceException if the execution of the given query failed.
     */
    private Model executeQuery() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model resultModel = QueryResults
                .asModel(connection.prepareGraphQuery(QueryLanguage.SPARQL, getQuery()).evaluate());
            if (resultModel.isEmpty()) {
                throw new ResourceNotFoundException(resourceIdentifier.rawIRI(), String
                    .format("The resource <%s> cannot be located.", resourceIdentifier().rawIRI()));
            }
            return resultModel;
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Wraps the given model into the a {@link Dto}.
     *
     * @param model {@link Model} that shall be wrapped into a {@link Dto}.
     * @return {@link Dto} that wraps the result of this {@link DescribeResourceService}.
     * @throws ServiceException if the result cannot be wrapped.
     */
    protected abstract Dto wrapResult(Model model) throws ServiceException;

    @Override
    public Dto execute() throws ServiceException {
        return wrapResult(executeQuery());
    }
}
