package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The position (latitude, longitude)
 */
public class Position {

    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lon")
    private Double lon;

    public Position() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
