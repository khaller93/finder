package at.ac.tuwien.finder.dto.spatial;

import org.outofbits.opinto.annotations.RdfsClass;
import org.outofbits.opinto.annotations.SuperclassOf;

/**
 * This is an marker interface that represents building units.
 *
 * @author Kevin Haller
 */
@RdfsClass(value = "http://finder.tuwien.ac.at/vocab/spatial#BuildingUnit")
@SuperclassOf(subclasses = {RoomDto.class, FloorDto.class, BuildingTractDto.class,
    FloorSectionDto.class})
public class BuildingUnitDto extends AbstractFeatureDto implements FeatureDto {

}
