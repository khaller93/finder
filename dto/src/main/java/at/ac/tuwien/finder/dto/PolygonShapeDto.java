package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;

import java.util.Optional;

/**
 * @author Kevin Haller
 */
public class PolygonShapeDto extends AbstractResourceDto implements GeometryDto {
    /**
     * Creates an instance of {@link AbstractResourceDto} with the {@link IResourceIdentifier}
     * describing the entity, and {@link Model} that contains all statements.
     *
     * @param resourceIRI {@link IResourceIdentifier} identifying the resource that is described by
     *                    this dto.
     * @param model       {@link Model} that contains all statements describing the entity.
     */
    public PolygonShapeDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }

    @Override
    public String asWKT() {
        Optional<Literal> wktLiteral =
            Models.objectLiteral(getModel().filter(getIRI().iriValue(), GeoSPARQL.asWKT, null));
        if (wktLiteral.isPresent()) {
            return wktLiteral.get().stringValue();
        }
        return null;
    }
}