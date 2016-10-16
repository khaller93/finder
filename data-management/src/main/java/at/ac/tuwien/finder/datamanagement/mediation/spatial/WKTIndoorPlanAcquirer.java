package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.mediation.DataAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.WKTTransformer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an implementation of {@link DataAcquirer} that acquires room plans given by CSV
 * files.
 *
 * @author Kevin Haller
 */
public class WKTIndoorPlanAcquirer implements SpatialDataAcquirer<Collection<CSVRecord>> {


    private static final String ROOM_PLANS_CSV_PATH = "spatial/roomPlans/buildingHEG_roomPlans.csv";
    private static final String PASSAGEWAY_PLANS_CSV_PATH =
        "spatial/roomPlans/buildingHEG_passagewayPlans.csv";
    private static final String FLOOR_PLANS_CSV_PATH =
        "spatial/floorPlans/buildingH_floorPlans.csv";
    private static final String ACCESS_UNITS_PLANS_CSV_PATH =
        "spatial/roomPlans/buildingHEG_accessUnits.csv";
    private static final String VERTICAL_PASSAGEWAY_PLANS_CSV_PATH =
        "spatial/roomPlans/buildingHEG_verticalPassagewaysPlans.csv";

    private static final String POINTS_OF_ROUTES_CSV_PATH =
        "spatial/roomPlans/buildingHEG_poRoutes.csv";

    @Override
    public DataTransformer<Collection<CSVRecord>> transformer() throws DataTransformationException {
        return new WKTTransformer();
    }

    @Override
    public Collection<CSVRecord> acquire() throws DataAcquireException {
        try (InputStream roomPlansCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
            .getResourceAsStream(ROOM_PLANS_CSV_PATH);
            InputStream passagewayPlansCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
                .getResourceAsStream(PASSAGEWAY_PLANS_CSV_PATH);
            InputStream floorPlansCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
                .getResourceAsStream(FLOOR_PLANS_CSV_PATH);
            InputStream accessUnitCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
                .getResourceAsStream(ACCESS_UNITS_PLANS_CSV_PATH);
            InputStream verticalPassagewayCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
                .getResourceAsStream(VERTICAL_PASSAGEWAY_PLANS_CSV_PATH);
            InputStream pointOfRoutesCSVStream = WKTIndoorPlanAcquirer.class.getClassLoader()
                .getResourceAsStream(POINTS_OF_ROUTES_CSV_PATH);) {
            List<CSVRecord> records = new LinkedList<>();
            records.addAll(
                CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(roomPlansCSVStream))
                    .getRecords());
            records.addAll(CSVFormat.DEFAULT.withHeader()
                .parse(new InputStreamReader(passagewayPlansCSVStream)).getRecords());
            records.addAll(
                CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(floorPlansCSVStream))
                    .getRecords());
            records.addAll(
                CSVFormat.DEFAULT.withHeader().parse(new InputStreamReader(accessUnitCSVStream))
                    .getRecords());
            records.addAll(CSVFormat.DEFAULT.withHeader()
                .parse(new InputStreamReader(verticalPassagewayCSVStream)).getRecords());
            records.addAll(CSVFormat.DEFAULT.withHeader()
                .parse(new InputStreamReader(pointOfRoutesCSVStream)).getRecords());
            return records;
        } catch (IOException e) {
            throw new DataAcquireException(e);
        }
    }

    @Override
    public void close() throws Exception {

    }
}
