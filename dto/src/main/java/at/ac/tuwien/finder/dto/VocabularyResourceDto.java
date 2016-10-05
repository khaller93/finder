package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.Model;

/**
 * @author Kevin Haller
 */
public class VocabularyResourceDto extends AbstractResourceDto {

    public VocabularyResourceDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }
}
