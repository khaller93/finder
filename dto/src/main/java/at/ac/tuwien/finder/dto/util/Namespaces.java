package at.ac.tuwien.finder.dto.util;

import at.ac.tuwien.finder.vocabulary.*;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.model.vocabulary.ORG;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents all known namespaces.
 *
 * @author Kevin Haller
 */
public final class Namespaces implements Iterable<Namespace> {

    private static final Map<String, Namespace> nameSpaceMap = new HashMap<>();

    static {
        nameSpaceMap.put(RDF.NAMESPACE, new SimpleNamespace(RDF.PREFIX, RDF.NAMESPACE));
        nameSpaceMap.put(RDFS.NAMESPACE, new SimpleNamespace(RDFS.PREFIX, RDFS.NAMESPACE));
        nameSpaceMap.put(OWL.NAMESPACE, new SimpleNamespace(OWL.PREFIX, OWL.NAMESPACE));
        nameSpaceMap
            .put(XMLSchema.NAMESPACE, new SimpleNamespace(XMLSchema.PREFIX, XMLSchema.NAMESPACE));
        nameSpaceMap.put(TUVS.NS, new SimpleNamespace(TUVS.PREFIX, TUVS.NS));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/building/id/",
            new SimpleNamespace("building", "http://finder.tuwien.ac.at/spatial/building/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/buildingtract/id/",
            new SimpleNamespace("buildingtract",
                "http://finder.tuwien.ac.at/spatial/buildingtract/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/floor/id/",
            new SimpleNamespace("floor", "http://finder.tuwien.ac.at/spatial/floor/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/room/id/",
            new SimpleNamespace("room", "http://finder.tuwien.ac.at/spatial/room/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/address/id/",
            new SimpleNamespace("address", "http://finder.tuwien.ac.at/spatial/address/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/spatial/geometry/id/",
            new SimpleNamespace("geometry", "http://finder.tuwien.ac.at/spatial/geometry/id/"));
        nameSpaceMap.put("http://finder.tuwien.ac.at/event/id/",
            new SimpleNamespace("event", "http://finder.tuwien.ac.at/event/id/"));
        nameSpaceMap.put(FOAF.NAMESPACE, new SimpleNamespace(FOAF.PREFIX, FOAF.NAMESPACE));
        nameSpaceMap.put(LOCN.NS, new SimpleNamespace(LOCN.PREFIX, LOCN.NS));
        nameSpaceMap.put(DCTERMS.NAMESPACE, new SimpleNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE));
        nameSpaceMap.put(ORG.NAMESPACE, new SimpleNamespace(ORG.PREFIX, ORG.NAMESPACE));
        nameSpaceMap.put(SCHEMA.NS, new SimpleNamespace(SCHEMA.PREFIX, SCHEMA.NS));
        nameSpaceMap.put("http://www.w3.org/2003/01/geo/wgs84_pos#",
            new SimpleNamespace("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"));
        nameSpaceMap.put(GeoSPARQL.NS, new SimpleNamespace(GeoSPARQL.PREFIX, GeoSPARQL.NS));
        nameSpaceMap.put(SF.NS, new SimpleNamespace(SF.PREFIX, SF.NS));
        nameSpaceMap.put(DCAT.NAMESPACE, new SimpleNamespace(DCAT.PREFIX, DCAT.NAMESPACE));
        nameSpaceMap.put("http://purl.org/vocommons/voaf#",
            new SimpleNamespace("voaf", "http://purl.org/vocommons/voaf#"));
        nameSpaceMap.put("http://purl.org/vocab/vann/",
            new SimpleNamespace("vann", "http://purl.org/vocab/vann/"));
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
     * @param iri       the IRI that shall be formatted.
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
            return String.format("%s:%s", nameSpaceMap.get(nameSpace).getPrefix(), localName);
        }
        return iri;
    }

    @Override
    public Iterator<Namespace> iterator() {
        return nameSpaceMap.values().iterator();
    }
}
