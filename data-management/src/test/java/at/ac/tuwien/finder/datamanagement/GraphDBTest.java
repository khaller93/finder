package at.ac.tuwien.finder.datamanagement;

import at.ac.tuwien.finder.vocabulary.TUVS;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.StatementCollector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

/**
 * This class tests the GraphDB.
 *
 * @author Kevin Haller
 */
public class GraphDBTest extends TripleStoreTest {

    private static final ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    private static final URI SPATIAL_UNIT_BASE =
        valueFactory.createURI("http://finder.tuwien.ac.at/spatial/unit/id/");
    private static final URI SPATIAL_CAMPUS_BASE =
        valueFactory.createURI("http://finder.tuwien.ac.at/spatial/campus/id/");

    @Test
    public void get_all_building_units_ok()
        throws RepositoryException, MalformedQueryException, QueryEvaluationException,
        RDFHandlerException {
        RepositoryConnection connection = getConnection();
        Model resultModel = new LinkedHashModel();
        connection.prepareGraphQuery(QueryLanguage.SPARQL, String
            .format("DESCRIBE ?buildingUnit WHERE { ?buildingUnit a <%s>}",
                TUVS.BuildingUnit.toString())).evaluate(new StatementCollector(resultModel));
        assertThat(
            "The result model must contain the building units with the id AA, AB, AC, AA01 and AC01.",
            resultModel.subjects(),
            hasItems(valueFactory.createURI(SPATIAL_UNIT_BASE.toString(), "AA"),
                valueFactory.createURI(SPATIAL_UNIT_BASE.toString(), "AA01"),
                valueFactory.createURI(SPATIAL_UNIT_BASE.toString(), "AB"),
                valueFactory.createURI(SPATIAL_UNIT_BASE.toString(), "AC"),
                valueFactory.createURI(SPATIAL_UNIT_BASE.toString(), "AC01")));
    }

    @Test
    public void get_campus_if_inference_works()
        throws RepositoryException, MalformedQueryException, QueryEvaluationException,
        RDFHandlerException {
        RepositoryConnection connection = getConnection();
        Model resultModel = new LinkedHashModel();
        connection.prepareGraphQuery(QueryLanguage.SPARQL,
            String.format("DESCRIBE ?campus WHERE { ?campus a <%s> . }", TUVS.Campus.toString()))
            .evaluate(new StatementCollector(resultModel));
        assertThat("The campus with id '1' must be part in the result model.",
            resultModel.subjects(),
            hasItem(valueFactory.createURI(SPATIAL_CAMPUS_BASE.toString(), "1")));
    }

}
