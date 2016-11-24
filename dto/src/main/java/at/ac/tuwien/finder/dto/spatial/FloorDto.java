package at.ac.tuwien.finder.dto.spatial;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an implementation of {@link FeatureDto} that represents a floor.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Floor")
public class FloorDto extends BuildingUnitDto {

    private FloorDto floorBeneath;

    /**
     * Piles up the given collection of {@link FloorDto}s. It is assumed that each floor is above
     * a given floor, or null (if at the bottom).
     *
     * @param floorDtos collection of {@link FloorDto}s that shall be piled up.
     * @return a list sorted from he floor at the bottom to the floor at the top.
     */
    public static List<FloorDto> pileUp(Collection<FloorDto> floorDtos) {
        List<FloorDto> piledUpFloorDtos = new LinkedList<>();
        for (FloorDto currentFloorDto : floorDtos) {
            int index = piledUpFloorDtos.indexOf(currentFloorDto.getFloorBeneath());
            piledUpFloorDtos.add(index != -1 ? index : 0, currentFloorDto);
        }
        return piledUpFloorDtos;
    }

    public FloorDto getFloorBeneath() {
        return floorBeneath;
    }

    @RdfProperty(value = "http://finder.tuwien.ac.at/vocab/spatial#aboveFloor")
    public void setFloorBeneath(FloorDto aboveFloor) {
        this.floorBeneath = aboveFloor;
    }
}
