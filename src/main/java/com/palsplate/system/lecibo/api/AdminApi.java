package com.palsplate.system.lecibo.api;

import com.palsplate.system.integrations.gtfs2neo4j.GTFS2Neo4jImporter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class AdminApi {

	public String uploadGTFSFRorRegion() {

		File newFile = new File("./tmp/Upload__HVV_Rohdaten_GTFS_Fpl_20170810.zip");
		String regionId = "55";
		boolean dryrun = true;

		File f;
		try {

			GTFS2Neo4jImporter importer = new GTFS2Neo4jImporter();
			importer.execute(newFile, regionId, dryrun);
		} catch (IOException e) {
			log.error("Error while processing GTFS file", e);
		}
		return "redirect:/uploadStatus";
	}

	public static void main(String[] args) {
		AdminApi aa = new AdminApi();
		aa.uploadGTFSFRorRegion();
	}
}
