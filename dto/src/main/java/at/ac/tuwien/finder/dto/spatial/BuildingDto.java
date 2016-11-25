package at.ac.tuwien.finder.dto.spatial;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class is an implementation of {@link FeatureDto} that represents a building.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Building")
public class BuildingDto extends AbstractFeatureDto implements FeatureDto {

    private Collection<BuildingUnitDto> buildingUnits;

    private AddressDto addressDto;

    @RdfProperty("http://finder.tuwien.ac.at/vocab/spatial#containsBuildingUnit")
    public void setBuildingUnits(Collection<BuildingUnitDto> buildingUnits) {
        this.buildingUnits = buildingUnits;
    }

    public AddressDto getAddress() {
        return addressDto;
    }

    @RdfProperty("http://www.w3.org/ns/locn#address")
    public void setAddress(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    public Collection<BuildingUnitDto> getBuildingUnits() {
        return buildingUnits;
    }

    /**
     * Gets all known rooms of this building.
     *
     * @return all known rooms of this building.
     */
    public Collection<RoomDto> getRooms() {
        return getBuildingUnits().stream().filter(buildingUnit -> buildingUnit instanceof RoomDto)
            .map(buildingUnit -> (RoomDto) buildingUnit).collect(Collectors.toList());
    }

    /**
     * Gets all known floors of this building.
     *
     * @return all known floors of this building.
     */
    public Collection<FloorDto> getFloors() {
        return getBuildingUnits().stream().filter(buildingUnit -> buildingUnit instanceof FloorDto)
            .map(buildingUnit -> (FloorDto) buildingUnit).collect(Collectors.toList());
    }


}
