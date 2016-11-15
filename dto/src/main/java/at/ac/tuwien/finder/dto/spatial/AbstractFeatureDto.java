package at.ac.tuwien.finder.dto.spatial;

import at.ac.tuwien.finder.dto.AbstractResourceDto;
import org.outofbits.opinto.annotations.RdfProperty;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an implementation of {@link AbstractFeatureDto}.
 *
 * @author Kevin Haller
 */
public class AbstractFeatureDto extends AbstractResourceDto implements FeatureDto {

    private String spatialIdentifier;

    private LocationPointDto locationPointDto;
    private PolygonShapeDto polygonShapeDto;

    @Override
    public String getSpatialIdentifier() {
        return spatialIdentifier;
    }

    @RdfProperty(value = "http://finder.tuwien.ac.at/vocab/spatial#spatialIdentifier")
    public void setSpatialIdentifier(String spatialIdentifier) {
        this.spatialIdentifier = spatialIdentifier;
    }

    @Override
    public LocationPointDto getLocationPoint() {
        return locationPointDto;
    }

    @RdfProperty(value = "http://www.opengis.net/ont/geosparql#hasGeometry")
    public void setLocationPoint(LocationPointDto locationPointDto) {
        this.locationPointDto = locationPointDto;
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
        List<GeometryDto> geometryShapes = new LinkedList<>();
        if (locationPointDto != null) {
            geometryShapes.add(locationPointDto);
        }
        if (polygonShapeDto != null) {
            geometryShapes.add(polygonShapeDto);
        }
        return geometryShapes;
    }
}
