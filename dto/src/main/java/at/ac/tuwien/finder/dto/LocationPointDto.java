package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import at.ac.tuwien.finder.vocabulary.GeoSPARQL;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.outofbits.opinto.annotations.RdfProperty;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Optional;

/**
 * This class is an implementation of {@link FeatureDto}.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://www.opengis.net/ont/sf#Point")
public class LocationPointDto extends AbstractResourceDto implements GeometryDto {

    private String wktString;

    public LocationPointDto() {

    }

    /**
     * Creates an instance of {@link AbstractResourceDto} with the {@link IResourceIdentifier}
     * describing the entity, and {@link Model} that contains all statements.
     *
     * @param resourceIRI {@link IResourceIdentifier} identifying the resource that is described by
     *                    this dto.
     * @param model       {@link Model} that contains all statements describing the entity.
     */
    public LocationPointDto(IResourceIdentifier resourceIRI, Model model) {
        assert resourceIRI != null;
        assert model != null;
        super.id(resourceIRI.iriValue());
        super.setModel(model);
    }

    @RdfProperty(value = "http://www.opengis.net/ont/geosparql#asWKT")
    public void setWKT(String wktString){
        this.wktString = wktString;
    }

    public String getWkt(){
        return wktString;
    }

    @Override
    public String asGeoJSON() {
        return null;
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
