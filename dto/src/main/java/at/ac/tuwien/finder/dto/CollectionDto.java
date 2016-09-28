package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.Object;

import java.util.List;

/**
 * Instances of this class represents a RDF collection.
 *
 * @param <T> the type of object.
 * @author Kevin Haller
 */
public interface CollectionDto<T extends Object> extends Dto {

    /**
     * Gets the objects in this collection in form of a list.
     *
     * @return the objects in this RDF collection in form of a list.
     */
    List<T> asList();

}
