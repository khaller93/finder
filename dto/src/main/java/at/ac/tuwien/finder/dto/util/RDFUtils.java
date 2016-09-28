package at.ac.tuwien.finder.dto.util;

import at.ac.tuwien.finder.dto.Dto;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides methods for RDF handling.
 *
 * @author Kevin Haller
 */
public final class RDFUtils {

    /**
     * Gets the first object literal that can be found using the given properties for the given
     * {@link Resource} in the {@link Model} of this {@link Dto}.
     *
     * @param model             {@link Model} that contains the information that shell be fetched.
     * @param resource          {@link Resource} for which the first object literal shall be returned.
     * @param preferredLanguage the language that is preferred.
     * @param properties        the properties that shall be used for finding a object literal
     * @return {@link Optional} string value that is potentially empty, if no object literal could
     * be found.
     */
    public static Optional<String> getFirstLiteralFor(Model model, Resource resource,
        String preferredLanguage, IRI... properties) {
        for (IRI property : properties) {
            Map<String, String> literalMap = new HashMap<>();
            model.filter(resource, property, (IRI) null).objects().stream()
                .filter(objectValue -> objectValue instanceof Literal).forEach(objectValue -> {
                Literal literal = (Literal) objectValue;
                Optional<String> optionalLanguageCode = literal.getLanguage();
                if (optionalLanguageCode.isPresent()) {
                    literalMap.put(optionalLanguageCode.get(), literal.getLabel());
                } else {
                    literalMap.put(null, literal.getLabel());
                }
            });
            if (literalMap.isEmpty()) {
                continue;
            } else if (literalMap.containsKey(preferredLanguage)) {
                return Optional.of(literalMap.get(preferredLanguage));
            } else if (literalMap.containsKey(null)) {
                return Optional.of(literalMap.get(null));
            } else if (literalMap.containsKey("en")) {
                return Optional.of(literalMap.get("en"));
            } else {
                return literalMap.values().stream().findFirst();
            }
        }
        return Optional.empty();
    }

}
