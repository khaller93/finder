package at.ac.tuwien.finder.dto;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;

/**
 * This class is an implementation of {@link FeatureDto} that represents a building.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Building")
public class BuildingDto extends AbstractFeatureDto implements FeatureDto {

    private Collection<RoomDto> rooms;
    private Collection<FloorDto> floors;

    @RdfProperty("http://finder.tuwien.ac.at/vocab/spatial#containsBuildingUnit")
    public void setRooms(Collection<RoomDto> rooms) {
        this.rooms = rooms;
    }

    /**
     * Gets all known rooms of this building.
     *
     * @return all known rooms of this building.
     */
    public Collection<RoomDto> getRooms() {
        return rooms;
    }

}
