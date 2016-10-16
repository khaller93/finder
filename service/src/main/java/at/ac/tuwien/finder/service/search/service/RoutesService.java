package at.ac.tuwien.finder.service.search.service;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.dto.Dto;
import at.ac.tuwien.finder.service.IService;
import at.ac.tuwien.finder.service.exception.ServiceException;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import at.ac.tuwien.finder.vocabulary.GeoSPARQLFunction;
import at.ac.tuwien.finder.vocabulary.NAVI;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * This class is an implementation of {@link IService} that search for a route to a given entity.
 *
 * @author Kevin Haller
 */
public class RoutesService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(RoutesService.class);

    private static Map<String, String> queryMap = new HashMap<>();

    static {
        queryMap.put("navi:xOfRoute", "<" + NAVI.xOfRoute.stringValue() + ">");
        queryMap.put("navi:yFeature", "<" + NAVI.yFeature.stringValue() + ">");
        queryMap.put("navi:hasConstraint", "<" + NAVI.hasContraint.stringValue() + ">");
        queryMap.put("gsp:hasGeometry", "<" + GeoSPARQL.hasGeometry.stringValue() + ">");
        queryMap.put("gsp:asWKT", "<" + GeoSPARQL.asWKT.stringValue() + ">");
        queryMap.put("gspf:distance", "<" + GeoSPARQLFunction.distance + ">");
        queryMap.put("uom:metre", "<http://www.opengis.net/def/uom/OGC/1.0/metre>");
    }

    private TripleStoreManager tripleStoreManager;

    private String requestIRIString;
    private String query;

    /**
     * Creates a new instance of {@link RoutesService} for the given time range.
     *
     * @param tripleStoreManager {@link TripleStoreManager} that shall be used for this service.
     * @param requestIRIString   the {@link IRI} of the search request.
     */
    public RoutesService(TripleStoreManager tripleStoreManager, String requestIRIString) {
        assert tripleStoreManager != null;
        this.tripleStoreManager = tripleStoreManager;
        this.requestIRIString = requestIRIString;
        StrSubstitutor queryFormatter = new StrSubstitutor(queryMap, "%(", ")");
        this.query = queryFormatter.replace(
            "SELECT ?routeFeature ?route ?yRouteFeature ?distance\n" + "WHERE {\n"
                + "    values (?startFeature ?endFeature) { (<http://finder.tuwien.ac.at/spatial/accessunit/id/HEEGM1> <http://finder.tuwien.ac.at/spatial/elevator/id/HFEG02C>) }.\n"
                + "    ?startFeature (%(navi:xOfRoute)/%(navi:yFeature))+ ?routeFeature .\n"
                + "    ?routeFeature %(navi:xOfRoute) ?route ;\n"
                + "        %(gsp:hasGeometry) [ %(gsp:asWKT) ?routeFeatureGeom ] .\n"
                + "    ?route %(navi:yFeature) ?yRouteFeature .\n"
                + "    ?yRouteFeature (%(navi:xOfRoute)/%(navi:yFeature))* ?endFeature ;\n"
                + "        %(gsp:hasGeometry) [ %(gsp:asWKT) ?yRouteFeatureGeom ] .\n"
                + "    FILTER(?routeFeature != ?yRouteFeature) .\n"
                + "    BIND(%(gspf:distance)(?routeFeatureGeom, ?yRouteFeatureGeom, %(uom:metre)) as ?distance) .\n"
                + "    FILTER NOT EXISTS {\n"
                + "    \t?route %(navi:hasConstraint) <todo://fillIn> .\n" + "\t}\n" + "}");
        logger.debug("Roots query: {}", query);
    }

    @Override
    public Dto execute() throws ServiceException {
        try (RepositoryConnection connection = tripleStoreManager.getConnection()) {
            Map<IRI, PriorityQueue<Edge>> pathMap = new HashMap<>();
            TupleQueryResult pathTupleResultIterator =
                connection.prepareTupleQuery(QueryLanguage.SPARQL, this.query).evaluate();
            while (pathTupleResultIterator.hasNext()) {
                BindingSet bindingSet = pathTupleResultIterator.next();
                IRI routeNode = (IRI) bindingSet.getBinding("routeFeature").getValue();
                System.out.println(bindingSet.getBinding("yRouteFeature"));
                Edge edge = new Edge((IRI) bindingSet.getBinding("route").getValue(),
                    (IRI) bindingSet.getBinding("yRouteFeature").getValue(), Double.parseDouble(
                    ((Literal) bindingSet.getBinding("distance").getValue()).getLabel()));
                if (!pathMap.containsKey(routeNode)) {
                    PriorityQueue<Edge> priorityEdgeQueue =
                        new PriorityQueue<>((o1, o2) -> Double.compare(o1.distance, o2.distance));
                    priorityEdgeQueue.add(edge);
                    pathMap.put(routeNode, priorityEdgeQueue);
                } else {
                    pathMap.get(routeNode).add(edge);
                }
                System.out.println(pathMap.toString());
            }
            return null;
        }
    }

    /**
     * This is a private class representing an edge of a path from node to another.
     */
    private class Edge {

        private IRI routeIRI;
        private IRI yFeature;
        private double distance;


        public Edge(IRI routeIRI, IRI yFeature, double distance) {
            this.routeIRI = routeIRI;
            this.yFeature = yFeature;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Edge edge = (Edge) o;

            return routeIRI != null ? routeIRI.equals(edge.routeIRI) : edge.routeIRI == null;

        }

        @Override
        public int hashCode() {
            return routeIRI != null ? routeIRI.hashCode() : 0;
        }
    }

}
