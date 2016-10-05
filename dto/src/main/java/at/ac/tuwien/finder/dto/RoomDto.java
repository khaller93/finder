package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

import java.util.Collection;

/**
 * This class is an implementation of {@link FeatureDto} that represent a room.
 *
 * @author Kevin Haller
 */
public class RoomDto extends AbstractResourceDto implements FeatureDto {
    /**
     * Creates an instance of {@link RoomDto} with the {@link IRI} describing the entity,
     * and {@link Model} that contains all statements.
     *
     * @param resourceIRI {@link IResourceIdentifier} identifying the resource that is described by
     *                    this dto.
     * @param model       {@link Model} that contains all statements describing the entity.
     */
    public RoomDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }

    @Override
    public Collection<GeometryDto> getGeometryShapes() {
        return null;
    }
}
