package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.outofbits.opinto.Identifiable;
import org.outofbits.opinto.Memorable;

import java.io.IOException;
import java.net.URI;

/**
 * Instances of this interface represents a Dto.
 *
 * @author Kevin Haller
 */
public interface Dto extends Identifiable, Memorable {

    /**
     * Gets the {@link URI} of the entity that is described by this {@link Dto}.
     *
     * @return the {@link URI} of the entity that is described by this
     * {@link Dto}.
     */
    IResourceIdentifier getIRI();

    /**
     * Gets the label of this entity, if a label exists, otherwise null. The preferred language is
     * English, if there is no language-neutral label.
     *
     * @return the label of this entity, if a label exists, otherwise null. The preferred language is
     * English, if there is no language-neutral label.
     */
    default String getLabel() {
        return getLabel(null);
    }

    /**
     * Gets the label of this entity, if a label exists, otherwise null. The given language will be
     * preferred. If no label with the given preferred language cannot be found, English will be
     * chosen.
     *
     * @param preferredLanguageCode the language that shall be preferred for the label.
     * @return the label of this entity, if a label exists, otherwise null.
     */
    String getLabel(String preferredLanguageCode);


    /**
     * Gets the description of this entity, if a description exists, otherwise null. The preferred
     * language is English, if there is no language-neutral label.
     *
     * @return the description of this entity, if a description exists, otherwise null. The preferred
     * language is English, if there is no language-neutral label.
     */
    default String getDescription() {
        return getDescription(null);
    }

    /**
     * Gets the description of this entity, if a description exists, otherwise null. The given
     * language will be preferred. If no description with the given preferred language cannot be
     * found, English will be chosen.
     *
     * @param preferredLanguageCode the language that shall be preferred for the description.
     * @return the description of this entity, if a label exists, otherwise null.
     */
    String getDescription(String preferredLanguageCode);

    /**
     * Transforms this {@link Dto} into a RDF string of the given format.
     *
     * @param format {@link SerializationFormat} the result shall have.
     * @return result of the transformation in the given {@link SerializationFormat}.
     * @throws IOException if the transformation of this {@link Dto} failed due to an IO error.
     */
    String transformTo(SerializationFormat format) throws IOException;
}
