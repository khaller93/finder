package at.ac.tuwien.finder.dto;

import at.ac.tuwien.finder.dto.rdf.IResourceIdentifier;
import org.eclipse.rdf4j.model.Model;
import org.outofbits.opinto.annotations.RdfsClass;

import java.util.Collection;

/**
 * This class is an implementation of {@link FeatureDto} that represents a floor.
 *
 * @author Kevin Haller
 */
@RdfsClass("http://finder.tuwien.ac.at/vocab/spatial#Floor")
public class FloorDto extends AbstractFeatureDto implements FeatureDto {

}
