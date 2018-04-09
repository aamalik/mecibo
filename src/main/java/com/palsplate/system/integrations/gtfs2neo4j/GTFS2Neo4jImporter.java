package com.palsplate.system.integrations.gtfs2neo4j;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.palsplate.system.akka.gpx.LatLon;
import io.micrometer.core.instrument.search.Search;
import org.neo4j.gis.spatial.*;
import org.neo4j.gis.spatial.filter.SearchIntersectWindow;
import org.neo4j.gis.spatial.index.SpatialIndexReader;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
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

	final static String LAYER_NAME = "finalHamburgLayer";

    GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File("./tmp/neotest")).newGraphDatabase();
    SpatialDatabaseService spatialService = new SpatialDatabaseService(db);

    Layer layer = spatialService.getLayer(LAYER_NAME);

    public void execute(File gtfsFile, String regionId, boolean dryrun) throws IOException {

		GtfsReader reader = new GtfsReader();
		reader.setInputLocation(gtfsFile);

		GtfsRelationalDaoImpl store = new GtfsRelationalDaoImpl();
		reader.setEntityStore(store);
		reader.run();

        log.info("Agencies: " + store.getAllAgencies().toString());
        log.info("Stop  Sizes: " +  store.getAllStops().size());

		try{
            // get or create a layer for station
            final EditableLayer layer = (spatialService.containsLayer(LAYER_NAME)
                    ? (EditableLayer) spatialService.getLayer(LAYER_NAME)
                    : (EditableLayer) spatialService.createSimplePointLayer(LAYER_NAME, "Longitude", "Latitude"));

            try (Transaction tx = spatialService.getDatabase().beginTx()) {
                log.info("All stops" + store.getAllStops());

                store.getAllStops().forEach((s) -> {

				    if (!dryrun) {
						SpatialRecord record = layer.add(layer.getGeometryFactory().createPoint(new Coordinate(s.getLat(), s.getLon())));

						// now we add some further data to the spatial point
						Node n = record.getGeomNode();
						n.setProperty("id", s.getId().toString());
						n.setProperty("name", s.getName());
						n.setProperty("regionId", regionId);
                        n.setProperty("locationTpe", s.getLocationType());

					}
					log.info("Imported {}", s.getName());
					store.getStopTimesForStop(s).forEach((st) -> {
//						log.info("Found for this {}", st.getRouteShortName());
					});

				});

				tx.success();
                log.info("Stored {} datasets to layer ", store.getAllStops().size(), LAYER_NAME);
			}
//            findCloseToStation();

		} catch (Exception e){
		    log.error(e.getMessage());
		}
	}

//	public void findCloseToStation(){
//
//        LatLon latlon = new LatLon(54.310567000000, 10.098604000000);
//
//        log.info("Closest Stations Sizes: " + findCloseTo(latlon,1));
//
//    }


    public Set<LatLon> findCloseTo(LatLon latlon, float distanceInKm) {

        List<SpatialDatabaseRecord> results = null;
        Set<LatLon> resultSet = null;

        try (Transaction tx = db.beginTx()) {

            results = GeoPipeline.startNearestNeighborLatLonSearch(layer, latLon2Coordinate(latlon), distanceInKm)
                    .toSpatialDatabaseRecordList();

            log.debug("results: " + results);
            log.info("List Size: " + results.size());

            resultSet = new HashSet<LatLon>(toLatLonList(results));
            log.info("Set Size: " + resultSet.size());

            return resultSet;
        }
    }

    private List<LatLon> toLatLonList(List<SpatialDatabaseRecord> results) {
        return results.stream().map(
                r -> Coordinate2LatLon(r.getGeometry().getCoordinate(), (String) r.getGeomNode().getProperty("name")))
                .collect(java.util.stream.Collectors.toList());
    }

    private LatLon Coordinate2LatLon(Coordinate c, String name) {
        return new LatLon(c.x, c.y, name);
    }

    private Coordinate latLon2Coordinate(LatLon latlon) {
        return new Coordinate(latlon.getLat(), latlon.getLon());
    }

    public void shutDownDatabase(){
        db.shutdown();
    }

}
