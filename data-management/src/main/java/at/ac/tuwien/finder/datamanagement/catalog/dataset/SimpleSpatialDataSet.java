package at.ac.tuwien.finder.datamanagement.catalog.dataset;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.catalog.DataCatalog;
import at.ac.tuwien.finder.vocabulary.DCAT;
import at.ac.tuwien.finder.vocabulary.SDMX_CODE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * This class is an implementation of {@link SpatialDataSet}.
 *
 * @author Kevin Haller
 */
public class SimpleSpatialDataSet implements SpatialDataSet {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSpatialDataSet.class);

    public static final String[] KEYWORDS =
        new String[] {"spatial", "campus", "facility", "geometry", "map"};
    public static final String TITLE =
        "Dataset concerning spatial information about the Vienna University of Technology.";

    private TripleStoreManager tripleStoreManager;

    /**
     * Creates a new instance of {@link SimpleSpatialDataSet}.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that manages the store for this data
     *                           source.
     */
    public SimpleSpatialDataSet(TripleStoreManager tripleStoreManager) {
        this.tripleStoreManager = tripleStoreManager;
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            if (!connection.hasStatement(NS, RDF.TYPE, DCAT.Dataset, true)) {
                connection.add(initSpatialDataSetDescription(new Date()), DataCatalog.NS);
            }
        }
    }

    /**
     * Initializes the description of the spatial dataset about the Vienna University of Technology.
     *
     * @param issued the {@link Date}, where the initialization was issued.
     * @return {@link Model} that contains the description of this {@link SimpleSpatialDataSet}.
     */
    private Model initSpatialDataSetDescription(Date issued) {
        logger.debug("New {} initialized.", this);
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        Model spatialDataModel = new LinkedHashModel();
        spatialDataModel.add(NS, RDF.TYPE, DCAT.Dataset);
        spatialDataModel.add(NS, DCTERMS.TITLE, valueFactory.createLiteral(TITLE, "en"));
        for (String keyword : KEYWORDS) {
            spatialDataModel.add(NS, DCAT.keyword, valueFactory.createLiteral(keyword));
        }
        spatialDataModel.add(NS, DCTERMS.ISSUED, valueFactory.createLiteral(issued));
        spatialDataModel.add(NS, DCTERMS.SPATIAL, DataCatalog.GEONAMES_VIENNA);
        spatialDataModel.add(NS, DCTERMS.PUBLISHER, DataCatalog.TU_VIENNA);
        spatialDataModel.add(NS, DCTERMS.ACCRUAL_PERIODICITY, SDMX_CODE.freq_A);
        return spatialDataModel;
    }

    @Override
    public IRI namespace() {
        return NS;
    }

    @Override
    public synchronized void modifiedAt(Date modificationDate) {
        logger.debug("Modification update ({}) for {}.", modificationDate, this);
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            connection.add(NS, DCTERMS.MODIFIED,
                connection.getValueFactory().createLiteral(modificationDate));
        }
    }

    @Override
    public Model description() {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Model descriptionModel = new LinkedHashModel();
            connection.prepareGraphQuery(QueryLanguage.SPARQL, String.format("DESCRIBE <%s>", NS))
                .evaluate(new StatementCollector(descriptionModel));
            return descriptionModel;
        }
    }

}
