package at.ac.tuwien.finder.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleResourceDto;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;

/**
 * This class is a simple implementation of {@link DescribeResourceService} that wraps the result
 * into a {@link SimpleResourceDto}.
 *
 * @author Kevin Haller
 */
public class SimpleDescribeResourceService extends DescribeResourceService {
    /**
     * Creates a new instance of {@link SimpleDescribeResourceService} for the given resource in
     * the triple store managed by {@link TripleStoreManager}. {@code execute()} can be used to get
     * the description of the given resource contained in the given triple store.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be the knowledge base for this {@link SimpleDescribeResourceService}.
     * @param resourceIri        the IRI of the resource for which the description can be requested
     *                           by calling {@code execute()}.
     */
    public SimpleDescribeResourceService(TripleStoreManager tripleStoreManager,
        String resourceIri) {
        super(tripleStoreManager, resourceIri);
    }

    @Override
    public Dto wrapResult(Model model) throws ServiceException {
        return new SimpleResourceDto(resourceIdentifier(), model);
    }
}
