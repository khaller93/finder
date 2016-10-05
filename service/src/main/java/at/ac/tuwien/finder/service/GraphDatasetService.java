package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.SimpleResourceDto;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

/**
 * This class is an implementation of {@link IService} that returns all the data contained in a
 * given named graph.
 *
 * @author Kevin Haller
 */
public class GraphDatasetService implements IService {

    private TripleStoreManager tripleStoreManager;
    private IRI graphName;

    /**
     * Creates a new instance of {@link GraphDatasetService}.
     *
     * @param graphName the name of the graph of which all the contained data shall be returned.
     */
    public GraphDatasetService(TripleStoreManager tripleStoreManager, String graphName) {
        this.tripleStoreManager = tripleStoreManager;
        this.graphName = SimpleValueFactory.getInstance().createIRI(graphName);
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection();) {
            return new SimpleResourceDto(new IResourceIdentifier(graphName.stringValue()),
                new LinkedHashModel(Iterations
                    .asList(connection.getStatements(null, null, null, true, graphName))));
        } catch (RepositoryException e) {
            throw new ServiceException(e);
        }
    }
}
