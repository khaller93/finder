package at.ac.tuwien.finder.datamanagement.integration;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import at.ac.tuwien.finder.datamanagement.catalog.exception.DataCatalogException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is an implementation of {@link DataIntegrator}, that uses no {@link DataLinker} or
 * {@link DataCleanser}.
 *
 * @author Kevin Haller
 */
public class SimpleDataIntegrator implements DataIntegrator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDataIntegrator.class);

    private IRI dataSetNameSpace;
    private Map<IRI, IRI> dataSetMapping;
    private TripleStoreManager tripleStoreManager = TripleStoreManager.getInstance();

    /**
     * Creates a new instance of {@link SimpleDataIntegrator} for the given {@link DataSet}.
     *
     * @param dataSetNameSpace the namespace {@link IRI} of the data set into which the data shall
     *                         be integrated.
     */
    public SimpleDataIntegrator(IRI dataSetNameSpace) {
        this(dataSetNameSpace, Collections.EMPTY_MAP);
    }

    /**
     * Creates a new instance of {@link SimpleDataIntegrator} for the given {@link DataSet}. Uses
     * the given mapping of resources to datasets. Resources
     *
     * @param dataSetNameSpace the namespace {@link IRI} of the data set into which the data shall
     *                         be integrated.
     * @param dataSetMapping   specifies which resources shall be integrated into which
     *                         {@link DataSet}.
     */
    public SimpleDataIntegrator(IRI dataSetNameSpace, Map<IRI, IRI> dataSetMapping) {
        assert dataSetNameSpace != null;
        this.dataSetNameSpace = dataSetNameSpace;
        this.dataSetMapping = dataSetMapping == null ? Collections.EMPTY_MAP : dataSetMapping;
    }

    @Override
    public void integrate(Model model) {
        logger.debug("integrate({})", model);
        if (model == null) {
            throw new IllegalArgumentException("The given model must not be null.");
        }
        Map<IRI, List<Statement>> partitionedModel =
            model.stream().collect(Collectors.groupingBy(statement -> {
                for (IRI subjectNs : dataSetMapping.keySet()) {
                    if (statement.getSubject().stringValue().startsWith(subjectNs.stringValue())) {
                        return subjectNs;
                    }
                }
                return dataSetNameSpace;
            }));
        logger.debug("Partition-Model: {}", partitionedModel);
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            for (IRI dataSetIRI : partitionedModel.keySet()) {
                try {
                    connection.add(partitionedModel.get(dataSetIRI), dataSetIRI);
                    tripleStoreManager.getDataCatalog().get(dataSetIRI)
                        .modifiedAt(new Date());
                } catch (DataCatalogException d) {
                    logger.error("{}", d);
                }
            }
        } catch (RepositoryException e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (tripleStoreManager != null) {
            tripleStoreManager.close();
        }
    }
}
