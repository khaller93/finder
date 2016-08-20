package at.ac.tuwien.lod.vocabulary;

import at.ac.tuwien.lod.vocabulary.exception.OntologyAccessException;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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

    private static final String DC_ONT_MANAGER_CONF_PATH = "config/ont-manager.rdf";
    private static final String SPATIAL_ONTOLOGY_PATH = "local/tuViennaSpatialOntology.ttl";
    private static final String ORGANIZATION_ONTOLOGY_PATH = "local/tuWienOrganization.ttl";

    /* ontology models */
    private static OntModel spatialOntology;
    private static OntModel organizationOntology;

    /**
     * Gets the ontology that models the domain of spatial information about a university.
     *
     * @return the ontology that models the domain of spatial information about a university.
     * @throws OntologyAccessException if the ontology can not be accessed.
     */
    public static OntModel getSpatialOntology() throws OntologyAccessException {
        if (spatialOntology == null) {
            spatialOntology = readOntology(SPATIAL_ONTOLOGY_PATH, RDFLanguages.TURTLE);
        }
        return spatialOntology;
    }

    /**
     * Gets the ontology that models the domain of the organizational structure of an university.
     *
     * @return the ontology that models the domain of the organizational structure of an university.
     * @throws OntologyAccessException if the ontology can not be accessed.
     */
    public static OntModel getUniversityOrganizationOntology() throws OntologyAccessException {
        if (organizationOntology == null) {
            organizationOntology = readOntology(ORGANIZATION_ONTOLOGY_PATH, RDFLanguages.TURTLE);
        }
        return organizationOntology;
    }

    /**
     * Reads in the ontology of the given path.
     *
     * @param path     the path of the ontology file.
     * @param language the language (rdf) of the ontology file.
     * @return the ontology of the given path.
     * @throws OntologyAccessException if the ontology can not be accessed.
     */
    private static OntModel readOntology(String path, Lang language)
        throws OntologyAccessException {
        OntModel ontologyModel = ModelFactory.createOntologyModel();
        OntDocumentManager documentManager = ontologyModel.getDocumentManager();
        documentManager.setMetadataSearchPath(DC_ONT_MANAGER_CONF_PATH, true);
        try (InputStream facilityOntStream = VocabularyManager.class.getClassLoader()
            .getResourceAsStream(path)) {
            ontologyModel.read(facilityOntStream, null, language.getName());
        } catch (IOException e) {
            logger.error("The ontology {} can not be read in. {}", path, e);
            throw new OntologyAccessException(e);
        }
        logger.debug(ontologyModel.toString());
        return ontologyModel;
    }

    /**
     * Gets all core ontologies. The key of the map is the namespace of the ontology.
     *
     * @return all core ontologies.
     * @throws OntologyAccessException if the ontology can not be accessed.
     */
    public static Map<String, OntModel> getCoreOntologies() throws OntologyAccessException {
        Map<String, OntModel> coreOntologies = new HashMap<>();
        coreOntologies.put(TuWienFacility.getURI(), getSpatialOntology());
        coreOntologies.put(TuWienOrganization.getURI(), getUniversityOrganizationOntology());
        return coreOntologies;
    }

}
