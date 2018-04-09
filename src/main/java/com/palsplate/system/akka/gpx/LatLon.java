package com.palsplate.system.akka.gpx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LatLon {

    public LatLon(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Getter
    @Setter
    private Double lat, lon;

    @Getter
    String name;

    @Override
    public String toString() {
        return "LatLon{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", name='" + name + '\'' +
                '}';
    }
}
