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
 * @author Kevin Haller
 */
@RdfsClass("http://www.opengis.net/ont/sf#Polygon")
public class PolygonShapeDto extends AbstractResourceDto implements GeometryDto {

    private String wktString;


    public PolygonShapeDto() {
    }

    public PolygonShapeDto(IResourceIdentifier resourceIdentifier, Model model) {
        assert resourceIdentifier != null;
        assert model != null;
        super.id(resourceIdentifier.iriValue());
        super.setModel(model);
    }

    @RdfProperty(value = "http://www.opengis.net/ont/geosparql#asWKT", datatype = "xs:string")
    public void setWKT(String wktString){
        this.wktString = wktString;
    }

    public String getWkt(){
        return wktString;
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
