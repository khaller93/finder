package at.ac.tuwien.finder.vocabulary;

import at.ac.tuwien.finder.vocabulary.exception.OntologyAccessException;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
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
public final class VocabularyManager {

    private static final Logger logger = LoggerFactory.getLogger(VocabularyManager.class);

    private static final String ONTOLOGY_BASE = "http://finder.tuwien.ac.at/vocab/spatial#";
    private static final String DC_ONT_MANAGER_CONF_PATH = "config/ontmanager-specification.rdf";
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

    private OntModel coreOntology;
    private Map<String, Model> ontologyMap;

    /**
     * Creates a new instance of {@link VocabularyManager}.
     */
    private VocabularyManager() throws OntologyAccessException {
        coreOntology = initCoreOntology();
        ontologyMap = initRawOntologyModels();
    }

    /**
     * Gets the ontology that models the domain of spatial information about a university.
     *
     * @return the ontology that models the domain of spatial information about a university.
     */
    public OntModel getCoreOntology() {
        return coreOntology;
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
        Model spatialOntologyModel = ModelFactory.createDefaultModel();
        try (InputStream spatialOntologyStream = VocabularyManager.class.getClassLoader()
            .getResourceAsStream(SPATIAL_ONTOLOGY_PATH)) {
            RDFDataMgr.read(spatialOntologyModel, spatialOntologyStream, Lang.TURTLE);
            ontologyMap.put("spatial", spatialOntologyModel);
        } catch (IOException e) {
            logger.error("The spatial ontology located at '{}' cannot be read in. {}",
                SPATIAL_ONTOLOGY_PATH, e);
            throw new OntologyAccessException(e);
        }
        return ontologyMap;
    }

    /**
     * Initializes the core ontology.
     *
     * @return {@link OntModel} that contains a spatial ontology.
     * @throws OntologyAccessException if the core ontology cannot be initiated.
     */
    private OntModel initCoreOntology() throws OntologyAccessException {
        OntModel ontologyModel = ModelFactory.createOntologyModel();
        OntDocumentManager documentManager = ontologyModel.getDocumentManager();
        documentManager.setMetadataSearchPath(DC_ONT_MANAGER_CONF_PATH, true);
        try (InputStream spatialOntologyStream = VocabularyManager.class.getClassLoader()
            .getResourceAsStream(SPATIAL_ONTOLOGY_PATH)) {
            ontologyModel.read(spatialOntologyStream, ONTOLOGY_BASE, Lang.TURTLE.getName());
        } catch (IOException e) {
            logger.error("The spatial ontology located at '{}' cannot be read in. {}",
                SPATIAL_ONTOLOGY_PATH, e);
            throw new OntologyAccessException(e);
        }
        logger.debug("Initialized core ontology {}.", ontologyModel.toString());
        return ontologyModel;
    }

}
