package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.exception.FileExtensionUnknownException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.util.*;

/**
 * Represents serialization formats for transforming a {@link Dto} into RDF string.
 *
 * @æuthor Kevin Haller
 */
public enum SerializationFormat {
    RDFXML, TURTLE, JSONLD, N3;

    private RDFFormat rdfFormat;
    private static Map<RDFFormat, SerializationFormat> internalFormatMapping = new HashMap<>();

    static {
        SerializationFormat.RDFXML.rdfFormat = RDFFormat.RDFXML;
        SerializationFormat.TURTLE.rdfFormat = RDFFormat.TURTLE;
        SerializationFormat.JSONLD.rdfFormat = RDFFormat.JSONLD;
        SerializationFormat.N3.rdfFormat = RDFFormat.N3;
        //Set-up internal mapping
        internalFormatMapping.put(RDFFormat.RDFXML, SerializationFormat.RDFXML);
        internalFormatMapping.put(RDFFormat.TURTLE, SerializationFormat.TURTLE);
        internalFormatMapping.put(RDFFormat.JSONLD, SerializationFormat.JSONLD);
        internalFormatMapping.put(RDFFormat.N3, SerializationFormat.N3);
    }

    /**
     * Gets the {@link RDFFormat} to the serialization format.
     *
     * @return {@link RDFFormat} of this serialization format.
     */
    public RDFFormat getRDFFormat() {
        return this.rdfFormat;
    }


    /**
     * Gets the default Mime-Type of this {@link SerializationFormat}.
     *
     * @return the default Mime-Type of this {@link SerializationFormat}.
     */
    public String getDefaultMimeType() {
        return this.rdfFormat.getDefaultMIMEType();
    }

    /**
     * Gets all the Mime-Types of this {@link SerializationFormat}.
     *
     * @return all the Mime-Types of this {@link SerializationFormat}.
     */
    public List<String> getMimeTypes() {
        return this.rdfFormat.getMIMETypes();
    }

    /**
     * Gets the {@link SerializationFormat} for the given file ending.
     *
     * @param fileEnding the file ending for which the {@link SerializationFormat} shall be detected.
     * @return the {@link SerializationFormat} for the given file ending.
     * @throws FileExtensionUnknownException if the file ending cannot be detected.
     */
    public static SerializationFormat getFormatOfFile(String fileEnding)
        throws FileExtensionUnknownException {
        Optional<RDFFormat> parserFormatForFileName =
            Rio.getParserFormatForFileName("a." + fileEnding);
        if (parserFormatForFileName.isPresent()) {
            RDFFormat rdfFormat = parserFormatForFileName.get();
            if (internalFormatMapping.containsKey(rdfFormat)) {
                return internalFormatMapping.get(rdfFormat);
            }
        }
        throw new FileExtensionUnknownException(String
            .format("The given file ending %s is not supported. Supported ones are %s.", fileEnding,
                Arrays.stream(SerializationFormat.values()).map(serializationFormat -> String
                    .join(",", serializationFormat.getRDFFormat().getFileExtensions()))
                    .reduce((a, b) -> a + "," + b).orElse("none")));
    }

    /**
     * Checks if the given content string has a matching mime type to this {@link SerializationFormat}.
     *
     * @param acceptedContentTypeString the content type string for which the acceptance shall be
     *                                  checked.
     * @return true, if there is a match of a certain mime type, otherwise false.
     */
    public boolean accept(String acceptedContentTypeString) {
        return RDFFormat.matchMIMEType(acceptedContentTypeString,
            Collections.singletonList(this.getRDFFormat())).isPresent();
    }

    /**
     * Gets the mime-types for this {@link SerializationFormat}.
     *
     * @return the mime-types for this {@link SerializationFormat}.
     */
    public List<String> mimeTypes() {
        return this.rdfFormat.getMIMETypes();
    }

}
