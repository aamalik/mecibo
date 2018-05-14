package com.mecibo.system.graphhopper;

import com.graphhopper.directions.api.client.ApiException;
import com.graphhopper.directions.api.client.api.RoutingApi;
import com.graphhopper.directions.api.client.model.ResponseInstruction;
import com.graphhopper.directions.api.client.model.RouteResponse;
import com.graphhopper.directions.api.client.model.RouteResponsePath;

import java.util.Arrays;

/**
 * A simple example for querying the Routing API.
 */
public class GraphopperRoutingExample {

    private void start() {
        RoutingApi routing = new RoutingApi();
        String key = System.getProperty("graphhopper.key", System.getenv("GraphhopperKey"));

        try {
            RouteResponse rsp = routing.routeGet(
                    Arrays.asList("53.567739000000, 9.970779000000", "53.551256000000, 10.001725000000"),
                    false,
                    key,
                    "en",
                    true,
                    "car",
                    true,
                    true,
                    Arrays.<String>asList(),
                    false,
                    "fastest",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            RouteResponsePath path = rsp.getPaths().get(0);
            ResponseInstruction instr = path.getInstructions().get(0);
            System.out.println(instr.getText());

        } catch (ApiException ex) {
            System.out.println(ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        new GraphopperRoutingExample().start();
    }
}