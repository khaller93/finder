package at.ac.tuwien.finder.dto.spatial;

import at.ac.tuwien.finder.dto.ResourceDto;

import java.util.Collection;

/**
 * This interface is a subclass of {@link ResourceDto} that is holding a spatial feature.
 *
 * @author Kevin Haller
 */
public interface FeatureDto extends ResourceDto {

    /**
     * Gets the spatial identifier of this feature.
     *
     * @return the spatial identifier of this feature.
     */
    String getSpatialIdentifier();

    /**
     * Gets the location of this feature in form of a {@link LocationPointDto}.
     *
     * @return {@link LocationPointDto} of this feature, or null, if there is no location point.
     */
    LocationPointDto getLocationPoint();

    /**
     * Gets the shape of this feature in form of a {@link PolygonShapeDto}.
     *
     * @return default {@link PolygonShapeDto} of this feature, or null, if there is no location
     * point.
     */
    PolygonShapeDto getDefaultShape();

    /**
     * Gets collection of {@link GeometryDto}s of this feature, or empty, if there is no geometry
     * shapes of this feature.
     *
     * @return collection of {@link GeometryDto}s of this feature, or empty, if there is no geometry
     * shapes of this feature.
     */
    Collection<GeometryDto> getGeometryShapes();

}
