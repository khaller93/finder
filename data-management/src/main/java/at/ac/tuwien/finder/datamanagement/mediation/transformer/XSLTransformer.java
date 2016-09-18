package at.ac.tuwien.finder.datamanagement.mediation.transformer;

import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.method.Base64EncodingMethod;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.method.PostalAddressMatcherMethod;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * This is an implementation of {@link DataTransformer} that uses XSLT for the transformation of
 * semi-structured data into linked data (rdf).
 *
 * @author Kevin Haller
 */
public class XSLTransformer implements DataTransformer<Source> {

    private static final Logger logger = LoggerFactory.getLogger(StringXSLTransformer.class);

    private static Configuration transformerConfiguration = Configuration.newConfiguration();

    static {
        transformerConfiguration.registerExtensionFunction(new Base64EncodingMethod());
        transformerConfiguration.registerExtensionFunction(new PostalAddressMatcherMethod());
    }

    private Transformer transformer;
    private RDFFormat rdfLanguage;

    /**
     * Creates a new xslt transformer with the given stylesheet.
     *
     * @param stylesheetPath the xslt stylesheet, which shall be used to transform the data.
     * @param rdfLanguage    the rdf language of the result of the transformation.
     * @throws DataTransformationException if the transformer cannot be setup.
     */
    public XSLTransformer(String stylesheetPath, RDFFormat rdfLanguage)
        throws DataTransformationException {
        this.rdfLanguage = rdfLanguage;
        TransformerFactory transformerFactory =
            new TransformerFactoryImpl(transformerConfiguration);
        try (FileInputStream stylesheetStream = new FileInputStream(stylesheetPath)) {
            transformer = transformerFactory.newTransformer(new StreamSource(stylesheetStream));
        } catch (FileNotFoundException e) {
            logger.error("{}", e);
            throw new DataTransformationException(
                String.format("The stylesheet file can not be found. %s", e));
        } catch (IOException e) {
            logger.error("{}", e);
            throw new DataTransformationException(
                String.format("The stylesheet file can not be read. %s", e));
        } catch (TransformerConfigurationException e) {
            logger.error("{}", e);
            throw new DataTransformationException(
                String.format("The transformer can not be setup. %s", e));
        }
    }

    /**
     * Creates a new xslt transformer with the given stylesheet.
     *
     * @param stylesheetStream stream of the xslt stylesheet, which shall be used to transform the
     *                         data.
     * @param rdfLanguage      the rdf language of the result of the transformation.
     * @throws DataTransformationException if the transformer cannot be setup.
     */
    public XSLTransformer(InputStream stylesheetStream, RDFFormat rdfLanguage)
        throws DataTransformationException {
        this.rdfLanguage = rdfLanguage;
        TransformerFactory transformerFactory =
            new TransformerFactoryImpl(transformerConfiguration);
        try {
            transformer = transformerFactory.newTransformer(new StreamSource(stylesheetStream));
        } catch (TransformerConfigurationException e) {
            logger.error("{}", e);
            throw new DataTransformationException(
                String.format("The transformer can not be setup. %s", e));
        }
    }

    @Override
    public Model transform(Source source) throws DataTransformationException {
        if (transformer == null) {
            throw new DataTransformationException(
                "The setup of the transformer has not been successful, so this method can not be called successfully.");
        }
        try (StringWriter stringWriter = new StringWriter()) {
            transformer.transform(source, new StreamResult(stringWriter));
            String rdfResponse = stringWriter.toString();
            logger.debug("transform({}) -> {}", source,
                rdfResponse.replaceAll("\\r|\\n", "").replaceAll(" ++", " "));
            try (ByteArrayInputStream rdfResponseStream = new ByteArrayInputStream(
                rdfResponse.getBytes())) {
                return Rio.parse(rdfResponseStream, "", rdfLanguage);
            } catch (RDFParseException e) {
                logger.error("transform({}) throws {}", source, e);
                throw new DataTransformationException(e);
            }
        } catch (TransformerException | IOException io) {
            logger.error("transform({}) throws {}", source, io);
            throw new DataTransformationException(io);
        }
    }

    @Override
    public void close() throws Exception {
        /* Nothing to do */
    }
}
