package com.palsplate.system.lecibo.api;

import com.palsplate.system.akka.gpx.AkkaGpxTest;
import com.palsplate.system.akka.gpx.LatLon;
import com.palsplate.system.integrations.gtfs2neo4j.GTFS2Neo4jImporter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class Main {

	public static void main(String[] args) throws IOException, XMLStreamException {

        ArrayList<ArrayList<LatLon>> allStationsInRoute = new ArrayList<>();
        GTFS2Neo4jImporter importer = new GTFS2Neo4jImporter();

        //Upload GTFS file into neo4j-spatial layer
        File newFile = new File("./tmp/Upload__HVV_Rohdaten_GTFS_Fpl_20170810.zip");
        String regionId = "55";
        boolean dryrun = true;
        importer.execute(newFile, regionId, dryrun);

        //Extract all GPS coordinates from GPX file
        AkkaGpxTest jt = new AkkaGpxTest();
        ArrayList<LatLon> outputFromXML = jt.extractLatLonFromFile();

        //Find all stations 0.2km radius away from coordinates and put them together
        for(LatLon latlon: outputFromXML){
            allStationsInRoute.add(importer.findCloseTo(latlon, 0.2));
        }

        for(ArrayList<LatLon> l: allStationsInRoute){
            System.out.println(l.toString());
            System.out.println("========");
        }

		importer.shutDownDatabase();


	}

}
