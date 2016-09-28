package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.Property;
import at.ac.tuwien.finder.dto.rdf.Resource;

import java.util.Set;

/**
 * Instances of this interface represent a {@link Dto} that describes a single resource.
 *
 * @auhtor Kevin Haller
 */
public interface ResourceDto extends Dto {

    /**
     * Gets all the {@link Property}s describing this entity.
     *
     * @return all the {@link Property}s describing this entity.
     */
    Set<Property> getProperties();

    /**
     * Gets the types of this entity.
     *
     * @return the types of this entity.
     */
    Set<Resource> getTypes();

}
