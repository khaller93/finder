package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import at.ac.tuwien.finder.vocabulary.SF;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * This class is an implementation of {@link DataTransformer} that transforms WKT shape files
 * serialized into comma-separated CSV.
 *
 * @author Kevin Haller
 */
public class WKTTransformer implements DataTransformer<Collection<CSVRecord>> {

    private final static Logger logger = LoggerFactory.getLogger(WKTTransformer.class);

    @Override
    public Model transform(Collection<CSVRecord> data) throws DataTransformationException {
        logger.debug("transforms CSV string ({}) by {}", data, this);
        Model resultModel = new LinkedHashModel();
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        for (CSVRecord record : data) {
            IRI featureIRI = valueFactory.createIRI(record.get("id"));
            IRI geometryIRI = valueFactory.createIRI(TripleStoreManager.BASE.stringValue(), String
                .format("spatial/geometry/id/polygon:%s",
                    record.get("id").replaceAll("^(.*)/spatial/", "").replaceAll("/", "-")));
            resultModel.add(featureIRI, RDF.TYPE, valueFactory.createIRI(record.get("type")));
            resultModel.add(featureIRI, GeoSPARQL.hasGeometry, geometryIRI);
            resultModel.add(geometryIRI, RDF.TYPE, valueFactory.createIRI(record.get("form")));
            resultModel.add(geometryIRI, GeoSPARQL.asWKT,
                valueFactory.createLiteral(record.get("WKT"), GeoSPARQL.wktLiteral));
        }
        logger.debug("Result of the transformation processed by {}: {}", this, resultModel);
        return resultModel;
    }

    @Override
    public void close() throws Exception {

    }
}
