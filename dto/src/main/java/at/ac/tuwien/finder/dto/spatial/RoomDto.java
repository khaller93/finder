package at.ac.tuwien.finder.dto.spatial;

import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;

/**
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Room")
public class RoomDto extends BuildingUnitDto {

    private String roomCode;
    private LocationPointDto locationPointDto;
    private PolygonShapeDto polygonShapeDto;

    @RdfProperty(value = "http://finder.tuwien.ac.at/vocab/spatial#roomCode", datatype = "xs:string")
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @RdfProperty(value = "http://www.opengis.net/ont/geosparql#hasGeometry")
    public void setLocationPoint(LocationPointDto locationPointDto) {
        this.locationPointDto = locationPointDto;
    }

    @Override
    public LocationPointDto getLocationPoint() {
        return locationPointDto;
    }

    @RdfProperty(value = "http://www.opengis.net/ont/geosparql#hasGeometry")
    public void setDefaultShape(PolygonShapeDto polygonShapeDto) {
        this.polygonShapeDto = polygonShapeDto;
    }

    @Override
    public PolygonShapeDto getDefaultShape() {
        return polygonShapeDto;
    }

    @Override
    public Collection<GeometryDto> getGeometryShapes() {
        return null;
    }
}
