package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.dto.rdf.Property;
import at.ac.tuwien.finder.dto.rdf.Resource;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.Identifiable;
import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;
import java.util.Set;

/**
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Room")
public class RoomDto extends AbstractFeatureDto implements FeatureDto {

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
