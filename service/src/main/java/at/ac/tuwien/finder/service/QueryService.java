package at.ac.tuwien.finder.service;

/**
 * Instances of this interface represents {@link IService}s that
 *
 * @author Kevin Haller
 */
public interface QueryService extends IService {

    /**
     * Gets the SPARQL query that shall be executed in order to cover this service.
     *
     * @return the SPARQL query that shall be executed in order to cover this service.
     */
    String getQuery();

}
