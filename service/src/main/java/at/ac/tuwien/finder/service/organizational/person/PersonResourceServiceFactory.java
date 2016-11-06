package at.ac.tuwien.finder.service.organizational.person;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.PersonDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.DescribeResourceService;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.IServiceFactory;
import at.ac.tuwien.finder.service.InternalTreeNodeServiceFactory;
import at.ac.tuwien.finder.service.exception.IRIInvalidException;
import at.ac.tuwien.finder.service.exception.IRIUnknownException;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.ORG;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.outofbits.opinto.RDFMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class is an implementation of {@link IServiceFactory} and
 * manages knowledge about {@link IServiceFactory}s and {@link IService}s concerning person
 * resources.
 *
 * @author Kevin Haller
 */
class PersonResourceServiceFactory extends InternalTreeNodeServiceFactory {

    private Map<String, IServiceFactory> personServiceFactoryMap = new HashMap<>();
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link PersonResourceServiceFactory}.
     *
     * @param tripleStoreManager the {@link TripleStoreManager} that manages the triple store that
     *                           shall be used as knowledge base for this
     *                           {@link PersonResourceServiceFactory}.
     */
    PersonResourceServiceFactory(TripleStoreManager tripleStoreManager) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
    }

    /**
     * Gets the name of the path segment that is handled by this {@link IServiceFactory}.
     *
     * @return name of the path segment that is handled by this {@link IServiceFactory}.
     */
    public static String getManagedPathName() {
        return "id";
    }

    @Override
    public Map<String, IServiceFactory> getServiceFactoryMap() {
        return personServiceFactoryMap;
    }

    @Override
    public IService getService(IResourceIdentifier parent, Scanner pathScanner,
        Map<String, String> parameter) throws IRIInvalidException, IRIUnknownException {
        String resourceId = pathScanner.next();
        IResourceIdentifier newParent = parent.resolve(resourceId);
        if (pathScanner.hasNext()) {
            return super.getService(newParent, pathScanner,
                super.pushParameter(parameter, "id", newParent.rawIRI()));
        }
        return new DescribeResourceService(tripleStoreManager, newParent.rawIRI()) {

            @Override
            public String getQuery() {
                return String.format("DESCRIBE <%s> ?room WHERE { OPTIONAL {<%s> <%s> ?room} }",
                    newParent.rawIRI(), newParent.rawIRI(), ORG.basedAt);
            }

            @Override
            protected Dto wrapResult(Model model) throws ServiceException {
                PersonDto personDto = RDFMapper.create().readValue(model, PersonDto.class,
                    SimpleValueFactory.getInstance().createIRI(newParent.rawIRI()));
                personDto.setModel(model);
                return personDto;
            }
        };
    }
}
