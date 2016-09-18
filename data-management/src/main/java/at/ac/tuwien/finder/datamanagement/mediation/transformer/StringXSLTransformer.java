package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;

/**
 * This is an implementation of {@link DataTransformer} that uses XSLT for the transformation of
 * semi-structured data into linked data (rdf). The format of the semi-structured data is a string.
 *
 * @author Kevin Haller
 */
public class StringXSLTransformer implements DataTransformer<String> {

    private XSLTransformer xslTransformer;

    /**
     * Creates a new xslt transformer with the given stylesheet.
     *
     * @param stylesheetPath the xslt stylesheet, which shall be used to transform the data.
     * @param rdfLanguage    the rdf language of the result of the transformation.
     * @throws DataTransformationException if the transformer cannot be setup.
     */
    public StringXSLTransformer(String stylesheetPath, RDFFormat rdfLanguage)
        throws DataTransformationException {
        this.xslTransformer = new XSLTransformer(stylesheetPath, rdfLanguage);
    }

    /**
     * Creates a new xslt transformer with the given stylesheet.
     *
     * @param stylesheetStream stream of the xslt stylesheet, which shall be used to transform the
     *                         data.
     * @param rdfLanguage      the rdf language of the result of the transformation.
     * @throws DataTransformationException if the transformer cannot be setup.
     */
    public StringXSLTransformer(InputStream stylesheetStream, RDFFormat rdfLanguage)
        throws DataTransformationException {
        this.xslTransformer = new XSLTransformer(stylesheetStream, rdfLanguage);
    }

    @Override
    public Model transform(String data) throws DataTransformationException {
        if (data == null) {
            throw new IllegalArgumentException("The given data must not be null.");
        }
        if (xslTransformer == null) {
            throw new DataTransformationException(
                "The setup of the transformer has not been successful, so this method can not be called successfully.");
        }
        return xslTransformer.transform(new StreamSource(new StringReader(data)));
    }

    @Override
    public void close() throws Exception {
       xslTransformer.close();
    }

}
