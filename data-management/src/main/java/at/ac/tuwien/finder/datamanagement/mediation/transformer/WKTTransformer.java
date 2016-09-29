package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import at.ac.tuwien.finder.vocabulary.SF;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.io.IOException;
import java.io.StringReader;

/**
 * This class is an implementation of {@link DataTransformer} that transforms WKT shape files
 * serialized into comma-separated CSV.
 *
 * @author Kevin Haller
 */
public class WKTTransformer implements DataTransformer<String> {

    @Override
    public Model transform(String data) throws DataTransformationException {
        System.out.println(data);
        try (StringReader csvDataReader = new StringReader(data)) {
            Model resultModel = new LinkedHashModel();
            ValueFactory valueFactory = SimpleValueFactory.getInstance();
            for (CSVRecord record : CSVFormat.DEFAULT.parse(csvDataReader).getRecords()) {
                IRI geometryIRI = valueFactory.createIRI(TripleStoreManager.BASE.stringValue(),
                    String.format("spatial/geometry/id/polygon:%s",
                        record.get(2).replaceAll("^(.*)/spatial/", "").replaceAll("/", "-")));
                resultModel
                    .add(valueFactory.createIRI(record.get(2)), GeoSPARQL.hasGeometry, geometryIRI);
                resultModel.add(geometryIRI, RDF.TYPE, SF.Polygon);
                resultModel.add(geometryIRI, GeoSPARQL.asWKT,
                    valueFactory.createLiteral(record.get(0), GeoSPARQL.wktLiteral));
            }
            return resultModel;
        } catch (IOException e) {
            throw new DataTransformationException(e);
        }
    }

    @Override
    public void close() throws Exception {

    }
}
