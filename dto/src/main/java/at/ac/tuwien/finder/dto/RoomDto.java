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
public class BRoomDto implements FeatureDto, Identifiable {

    private IResourceIdentifier resourceIdentifier;

    private String roomCode;
    private LocationPointDto locationPointDto;
    private PolygonShapeDto polygonShapeDto;


    @Override
    public org.eclipse.rdf4j.model.Resource id() {
        return resourceIdentifier.iriValue();
    }

    @Override
    public void id(org.eclipse.rdf4j.model.Resource theResource) {
        this.resourceIdentifier = new IResourceIdentifier(theResource.stringValue());
    }

    @RdfProperty(value = "http://finder.tuwien.ac.at/vocab/spatial#roomCode", datatype = "xs:string")
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }
    
    @Override
    public IResourceIdentifier getIRI() {
        return resourceIdentifier;
    }

    @Override
    public String getLabel(String preferredLanguageCode) {
        return null;
    }

    @Override
    public String getDescription(String preferredLanguageCode) {
        return null;
    }

    @Override
    public void setModel(Model model) {

    }

    @Override
    public Model getModel() {
        return null;
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

    @Override
    public Set<Property> getProperties() {
        return null;
    }

    @Override
    public Set<Resource> getTypes() {
        return null;
    }
}
