package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.mediation.DataAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.NOPTransformer;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is an implementation of {@link DataAcquirer} that acquire information about the
 * buildings and building tracts, which has been exposed on the page of the GUT.
 * <p>
 * The information has been gathered by scrapping and transforming the scrapped messy data into
 * linked data with (Google) OpenRefine.
 *
 * @author Kevin Haller
 * @see <a href="http://www.gut.tuwien.ac.at/wir_fuer_sie/immobilienmanagement/grundrisse_objekte/">GUT building overview</a>
 */
public class GUTBuildingInformationAcquirer implements SpatialDataAcquirer<Model> {

    private static final Logger logger =
        LoggerFactory.getLogger(GUTBuildingInformationAcquirer.class);

    private static final String GUT_BUILDING_OVERVIEW = "spatial/GUTBuilding-Overview.ttl";

    private static final String GUT_BUILDING_TRACT_OVERVIEW =
        "spatial/GUTBuilding-Tract-Overview.ttl";
    private static final String GUT_BUILDING_TRACT_FLOORS = "spatial/GUTBuilding-Tract-Floors.ttl";

    @Override
    public DataTransformer<Model> transformer() throws DataTransformationException {
        return new NOPTransformer();
    }

    @Override
    public Model acquire() throws DataAcquireException {
        try (InputStream buildingOverviewStream = GUTBuildingInformationAcquirer.class
            .getClassLoader().getResourceAsStream(GUT_BUILDING_OVERVIEW);
            InputStream buildingTractOverviewStream = GUTBuildingInformationAcquirer.class
                .getClassLoader().getResourceAsStream(GUT_BUILDING_TRACT_OVERVIEW);
            InputStream buildingTractFloorsStream = GUTBuildingInformationAcquirer.class
                .getClassLoader().getResourceAsStream(GUT_BUILDING_TRACT_FLOORS)) {
            Model buildingInfoModel = new LinkedHashModel();
            if (buildingOverviewStream == null) {
                logger.error("The rdf file of the building overview ({}) cannot be found.",
                    GUT_BUILDING_OVERVIEW);
                throw new DataAcquireException(
                    "The rdf file of the building overview (" + GUT_BUILDING_TRACT_FLOORS
                        + ") cannot be found.");
            } else if (buildingTractOverviewStream == null) {
                logger.error("The rdf file of the building tract overview ({}) cannot be found.",
                    GUT_BUILDING_TRACT_OVERVIEW);
                throw new DataAcquireException(
                    "The rdf file of the building tract overview (" + GUT_BUILDING_TRACT_OVERVIEW
                        + ") cannot be found.");
            } else if (buildingTractFloorsStream == null) {
                logger.error("The rdf file of the building tract overview ({}) cannot be found.",
                    GUT_BUILDING_TRACT_FLOORS);
                throw new DataAcquireException(
                    "The rdf file of the building tract overview (" + GUT_BUILDING_TRACT_FLOORS
                        + ") cannot be found.");
            }
            RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
            parser.setRDFHandler(new StatementCollector(buildingInfoModel));
            try {
                parser.parse(buildingOverviewStream, TripleStoreManager.BASE.stringValue());
                parser.parse(buildingTractOverviewStream, TripleStoreManager.BASE.stringValue());
                parser.parse(buildingTractFloorsStream, TripleStoreManager.BASE.stringValue());
            } catch (RDFParseException | RDFHandlerException e) {
                logger
                    .error("The linked data about university facilities can not be read in. {}", e);
                throw new DataAcquireException(e);
            }
            return buildingInfoModel;
        } catch (IOException e) {
            logger.error("The linked data about university facilities can not be read in. {}", e);
            throw new DataAcquireException(e);
        }
    }

    @Override
    public void close() throws Exception {
        /* Nothing to do */
    }
}
