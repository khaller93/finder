package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

/**
 * @author Kevin Haller
 */
public class VocabularyResourceDto extends AbstractResourceDto {

    public VocabularyResourceDto(IResourceIdentifier resourceIRI, Model model) {
        assert resourceIRI != null;
        assert model != null;
        super.id(resourceIRI.iriValue());
        super.setModel(model);
    }
}
