package at.ac.tuwien.finder.service.dump.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.dto.SimpleResourceDto;
import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.VOID;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.util.Date;

/**
 * This class is an implementation of {@link IService} that provides a dump of the whole dataset.
 *
 * @author Kevin Haller
 */
public class DumpService implements IService {

    private IResourceIdentifier dumpIRI;
    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new {@link DumpService}.
     *
     * @param dumpIRI            {@link IResourceIdentifier} of the given dump request.
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used.
     */
    public DumpService(IResourceIdentifier dumpIRI, TripleStoreManager tripleStoreManager) {
        assert dumpIRI != null;
        assert tripleStoreManager != null;
        this.dumpIRI = dumpIRI;
        this.tripleStoreManager = tripleStoreManager;
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            ValueFactory valueFactory = SimpleValueFactory.getInstance();
            Model dumpModel = new ModelBuilder().add(dumpIRI.iriValue(), RDF.TYPE, VOID.DATASET)
                .add(dumpIRI.iriValue(), DCTERMS.TITLE,
                    valueFactory.createLiteral("RDF dump of the Finder (app)."))
                .add(dumpIRI.iriValue(), DCTERMS.ISSUED, valueFactory.createLiteral(new Date()))
                .add(dumpIRI.iriValue(), DCTERMS.DESCRIPTION,
                    "This dataset is a RDF dump of the finder (app) including spatial, organizational and event data of TU Vienna.")
                .add(dumpIRI.iriValue(), DCTERMS.CONTRIBUTOR, valueFactory
                    .createIRI("http://dbpedia.org/resource/Vienna_University_of_Technology"))
                .build();
            connection.exportStatements(null, null, null, true, new StatementCollector(dumpModel));
            return new SimpleResourceDto(dumpIRI, dumpModel);
        }
    }
}
