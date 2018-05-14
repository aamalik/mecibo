package com.mecibo.system;

import com.mecibo.system.akka.gpx.AkkaGpxTest;
import com.mecibo.system.akka.gpx.LatLon;
import com.mecibo.system.graphhopper.GoogleDirectionsApi;
import com.mecibo.system.integrations.gtfs2neo4j.GTFS2Neo4jImporter;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class    Main {

    public static void main(String[] args) throws IOException, XMLStreamException {

        ArrayList<ArrayList<LatLon>> listOfAllStopsInAllRadius = new ArrayList<>();

        GTFS2Neo4jImporter importer = new GTFS2Neo4jImporter();

        //Upload GTFS file into neo4j-spatial layer
        File newFile = new File("./tmp/Upload__HVV_Rohdaten_GTFS_Fpl_20170810.zip");
        String regionId = "55";
        boolean dryrun = true;
        importer.execute(newFile, regionId, dryrun);

        //Extract all GPS coordinates from GPX file
        AkkaGpxTest jt = new AkkaGpxTest();
        ArrayList<LatLon> outputFromXML = jt.extractLatLonFromFile();

        //Find all stations 0.2km radius away from every coordinates and put them together as a list
        for(LatLon latlon: outputFromXML){
            listOfAllStopsInAllRadius.add(importer.findCloseTo(latlon, 0.2));
        }

        //Print all stations in all radius
        Integer i = 0;
        for(ArrayList<LatLon> l: listOfAllStopsInAllRadius){
            System.out.println("========");
            System.out.println(i + ": " + l.toString());
            i++;
        }

        GoogleDirectionsApi gDA = new GoogleDirectionsApi();

        //Print unique stations in radius
        Integer j = 0;
        for(ArrayList<String> l: gDA.getUniqueStationsInRadius(listOfAllStopsInAllRadius)){
            System.out.println("========");
            System.out.println(j + ": " + l.toString());
            j++;
        }

        importer.shutDownDatabase();

	}
}
