package at.ac.tuwien.finder.vocabulary;

import at.ac.tuwien.finder.vocabulary.exception.OntologyAccessException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the vocabulary of the finder application including the local vocabularies and
 * the external vocabulary, on which the local vocabularies of the finder application depends.
 *
 * @author Kevin Haller
 */
public class VocabularyManager {

    private static final Logger logger = LoggerFactory.getLogger(VocabularyManager.class);

    private static final String ONTOLOGY_BASE = "http://finder.tuwien.ac.at/vocab/spatial#";
    private static final String SPATIAL_ONTOLOGY_PATH = "local/tuViennaSpatialOntology.ttl";

    private static VocabularyManager vocabularyManager;

    /**
     * Gets an instance of {@link VocabularyManager} that is designed to be a singleton class.
     *
     * @return an instance of {@link VocabularyManager} that is designed to be a singleton class.
     * @throws OntologyAccessException if the core ontology cannot be accessed.
     */
    public static VocabularyManager getInstance() throws OntologyAccessException {
        if (vocabularyManager == null) {
            vocabularyManager = new VocabularyManager();
        }
        return vocabularyManager;
    }

    private Map<String, Model> ontologyMap;

    /**
     * Creates a new instance of {@link VocabularyManager}.
     */
    private VocabularyManager() throws OntologyAccessException {
        ontologyMap = initRawOntologyModels();
    }

    /**
     * Gets a raw {@link Model} of the local spatial ontology.
     *
     * @return a raw {@link Model} of the local spatial ontology.
     */
    public Model getSpatialOntology() {
        return ontologyMap.get("spatial");
    }

    /**
     * Reads in all local ontologies and stores them into an ontology map.
     *
     * @return the map containing {@link Model} for each local ontology.
     * @throws OntologyAccessException if a local ontology cannot be accessed.
     */
    private Map<String, Model> initRawOntologyModels() throws OntologyAccessException {
        Map<String, Model> ontologyMap = new HashMap<>();
        Model spatialOntologyModel = new LinkedHashModel();
        try (InputStream spatialOntologyStream = VocabularyManager.class.getClassLoader()
            .getResourceAsStream(SPATIAL_ONTOLOGY_PATH)) {
            RDFParser turtleParser = Rio.createParser(RDFFormat.TURTLE);
            turtleParser.setRDFHandler(new StatementCollector(spatialOntologyModel));
            turtleParser.parse(spatialOntologyStream, ONTOLOGY_BASE);
            ontologyMap.put("spatial", spatialOntologyModel);
        } catch (IOException | RDFHandlerException | RDFParseException e) {
            logger.error("The spatial ontology located at '{}' cannot be read in. {}",
                SPATIAL_ONTOLOGY_PATH, e);
            throw new OntologyAccessException(e);
        }
        return ontologyMap;
    }

    /**
     * For testing purposes.
     */
    public static void setVocabularyManager(VocabularyManager vocabularyManager){
        VocabularyManager.vocabularyManager = vocabularyManager;
    }

}
