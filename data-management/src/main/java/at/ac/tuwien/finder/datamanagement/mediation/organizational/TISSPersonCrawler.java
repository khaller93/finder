package at.ac.tuwien.finder.datamanagement.mediation.organizational;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import org.w3c.dom.Document;

import java.util.Collection;

/**
 * Instances of this class represents a crawler that gather information about a
 * person or a list of persons.
 *
 * @author Kevin Haller
 */
public interface TISSPersonCrawler extends AutoCloseable {

    /**
     * Returns the information of the person with the given oid, which has been exposed on TISS. The
     * returned document will be a xml document, which corresponds to the response of the TISS API.
     *
     * @param personOId the oid of the person, of which the information shall be retrieved.
     * @return the information of the person with the given oid.
     * @throws DataAcquireException if the crawling of the information was not successful.
     */
    Document getInformationOfPerson(String personOId) throws DataAcquireException;

    /**
     * Returns the information of the list of persons with the given oids, which has been
     * exposed on TISS. The returned document will be a xml document, which corresponds to the
     * response of the TISS API.
     *
     * @param personOIdList list of the oids of those persons, for whom the information, which has
     *                      been exposed on TISS, shall be returned.
     * @return the information of the person with the given oid.
     * @throws DataAcquireException if the crawling of the information was not successful.
     */
    Document getInformationOfPersons(Collection<String> personOIdList) throws DataAcquireException;

}
