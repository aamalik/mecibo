package com.mecibo.system.graphhopper;

import netscape.javascript.JSObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONArray;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class HereApi {

    public static JSONObject routingAPI() throws IOException, JSONException {

        String url = "https://route.cit.api.here.com/routing/7.2/calculateroute.json?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("app_id", "yRLlpk97BXj4EnCdrXrt"));
        params.add(new BasicNameValuePair("app_code", "8N7Oef179q-W8QLRNVQVTQ"));
        params.add(new BasicNameValuePair("waypoint0", "geo!51.517251,7.459178"));
        params.add(new BasicNameValuePair("waypoint1", "geo!51.478919,7.222661"));
        params.add(new BasicNameValuePair("alternatives", "4"));
//        params.add(new BasicNameValuePair("departure", "now"));
        params.add(new BasicNameValuePair("mode", "fastest;publicTransport"));
        params.add(new BasicNameValuePair("combineChange", "true"));

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

        JSONObject output = parseJson(googleMapOutput);

        return output;

    }

    public static JSONObject parseJson(JSONObject googleMapOutput) throws JSONException {

        JSONObject jsonObject = googleMapOutput.getJSONObject("response");
        System.out.println("jsonObject: " + jsonObject);

        JSONArray jsonObjectTwo = jsonObject.getJSONArray("route");
        System.out.println("jsonObjectTwo: " + jsonObjectTwo);

        for(int i = 0 ; i < jsonObjectTwo.length() ; i++) {
//            jsonObjectTwo.getJSONObject(i).getJSONArray("waypoint");
//            jsonObjectTwo.getJSONObject(i).getJSONObject("mode");
//            jsonObjectTwo.getJSONObject(i).getJSONObject("summary");
            JSONArray leg = jsonObjectTwo.getJSONObject(i).getJSONArray("leg");
            JSONArray publicTransportLine = jsonObjectTwo.getJSONObject(i).getJSONArray("publicTransportLine");
            System.out.println("=================");
            for(int j =0; j < publicTransportLine.length(); j++){
                System.out.println("lineName: " + publicTransportLine.getJSONObject(j).get("lineName"));
            }

        }

        return jsonObject;
    }

    public static JSONObject transitAPI() throws IOException, JSONException {

        String url = "https://transit.cit.api.here.com/v3/route.json?";
        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("app_id", "yRLlpk97BXj4EnCdrXrt"));
        params.add(new BasicNameValuePair("app_code", "8N7Oef179q-W8QLRNVQVTQ"));
        params.add(new BasicNameValuePair("routing", "all"));
        params.add(new BasicNameValuePair("dep", "Bleichstraße, Dortmund"));
        params.add(new BasicNameValuePair("arr", "Holtestraße, Dortmund"));
        params.add(new BasicNameValuePair("time", "2018-05-06T07%3A30%3A00"));

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

        return googleMapOutput;

    }

    public static void main(String[] args) throws IOException, JSONException {
//        routingAPI();
        transitAPI();
    }
}