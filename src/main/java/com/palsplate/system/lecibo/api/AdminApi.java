package com.palsplate.system.lecibo.api;

import com.palsplate.system.akka.gpx.LatLon;
import com.palsplate.system.integrations.gtfs2neo4j.GTFS2Neo4jImporter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class AdminApi {

	public void uploadGTFSFRorRegion() throws IOException {

		File newFile = new File("./tmp/Upload__HVV_Rohdaten_GTFS_Fpl_20170810.zip");
        LatLon latlon = new LatLon(54.310567000000, 10.098604000000);
        String regionId = "55";
		boolean dryrun = true;

		GTFS2Neo4jImporter importer = new GTFS2Neo4jImporter();

        importer.execute(newFile, regionId, dryrun);

        log.info("Closest Stations Sizes: " + importer.findCloseTo(latlon,1));

        importer.shutDownDatabase();

	}

	public static void main(String[] args) throws IOException {

		AdminApi aa = new AdminApi();
		aa.uploadGTFSFRorRegion();

	}

}
