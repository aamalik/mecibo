package com.palsplate.system.graphhopper;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

public class GoogleDirectionsApi {

    public static void main(String[] args){

        double lat1 = 53.567739000000;
        double lon1 = 9.970779000000;
        double lat2 = 53.551256000000;
        double lon2 = 10.001725000000;

        String url = "https://maps.googleapis.com/maps/api/directions/json?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("origin", lat1 + "," + lon1));
        params.add(new BasicNameValuePair("destination", lat2 + "," + lon2));
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
}


