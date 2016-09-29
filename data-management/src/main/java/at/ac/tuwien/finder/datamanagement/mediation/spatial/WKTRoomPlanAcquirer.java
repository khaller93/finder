package at.ac.tuwien.finder.datamanagement.mediation.spatial;

import at.ac.tuwien.finder.datamanagement.mediation.DataAcquirer;
import at.ac.tuwien.finder.datamanagement.mediation.DataTransformer;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataAcquireException;
import at.ac.tuwien.finder.datamanagement.mediation.exception.DataTransformationException;
import at.ac.tuwien.finder.datamanagement.mediation.transformer.WKTTransformer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This class is an implementation of {@link DataAcquirer} that acquires room plans given by CSV
 * files.
 *
 * @author Kevin Haller
 */
public class WKTRoomPlanAcquirer implements SpatialDataAcquirer<String> {

    @Override
    public DataTransformer<String> transformer() throws DataTransformationException {
        return new WKTTransformer();
    }

    @Override
    public String acquire() throws DataAcquireException {
        try (InputStream buildingHEGWktRoomPlanStream = WKTRoomPlanAcquirer.class.getClassLoader()
            .getResourceAsStream("spatial/roomPlans/buildingHEG_roomPlans.csv")) {
            return IOUtils.toString(buildingHEGWktRoomPlanStream, Charset.defaultCharset());
        } catch (IOException e) {
            throw new DataAcquireException(e);
        }
    }

    @Override
    public void close() throws Exception {

    }
}
