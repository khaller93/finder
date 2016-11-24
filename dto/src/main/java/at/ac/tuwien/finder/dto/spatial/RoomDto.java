package at.ac.tuwien.finder.dto.spatial;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

/**
 * This class is a room.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Room")
public class RoomDto extends BuildingUnitDto {

    private String roomCode;

    @RdfProperty(value = "http://finder.tuwien.ac.at/vocab/spatial#roomCode", datatype = "xs:string")
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

}
