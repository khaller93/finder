package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.RDFMapper;

/**
 * @author Kevin Haller
 */
public class SimpleResourceDto extends AbstractResourceDto {

    public SimpleResourceDto(IResourceIdentifier resourceIdentifier, Model model) {
        assert resourceIdentifier != null;
        assert model != null;
        super.id(resourceIdentifier.iriValue());
        super.setModel(model);
    }
}
