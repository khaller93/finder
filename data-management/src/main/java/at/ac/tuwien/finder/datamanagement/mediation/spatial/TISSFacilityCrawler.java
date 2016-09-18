package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Instances of this interface represent crawler that gather information about the rooms and the
 * corresponding reservations that are exposed on TISS.
 *
 * @author Kevin Haller
 * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
 */
public interface TISSFacilityCrawler extends AutoCloseable {

    String TISS_FACILITY_XHTML_PROTOTYP =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<tiss_facility>%(info)s</tiss_facility>";

    String TISS_ROOM_SCHEDULE_URL = "https://tiss.tuwien.ac.at/events/roomSchedule.xhtml";
    String TISS_ROOM_SET_XHTML_PROTOTYP = "<roomSet>%(rooms)s</roomSet>";
    String TISS_ROOM_SCHEDULE_XHTML_PROTOTYP =
        "<room>" + "<title>%(title)s</title>" + "<id>%(id)s</id>"
            + "<buildingId>%(buildingId)s</buildingId>" + "<location>%(location)s</location>"
            + "<roomNumber>%(roomNumber)s</roomNumber>"
            + "<roomCapacity>%(roomCapacity)s</roomCapacity>"
            + "<schedule_table>%(schedule_table)s</schedule_table>" + "</room>";

    String TISS_BUILDING_SET_XHTML_PROTOTYP = "<buildingSet>%(buildings)s</buildingSet>";
    String TISS_BUILDING_XHTML_PROTOTYP =
        "<building>" + "<id>%(id)s</id>" + "<info>%(info)s</info>" + "<rooms>%(roomIds)s</rooms>"
            + "</building>";

    /**
     * Gets all the building identifiers, which has been exposed on TISS.
     *
     * @return all the building identifiers, which has been exposed on TISS.
     * @throws DataAcquireException if the crawling was not possible as expected (this may be no
     *                              connection to TISS).
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    Collection<String> allBuildingsIdentifiers() throws DataAcquireException;


    /**
     * Gets all the building identifiers with the corresponding description, which has been exposed
     * on TISS.
     *
     * @return all the building identifiers, which has been exposed on TISS.
     * @throws DataAcquireException if the crawling was not possible as expected (this may be no
     *                              connection to TISS).
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    Map<String, String> allBuildingsDescriptions() throws DataAcquireException;

    /**
     * Returns the identifiers of all buildings and the corresponding information about the building
     * that are exposed on the room site of TISS.
     *
     * @return the identifier of all buildings that are exposed on the room site of TISS.
     * @throws DataAcquireException if the crawling was not possible as expected (this may be no
     *                              connection to TISS).
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    String allBuildingsInformation() throws DataAcquireException;

    /**
     * Returns the identifiers of all rooms of the given tiss building that are exposed on
     * the room site of TISS. The given identifier of the building must not be null.
     *
     * @param buildingTISSIdentifier the tiss identifier of the building of which
     *                               the rooms shall be returned.
     * @return the identifiers of all rooms of the given tiss building that are exposed on
     * the room site of TISS.
     * @throws IllegalArgumentException if the given buildingTISSIdentifier is null.
     * @throws DataAcquireException     if the crawling was not possible as expected (this may be
     *                                  no connection to TISS) or if the building can not be found
     *                                  during the crawling.
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    Collection<String> allRoomIdentifiersOf(String buildingTISSIdentifier)
        throws DataAcquireException;

    /**
     * Returns the schedule of the room between the initial and the end date as well as
     * meta-information. The schedule is returned as XHTML table. The returned string is a valid
     * X(H)M(T)L document of the following form.
     * <p>
     * <room>
     * <title><!-- Label of the room --></title>
     * <id><!-- TISS ID --></id>
     * <buildingID><!-- The TISS id of the building, where the room is located --></buildingID>
     * <location><!-- Location information about the room --></location>
     * <roomCapacity><!-- The capacity of the room --></roomCapacity>
     * <roomNumber><!-- The room number that uniquely identify it --></roomNumber>
     * <schedule_table><!-- The schedule XHTML table --></schedule_table>
     * </room>
     *
     * @param roomTISSIdentifier the identifier of the tiss room, of which the schedule shall be
     *                           returned.
     * @param initialDate        the initial date, where to begin.
     * @param endDate            the end date, where to end.
     * @return the schedule of the room for the time between the initial and the end date as XHTML
     * table.
     * @throws DataAcquireException if the crawling was not possible as expected (this may be no
     *                              connection to TISS) or if the room can not be found during
     *                              the crawling.
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    String scheduleOf(String roomTISSIdentifier, Date initialDate, Date endDate)
        throws DataAcquireException;

    /**
     * Returns the schedule of all rooms of the Vienna University of Technology that are exposed
     * on the room site of TISS between the given initial and the end date. The response is
     * returned as XHTML document in the following format:
     * <p>
     * <facility>
     * <room><!-- The information about the room. --></room>
     * ...
     * </facility>
     *
     * @param initialDate the initial date, where to begin.
     * @param endDate     the end date, where to end.
     * @return Returns the schedule of all rooms of the Vienna University of Technology, which are
     * exposed on the room site of TISS.
     * @throws DataAcquireException if the crawling was not possible as expected (this may be no
     *                              connection to TISS).
     * @see <a href="https://tiss.tuwien.ac.at/events/roomSchedule.xhtml">TISS room schedule</a>.
     */
    String scheduleOfAllRooms(Date initialDate, Date endDate) throws DataAcquireException;
}
