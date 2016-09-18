package at.ac.tuwien.finder.datamanagement.mediation.organizational;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import org.w3c.dom.Document;

import java.util.Collection;

/**
 * Instances of this class represents a crawler that gather information about an
 * organization and the sub-organizations, which is exposed on TISS.
 *
 * @author Kevin Haller
 */
public interface TISSOrganizationCrawler extends AutoCloseable {

    /**
     * Gets the information about the organization with the given number (an unique identifier,
     * which usually starts with E) and all the descendants (sub-organizations) of it.
     * The response has the following X(HT)ML format (the tree-structure is flattened):
     * <p/>
     * {@code <organisations>
     * <orgunit><!-- Information about the sub-organizations --></orgunit>
     * <orgunit><!-- Information about one sub-organization --></orgunit>
     * <orgunit><!-- Information about a further sub-organization --></orgunit>
     * ...
     * </organizations> }
     *
     * @param number the number (an unique identifier, which usually starts with E) of the organization.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document traverseOrganizationInformationByNumber(String number) throws DataAcquireException;


    /**
     * Gets the information about the organizations, which has one of the given number code (an
     * unique identifier, which usually starts with E) and all the descendants (sub-organizations)
     * of it. The response has the following X(HT)ML format (the tree-structure is flattened):
     * <p/>
     * {@code <organisations>
     * <orgunit><!-- Information about the sub-organizations --></orgunit>
     * <orgunit><!-- Information about one sub-organization --></orgunit>
     * <orgunit><!-- Information about a further sub-organization --></orgunit>
     * ...
     * </organizations> }
     *
     * @param listOfNumberCodes the list of number codes of the organizations, of which the information
     *                          shall be returned.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document traverseOrganizationInformationWithNumberCodes(Collection<String> listOfNumberCodes)
        throws DataAcquireException;

    /**
     * Gets the information about the organizations, which has one of the given organization
     * identifier and all the descendants (sub-organizations)
     * of it. The response has the following X(HT)ML format (the tree-structure is flattened):
     * <p/>
     * {@code <organisations>
     * <orgunit><!-- Information about the sub-organizations --></orgunit>
     * <orgunit><!-- Information about one sub-organization --></orgunit>
     * <orgunit><!-- Information about a further sub-organization --></orgunit>
     * ...
     * </organizations> }
     *
     * @param listOfIds the list of number codes of the organizations, of which the information
     *                  shall be returned.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document traverseOrganizationInformationWithIds(Collection<String> listOfIds)
        throws DataAcquireException;

    /**
     * Gets the information about the organization with the given id and all the
     * sub-organization of it. The response has the following X(HT)ML format:
     * (the tree-structure is flattened)
     * <p/>
     * {@code <organizations>
     * <orgunit><!-- Information about the organizations --></orgunit>
     * <orgunit><!-- Information about one sub-organization --></orgunit>
     * <orgunit><!-- Information about a further sub-organization --></orgunit>
     * ...
     * </organizations> }
     *
     * @param oid the id of the organization.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document traverseOrganizationInformationById(String oid) throws DataAcquireException;

    /**
     * Gets the information about the organizations with the given ids.
     * The response has the following X(HT)ML format:
     * <p/>
     * {@code <organizations>
     * <orgunit><!-- Information about the organizations --></orgunit>
     * <orgunit><!-- Information about one sub-organization --></orgunit>
     * <orgunit><!-- Information about a further sub-organization --></orgunit>
     * ...
     * </organizations> }
     *
     * @param listOfIds the ids of the organization, of which the information that has been exposed
     *                  on TISS shall be returned.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document getOrganizationInformationByIds(Collection<String> listOfIds)
        throws DataAcquireException;

    /**
     * Gets the information about the organization with the given number (an unique identifier,
     * which usually starts with E). The response has the following X(HT)ML format:
     * <p/>
     * {@code <orgunit><!-- Information about the organizations --></orgunit> }
     *
     * @param number the number (an unique identifier, which usually starts with E) of the organization.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document getOrganizationInformationByNumber(String number) throws DataAcquireException;

    /**
     * Gets the information about the organization with the given number (an unique identifier,
     * which usually starts with E). The response has the following X(HT)ML format:
     * <p/>
     * {@code <orgunit><!-- Information about the organizations --></orgunit> }
     *
     * @param oid the id of the organization.
     * @return the information about the organization as X(H)(T)ML response.
     * @throws DataAcquireException if the crawling was not possible as expected (there may be no
     *                              connection to TISS).
     */
    Document getOrganizationInformationById(String oid) throws DataAcquireException;

}
