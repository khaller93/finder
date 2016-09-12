package at.ac.tuwien.finder.datamanagement.catalog;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.DataSet;
import at.ac.tuwien.finder.datamanagement.catalog.dataset.SpatialDataSet;
import at.ac.tuwien.finder.vocabulary.DCAT;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * This class represents a data catalog.
 *
 * @author Kevin Haller
 */
public class DataCatalog {

    private static final Logger logger = LoggerFactory.getLogger(DataCatalog.class);

    public static final IRI NS;
    public static final IRI TU_VIENNA;

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
    }


    private TripleStoreManager tripleStoreManager;
    private List<DataSet> dataSetList = new LinkedList<>();

    /**
     * Creates a new instance of {@link DataCatalog} with the given {@link TripleStoreManager}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for managing the data
     *                           catalog.
     */
    public DataCatalog(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
        dataSetList.add(new SpatialDataSet(tripleStoreManager));
        RepositoryConnection connection = null;
        try {
            connection = tripleStoreManager.getConnection();
            if (!connection.prepareBooleanQuery(QueryLanguage.SPARQL,
                String.format("ASK { <%s> a <%s> . }", NS, DCAT.Catalog)).evaluate()) {
                connection.add(initializeDataCatalogStatements(), NS);
            }
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Initialize the basic statements of this data catalog.
     *
     * @return a collection of statements describing the catalog of this application.
     */
    private Collection<Statement> initializeDataCatalogStatements() {
        IRI datCatResource = NS;
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        List<Statement> statementList = new LinkedList<>();
        statementList.add(valueFactory.createStatement(datCatResource, RDF.TYPE, DCAT.Dataset));
        statementList.add(valueFactory.createStatement(datCatResource, DCTERMS.TITLE,
            valueFactory.createLiteral("Catalog of Finder app (TU Vienna)", "en")));
        statementList.add(valueFactory.createStatement(datCatResource, RDFS.LABEL,
            valueFactory.createLiteral("Catalog of Finder app (TU Vienna)", "en")));
        statementList
            .add(valueFactory.createStatement(datCatResource, DCTERMS.PUBLISHER, TU_VIENNA));
        return statementList;
    }


}
