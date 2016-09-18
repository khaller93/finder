package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * This is an implementation of {@link DataTransformer} that uses XSLT for the transformation of
 * semi-structured data into linked data (rdf). The format of the semi-structured data is a dom
 * document.
 *
 * @author Kevin Haller
 */
public class DOMXSLTransformer implements DataTransformer<Document> {
    private XSLTransformer xslTransformer;

    /**
     * Creates a new xslt transformer with the given stylesheet.
     *
     * @param stylesheetPath the xslt stylesheet, which shall be used to transform the data.
     * @param rdfLanguage    the rdf language of the result of the transformation.
     * @throws DataTransformationException if the transformer cannot be setup.
     */
    public DOMXSLTransformer(String stylesheetPath, RDFFormat rdfLanguage)
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
    public DOMXSLTransformer(InputStream stylesheetStream, RDFFormat rdfLanguage)
        throws DataTransformationException {
        this.xslTransformer = new XSLTransformer(stylesheetStream, rdfLanguage);
    }

    @Override
    public Model transform(Document data) throws DataTransformationException {
        if (data == null) {
            throw new IllegalArgumentException("The given data must not be null.");
        }
        if (xslTransformer == null) {
            throw new DataTransformationException(
                "The setup of the transformer has not been successful, so this method can not be called successfully.");
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            try(StringWriter stringWriter = new StringWriter()) {
                transformer.transform(new DOMSource(data), new StreamResult(stringWriter));
                try(StringReader stringReader = new StringReader(stringWriter.toString())){
                    return xslTransformer.transform(new StreamSource(stringReader));
                }
            } catch (IOException | TransformerException e) {
                throw new DataTransformationException(e);
            }
        } catch (TransformerConfigurationException e) {
            throw new DataTransformationException(e);
        }
        //return xslTransformer.transform(new DOMSource(data));
    }

    @Override
    public void close() throws Exception {
        xslTransformer.close();
    }
}
