package com.mecibo.system.graphhopper;

import com.mecibo.system.akka.gpx.LatLon;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GoogleDirectionsApi {


    public ArrayList<ArrayList<String>> getUniqueStationsInRadius(ArrayList<ArrayList<LatLon>> listOfAllStopsInAllRadius) {

        ArrayList<ArrayList<String>> uniqueListOfAllStopsInAllRadius = new ArrayList<>();

        for (ArrayList<LatLon> geoCoordinatesList : listOfAllStopsInAllRadius) {

            ArrayList<String> uniqueStationsInThisRadius = new ArrayList<>();

            for (LatLon ll : geoCoordinatesList) {
                if (!uniqueStationsInThisRadius.contains(ll.getName())) {
                    uniqueStationsInThisRadius.add(ll.getName());
                }
            }
            uniqueListOfAllStopsInAllRadius.add(uniqueStationsInThisRadius);
        }

        return uniqueListOfAllStopsInAllRadius;
    }

    public void calculateListOfAllPossibleRoutes(ArrayList<ArrayList<String>> allUniqueStations) {

        for(ArrayList<String> strArr: allUniqueStations){

        }
    }

    public static void findRouteByName(){

        String startStation = "U Schlump";
        String endStation = "Mönckebergstraße";

        String url = "https://maps.googleapis.com/maps/api/directions/json?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("origin", startStation));
        params.add(new BasicNameValuePair("destination", endStation));
        params.add(new BasicNameValuePair("sensor", "false"));
        params.add(new BasicNameValuePair("alternatives", "true"));
        params.add(new BasicNameValuePair("travel_mode", "transit"));
        params.add(new BasicNameValuePair("mode", "transit"));
        params.add(new BasicNameValuePair("key", System.getenv("GoogleTransitRoutingApi")));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;

        System.out.println("url: " + url);
        HttpGet get = new HttpGet(url);

    }

    public static JSONObject findRouteByGeoLocation() throws IOException, JSONException {

        double lat1 = 53.567739000000;
        double lon1 = 9.970779000000;
        double lat2 = 53.551256000000;
        double lon2 = 10.001725000000;

        LatLon startGC = new LatLon(lat1, lon1);
        LatLon endGC = new LatLon(lat2, lon2);

        String url = "https://maps.googleapis.com/maps/api/directions/json?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("origin", startGC.getLat() + "," + startGC.getLon()));
        params.add(new BasicNameValuePair("destination", endGC.getLat() + "," + endGC.getLon()));
        params.add(new BasicNameValuePair("sensor", "false"));
        params.add(new BasicNameValuePair("alternatives", "true"));
        params.add(new BasicNameValuePair("travel_mode", "transit"));
        params.add(new BasicNameValuePair("mode", "transit"));
        params.add(new BasicNameValuePair("key", System.getenv("GoogleTransitRoutingApi")));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        url += paramString;

        System.out.println("url: " + url);
        HttpGet get = new HttpGet(url);

        HttpClient client = new DefaultHttpClient();

        HttpResponse response = client.execute(get);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        JSONObject googleMapOutput = new JSONObject(result.toString());

        parseJson(googleMapOutput);

        return googleMapOutput;

    }

    public static void parseJson(JSONObject googleMapOutput) throws JSONException {

        JSONArray jArray = googleMapOutput.getJSONArray("routes");

        for(int i = 0 ; i < jArray.length() ; i++){

            JSONArray jarr1 = jArray.getJSONObject(i).getJSONArray("legs");
            for(int j = 0; j < jarr1.length(); j++){

                System.out.println("======================");
                JSONArray jarr2 = jarr1.getJSONObject(j).getJSONArray("steps");

                for(int k = 0; k < jarr2.length(); k++){

                    String travel_mode = jarr2.getJSONObject(k).getString("travel_mode");

                    if(travel_mode.equalsIgnoreCase("TRANSIT")){

                        System.out.println("travel_mode: " + travel_mode);
                        JSONObject transit_details = jarr2.getJSONObject(k).getJSONObject("transit_details");
                        String shortName = transit_details.getJSONObject("line").getString("short_name");
                        String arrivalStopName = transit_details.getJSONObject("arrival_stop").getString("name");
                        String departureStopName = transit_details.getJSONObject("departure_stop").getString("name");
                        String arrivalTime = transit_details.getJSONObject("arrival_time").getString("text");
                        String departureTime = transit_details.getJSONObject("departure_time").getString("text");

                        System.out.println("short_name: " + shortName);
                        System.out.println("departureStopName: " + departureStopName);
                        System.out.println("arrival_stop_name: " + arrivalStopName);
                        System.out.println("departureTime: " + departureTime);
                        System.out.println("arrivalTime: " + arrivalTime);

                    }

                    if(travel_mode.equalsIgnoreCase("WALKING")){
                        System.out.println("----");
                        System.out.println("travel_mode: " + travel_mode);
                        System.out.println("html_instructions: " + jarr2.getJSONObject(k).getString("html_instructions"));
                        System.out.println("Walking Steps Size: " + jarr2.getJSONObject(k).getJSONArray("steps").length());
                        System.out.println("distance: " + jarr2.getJSONObject(k).getJSONObject("distance").getString("text"));
                        System.out.println("duration: " + jarr2.getJSONObject(k).getJSONObject("duration").getString("text"));
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, JSONException {

        findRouteByGeoLocation();

    }
}