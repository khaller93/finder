package at.ac.tuwien.finder.dto;

import org.eclipse.rdf4j.model.Model;

/**
 * @author Kevin Haller
 */
public class SimpleResourceDto extends AbstractResourceDto {

    public SimpleResourceDto(IResourceIdentifier resourceIRI, Model model) {
        super(resourceIRI, model);
    }
}
