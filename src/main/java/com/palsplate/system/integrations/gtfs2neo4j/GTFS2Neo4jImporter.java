package com.palsplate.system.integrations.gtfs2neo4j;

import java.io.File;
import java.io.IOException;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.vividsolutions.jts.geom.Coordinate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GTFS2Neo4jImporter {

	final static String LAYER_NAME = "stationsHamburg";

	public void execute(File gtfsFile, String regionId, boolean dryrun)
			throws IOException {

		GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("./tmp/neotest")).newGraphDatabase();

        SpatialDatabaseService spatialService = new SpatialDatabaseService(db);

		GtfsReader reader = new GtfsReader();
		reader.setInputLocation(gtfsFile);

		GtfsRelationalDaoImpl store = new GtfsRelationalDaoImpl();
		reader.setEntityStore(store);

		reader.run();

		try{
            // get or create a layer for station
            final EditableLayer layer = (spatialService.containsLayer(LAYER_NAME)
                    ? (EditableLayer) spatialService.getLayer(LAYER_NAME)
                    : (EditableLayer) spatialService.createSimplePointLayer(LAYER_NAME, "Longitude", "Latitude"));

            try (Transaction tx = spatialService.getDatabase().beginTx()) {
				store.getAllStops().forEach((s) -> {
					if (!dryrun) {
						SpatialRecord record = layer
								.add(layer.getGeometryFactory().createPoint(new Coordinate(s.getLat(), s.getLon())));
						// now we add some further data to the spatial point
						Node n = record.getGeomNode();
						n.setProperty("id", s.getId().toString());
						n.setProperty("name", s.getName());
						n.setProperty("regionId", regionId);
					}
					log.info("Imported {}", s.getName());
					store.getStopTimesForStop(s).forEach((st) -> {
						log.info("Found for this {}", st.getRouteShortName());
					});

				});
				tx.success();

				log.info("Stored {} datasets to layer ", store.getAllStops().size(), LAYER_NAME);
			}
		} finally{
			db.shutdown();
		}
	}
}
