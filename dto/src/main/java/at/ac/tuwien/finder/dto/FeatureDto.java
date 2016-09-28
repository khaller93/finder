package at.ac.tuwien.finder.dto;

import java.util.Collection;

/**
 * This interface is a subclass of {@link ResourceDto} that is holding a spatial feature.
 *
 * @author Kevin Haller
 */
public interface FeatureDto extends ResourceDto {

    /**
     * Gets collection of {@link GeometryDto}s of this feature, or empty, if there is no geometry
     * shapes of this feature.
     *
     * @return collection of {@link GeometryDto}s of this feature, or empty, if there is no geometry
     * shapes of this feature.
     */
    Collection<GeometryDto> getGeometryShapes();

}
