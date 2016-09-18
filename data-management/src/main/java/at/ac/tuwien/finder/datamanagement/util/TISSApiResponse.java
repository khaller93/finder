package at.ac.tuwien.finder.datamanagement.util;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This class represents a new api response for TISS.
 *
 * @author Kevin Haller
 */
public class TISSApiResponse {

    private DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

    private String content;

    /**
     * Creates an new class that manages the content of the api response.
     *
     * @param content the content from the response of an api call.
     */
    public TISSApiResponse(String content) {
        assert content != null;
        this.content = content;
    }

    /**
     * Returns the content of the response of the api call as a string.
     *
     * @return the content of the response of the api call as a string.
     */
    public String content() {
        return content;
    }

    /**
     * Gets the response as xml document.
     *
     * @return the response of the api call as xml document.
     */
    public Document xmlDocument() throws DataAcquireException {
        try {
            DocumentBuilder docBuilder = documentFactory.newDocumentBuilder();
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                content.getBytes())) {
                return docBuilder.parse(byteArrayInputStream);
            } catch (SAXException | IOException e) {
                throw new DataAcquireException(e);
            }
        } catch (ParserConfigurationException e) {
            throw new DataAcquireException("The building of the xml response failed.");
        }
    }


}
