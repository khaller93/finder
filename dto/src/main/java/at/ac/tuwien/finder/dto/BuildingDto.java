package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.vocabulary.SF;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is an implementation of {@link FeatureDto} that represents a building.
 *
 * @author Kevin Haller
 */
public class BuildingDto extends AbstractResourceDto implements FeatureDto {

    /**
     * Creates a new {@link BuildingDto}.
     *
     * @param resourceIRI {@link IResourceIdentifier} of the building.
     * @param model       {@link Model} that contains the building.
     */
    public BuildingDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }

    /**
     * Gets the {@link LocationPointDto}s of this building  or a empty list, if there is no
     * location point for this building.
     *
     * @return {@link LocationPointDto} of this feature, or a empty list, if there is no
     * location point for this building.
     */
    public Collection<LocationPointDto> getLocations() {
        return getModel().filter(null, RDF.TYPE, SF.Point).subjects().stream()
            .filter(resource -> resource instanceof IRI).map(
                resource -> new LocationPointDto(new IResourceIdentifier(resource.stringValue()),
                    getModel().filter(resource, null, null))).collect(Collectors.toSet());
    }

    @Override
    public Collection<GeometryDto> getGeometryShapes() {
        return Stream.concat(getLocations().stream(), getIndoorPlanShape().stream())
            .collect(Collectors.toList());
    }


    public Collection<PolygonShapeDto> getIndoorPlanShape() {
        return getModel().filter(null, RDF.TYPE, SF.Polygon).subjects().stream()
            .filter(resource -> resource instanceof IRI).map(
                resource -> new PolygonShapeDto(new IResourceIdentifier(resource.stringValue()),
                    getModel().filter(resource, null, null))).collect(Collectors.toSet());
    }
}
