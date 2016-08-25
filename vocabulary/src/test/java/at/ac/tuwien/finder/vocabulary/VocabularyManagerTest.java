package at.ac.tuwien.finder.vocabulary;

import at.ac.tuwien.finder.vocabulary.exception.OntologyAccessException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

/**
 * This class tests the {@link VocabularyManager}.
 *
 * @author Kevin Haller
 */
public class VocabularyManagerTest {

    private VocabularyManager vocabularyManager;
    private static Dataset dataset;

    @BeforeClass
    public static void setUpClass() throws IOException {
        dataset = DatasetFactory.create();
        try (InputStream vocabDemoStream = VocabularyManager.class.getClassLoader()
            .getResourceAsStream("spatialDemo.ttl");
            InputStream inconsistencyVocabDemoStream = VocabularyManager.class.getClassLoader()
                .getResourceAsStream("inconsistencyDemo.ttl");
            InputStream locationVocabDemoStream = VocabularyManager.class.getClassLoader()
                .getResourceAsStream("locationDemo.ttl");
            InputStream geoVocabDemoStream = VocabularyManager.class.getClassLoader()
                .getResourceAsStream("geoDemo.ttl");) {
            RDFDataMgr.read(dataset.getNamedModel("demo"), vocabDemoStream, Lang.TURTLE);
            RDFDataMgr.read(dataset.getNamedModel("inconsistency"), inconsistencyVocabDemoStream,
                Lang.TURTLE);
            RDFDataMgr
                .read(dataset.getNamedModel("location"), locationVocabDemoStream, Lang.TURTLE);
            RDFDataMgr.read(dataset.getNamedModel("geo"), geoVocabDemoStream, Lang.TURTLE);
        }
    }

    @Before
    public void setUp() throws OntologyAccessException {
        vocabularyManager = VocabularyManager.getInstance();
    }

    @Test
    public void get_raw_spatial_ontology() {
        Model spatialOntology = vocabularyManager.getSpatialOntology();
        assertTrue("The class 'Building' of the spatial ontology must be part of the raw model.",
            spatialOntology.containsResource(TuViennaSpatialOntology.Building));
        assertThat("The class Building must have two rdfs:label, 'Building and 'Gebäude'",
            spatialOntology.listObjectsOfProperty(TuViennaSpatialOntology.Building, RDFS.label)
                .toList().stream().map(RDFNode::asLiteral).map(Literal::getString)
                .collect(Collectors.toList()), containsInAnyOrder("Building", "Gebäude"));
    }

    @Test
    public void reasoning_of_transitive_contain_property_ok() {
        Model baseModel = dataset.getNamedModel("demo");
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(vocabularyManager.getCoreOntology());
        InfModel infmodel = ModelFactory.createInfModel(reasoner, baseModel);
        assertTrue("The knowledge base must be valid.", infmodel.validate().isValid());
        assertThat(
            "Building with id A must also contain the building units with ids AA01 and AC01, due to the transitive characteristics of 'fs:containsBuildingUnit'.",
            infmodel.listObjectsOfProperty(resource("spatial/building/id/A"),
                TuViennaSpatialOntology.containsBuildingUnit).toList(),
            hasItems(resource("spatial/unit/id/AA01"), resource("spatial/unit/id/AC01")));
    }

    @Test
    public void check_isa_location_core_vocabulary_simple_reasoning() {
        Model baseModel = dataset.getNamedModel("location");
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(vocabularyManager.getCoreOntology());
        InfModel infmodel = ModelFactory.createInfModel(reasoner, baseModel);
        assertTrue("The knowledge base must be valid.", infmodel.validate().isValid());
        assertThat("Address 'AT1040-1c56fcbcb8725edda11e2c76a1d21c77-13' must be a 'locn:Address'.",
            infmodel.listSubjectsWithProperty(RDF.type, Locn.Address).toList(), containsInAnyOrder(
                resource("spatial/address/id/AT1040-1c56fcbcb8725edda11e2c76a1d21c77-13")));
    }

    @Test
    public void check_geosparql_simple_reasoning() {
        Model baseModel = dataset.getNamedModel("geo");
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(vocabularyManager.getCoreOntology());
        InfModel infmodel = ModelFactory.createInfModel(reasoner, baseModel);
        assertTrue("The knowledge base must be valid.", infmodel.validate().isValid());
        assertThat("Resources with id 'A' as well as 'AA' must be both a ogc:Feature.",
            infmodel.listSubjectsWithProperty(RDF.type, Geosparql.Feature).toList(),
            containsInAnyOrder(resource("spatial/unit/id/A"), resource("spatial/unit/id/AA")));
        assertThat("Resource with id 'AA' must be a fs:BuildingUnit",
            infmodel.listSubjectsWithProperty(RDF.type, TuViennaSpatialOntology.BuildingUnit)
                .toList(), containsInAnyOrder(resource("spatial/unit/id/AA")));
    }

    @Test
    public void inference_model_inconsistency_knowledge_base() {
        Model baseModel = dataset.getNamedModel("inconsistency");
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(vocabularyManager.getCoreOntology());
        InfModel infmodel = ModelFactory.createInfModel(reasoner, baseModel);
        assertFalse("The basic model must not be valid.", infmodel.validate().isValid());
    }

    private static Resource resource(String local) {
        return ResourceFactory.createResource("http://finder.tuwien.ac.at/" + local);
    }

}
