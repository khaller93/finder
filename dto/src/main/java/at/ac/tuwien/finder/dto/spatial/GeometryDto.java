package at.ac.tuwien.finder.dto.spatial;

import at.ac.tuwien.finder.dto.ResourceDto;
import org.outofbits.opinto.annotations.RdfsClass;
import org.outofbits.opinto.annotations.SuperclassOf;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This interface is a subclass of {@link GeometryDto} that is holding a certain geometry.
 *
 * @author Kevin Haller
 */
@RdfsClass(value = "http://www.opengis.net/ont/geosparql#Geometry")
@SuperclassOf(subclasses = {PolygonShapeDto.class, LocationPointDto.class})
public interface GeometryDto extends ResourceDto {

    /**
     * Returns this geometry in form of a GeoJSON string.
     *
     * @return this geometry in form of a GeoJSON string.
     * @see <a href="http://geojson.org/">GeoJSON</a>
     */
    default String asGeoJSON(){
        throw new NotImplementedException();
    }

    /**
     * Returns this geometry in form of a WKT string.
     *
     * @return this geometry in form of a WKT string.
     */
    String asWKT();

}
