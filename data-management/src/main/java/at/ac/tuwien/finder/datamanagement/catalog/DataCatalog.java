package at.ac.tuwien.finder.datamanagement.catalog;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.factory.DataSetFactory;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.factory.exception.DataSetFactoryException;
import at.ac.tuwien.finder.datamanagement.catalog.exception.DataCatalogException;
import at.ac.tuwien.finder.vocabulary.DCAT;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class represents a data catalog.
 *
 * @author Kevin Haller
 */
public class DataCatalog {

    private static final Logger logger = LoggerFactory.getLogger(DataCatalog.class);

    public static final IRI NS;
    public static final IRI TU_VIENNA;
    public static final IRI GEONAMES_VIENNA;

    public static final String TITLE = "Catalog of Finder app (TU Vienna)";

    static {
        Properties dataCatalogProperties = new Properties();
        try (InputStream propertiesStream = TripleStoreManager.class.getClassLoader()
            .getResourceAsStream("config/datamanagement.properties")) {
            dataCatalogProperties.load(propertiesStream);
        } catch (IOException e) {
            logger.error("The property file for data-manegement cannot be accessed. {}", e);
            System.exit(1);
        }
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        NS = valueFactory.createIRI(TripleStoreManager.BASE.stringValue(), "catalog");
        TU_VIENNA =
            valueFactory.createIRI("http://dbpedia.org/resource/Vienna_University_of_Technology");
        GEONAMES_VIENNA = valueFactory.createIRI("http://sws.geonames.org/2761333/");
    }


    private TripleStoreManager tripleStoreManager;
    private ConcurrentHashMap<IRI, DataSet> dataSetMap = new ConcurrentHashMap<>();

    /**
     * Creates a new instance of {@link DataCatalog} with the given {@link TripleStoreManager}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for managing the data
     *                           catalog.
     */
    public DataCatalog(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            if (!connection.hasStatement(NS, RDF.TYPE, DCAT.Catalog, true)) {
                connection.add(initializeDataCatalogStatements(), NS);
            } else {
                for (Value dataSetResource : Iterations
                    .stream(connection.getStatements(NS, DCAT.dataset, null))
                    .map(Statement::getObject).filter(value -> value instanceof IRI)
                    .collect(Collectors.toList())) {
                    dataSetMap.put((IRI) dataSetResource,
                        DataSetFactory.createDataSet((IRI) dataSetResource, tripleStoreManager));
                }
            }
        } catch (DataSetFactoryException e) {
            logger.error("Dataset cannot be established for a persisted data set resource. {}", e);
        }
    }

    /**
     * Initialize the basic statements of this data catalog.
     *
     * @return a collection of statements describing the catalog of this application.
     */
    private Model initializeDataCatalogStatements() {
        logger.debug("Initializes the data catalog.");
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        Model catModel = new LinkedHashModel();
        catModel.add(NS, RDF.TYPE, DCAT.Catalog);
        catModel.add(NS, DCTERMS.TITLE, valueFactory.createLiteral(TITLE, "en"));
        catModel.add(NS, RDFS.LABEL, valueFactory.createLiteral(TITLE, "en"));
        catModel.add(NS, DCTERMS.PUBLISHER, TU_VIENNA);
        return catModel;
    }

    /**
     * Gets the {@link DataSet} for the given namespace {@link IRI}. If there is not already a
     * {@link DataSet} with this namespace, a new one will be created and the metadata persisted.
     *
     * @param namespace the namespace {@link IRI} for which the corresponding {@link DataSet} shall
     *                  be returned.
     * @return the {@link DataSet} for the given namespace {@link IRI}.
     * @throws DataCatalogException if the {@link DataSet} for the given namespace {@link IRI}
     *                              cannot be created.
     */
    public synchronized DataSet get(IRI namespace) throws DataCatalogException {
        if (dataSetMap.containsKey(namespace)) {
            return dataSetMap.get(namespace);
        }
        try {
            DataSet dataSet = DataSetFactory.createDataSet(namespace, tripleStoreManager);
            if (dataSet != null) {
                try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
                    connection.add(NS, DCAT.dataset, namespace);
                }
            }
            return dataSet;
        } catch (DataSetFactoryException f) {
            throw new DataCatalogException(f);
        }
    }

}
