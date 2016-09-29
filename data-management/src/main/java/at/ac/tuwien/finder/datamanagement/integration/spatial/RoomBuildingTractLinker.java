package at.ac.tuwien.finder.datamanagement.integration.spatial;

import at.ac.tuwien.finder.datamanagement.TripleStoreManager;
import at.ac.tuwien.finder.datamanagement.integration.DataLinker;
import at.ac.tuwien.finder.vocabulary.TUVS;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation of {@link DataLinker} that links rooms to their building tract by
 * using the room and building tract identifier.
 *
 * @author Kevin Haller
 */
public final class RoomBuildingTractLinker extends RoomBuildingUnitLinker {

    public RoomBuildingTractLinker() {
        super(TUVS.BuildingTract, TUVS.buildingTractCode);
    }

}
