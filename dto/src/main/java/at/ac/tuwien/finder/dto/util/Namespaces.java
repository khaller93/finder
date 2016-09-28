package at.ac.tuwien.finder.dto.util;

import at.ac.tuwien.finder.vocabulary.*;
import org.eclipse.rdf4j.model.vocabulary.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents all known namespaces.
 *
 * @author Kevin Haller
 */
public final class Namespaces {

    private static final Map<String, String> nameSpaceMap = new HashMap<>();

    static {
        nameSpaceMap.put(RDF.NAMESPACE, RDF.PREFIX);
        nameSpaceMap.put(RDFS.NAMESPACE, RDFS.PREFIX);
        nameSpaceMap.put(OWL.NAMESPACE, OWL.PREFIX);
        nameSpaceMap.put(XMLSchema.NAMESPACE, XMLSchema.PREFIX);
        nameSpaceMap.put("http://finder.tuwien.ac.at/vocab/spatial#", "tuvs");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/building/id/", "building");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/buildingtract/id/", "buildingtract");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/floor/id/", "floor");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/room/id/", "room");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/address/id/", "address");
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/geometry/id/", "geometry");
        nameSpaceMap.put("http://finder.tuwien.ac.at/event/id/", "event");
        nameSpaceMap.put(FOAF.NAMESPACE, FOAF.PREFIX);
        nameSpaceMap.put(LOCN.NS, LOCN.PREFIX);
        nameSpaceMap.put(DCTERMS.NAMESPACE, DCTERMS.PREFIX);
        nameSpaceMap.put(ORG.NS, ORG.PREFIX);
        nameSpaceMap.put(SCHEMA.NS, SCHEMA.PREFIX);
        nameSpaceMap.put("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
        nameSpaceMap.put(GeoSPARQL.NS, GeoSPARQL.PREFIX);
        nameSpaceMap.put(SF.NS, SF.PREFIX);
        nameSpaceMap.put("http://purl.org/vocommons/voaf#", "voaf");
        nameSpaceMap.put("http://purl.org/vocab/vann/", "vann");
    }

    /**
     * Formats a given string into the presentation {@code prefix:LocatorName}, if the namespace
     * for the given string of an IRI is known, otherwise the given IRI is returned without change.
     *
     * @param iri string of an IRI that shall be formatted.
     * @return the result of formatting the given string of an IRI into the presentation
     * {@code prefix:LocatorName}, or the given IRI, if no prefix for the detected namespace can be
     * found.
     */
    public static String format(String iri) {
        if (iri == null) {
            return "iri:none";
        }
        for (String namespace : nameSpaceMap.keySet()) {
            if (iri.startsWith(namespace)) {
                return String.format("%s:%s", nameSpaceMap.get(namespace),
                    iri.replaceAll("^" + namespace, ""));
            }
        }
        return iri;
    }

    /**
     * Formats a given string into the presentation {@code prefix:LocatorName}, if the namespace
     * for the given string of an IRI is known, otherwise the given IRI is returned without change.
     *
     * @param iri
     * @param nameSpace the namespace of the given IRI
     * @param localName the local name of the given IRI that forms the given IRI when appended to
     *                  the given namespace.
     * @return the result of formatting the given string of an IRI into the presentation
     * {@code prefix:LocatorName}, or the given IRI, if no prefix for the detected namespace can be
     * found.
     */
    public static String format(String iri, String nameSpace, String localName) {
        if (iri == null) {
            return "iri:none";
        }
        if (nameSpaceMap.containsKey(nameSpace)) {
            return String.format("%s:%s", nameSpaceMap.get(nameSpace), localName);
        }
        return iri;
    }

}
