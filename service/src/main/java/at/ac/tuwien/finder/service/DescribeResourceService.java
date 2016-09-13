package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.service.exception.RDFSerializableException;
import at.ac.tuwien.finder.service.exception.ResourceNotFoundException;
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
 * This class is an implementation of {@link IService} that returns all known information about a
 * given resource contained in the triple store managed by the given {@link TripleStoreManager}.
 *
 * @author Kevin Haller
 */
public class DescribeResourceService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(DescribeResourceService.class);

    private String resourceUri;
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link DescribeResourceService} for the given resource in the triple
     * store managed by {@link TripleStoreManager}. {@code execute()} can be used to get the
     * description of the given resource contained in the given triple store.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be the knowledge base for this {@link DescribeResourceService}.
     * @param resourceIri        the IRI of the resource for which the description can be requested by
     *                           calling {@code execute()}.
     */
    public DescribeResourceService(TripleStoreManager tripleStoreManager, String resourceIri) {
        assert tripleStoreManager != null;
        assert resourceIri != null;
        this.tripleStoreManager = tripleStoreManager;
        this.resourceUri = resourceIri;
    }

    @Override
    public Model execute() throws RDFSerializableException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model resultModel = new LinkedHashModel();
            connection.prepareGraphQuery(QueryLanguage.SPARQL,
                String.format("DESCRIBE <%s>", resourceUri))
                .evaluate(new StatementCollector(resultModel));
            if (resultModel.isEmpty()) {
                throw new ResourceNotFoundException(
                    String.format("Resource <%s> is unknown.", resourceUri), resourceUri);
            }
            return resultModel;
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException | RDFHandlerException e) {
            throw new ServiceException(e);
        }
    }
}
