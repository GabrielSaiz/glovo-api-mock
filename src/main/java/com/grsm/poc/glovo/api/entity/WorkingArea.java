package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The WorkingArea is the geographical space where Glovo can pick-up and deliver an order during certain hours of
 * the day. Cross-WorkingArea orders are not supported.
 */
public class WorkingArea {

    // Id of the delivery area (e.g. BCN, MAD, BUE).
    @JsonProperty("code")
    String code;
    // List of encoded polylines where a package can be picked-up or delivered.
    @JsonProperty("polygons")
    List<String> polygons;
    // WorkingTime during which this area is active.
    @JsonProperty("workingTimes")
    WorkingTime workingTimes;

    public WorkingArea() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<String> polygons) {
        this.polygons = polygons;
    }

    public WorkingTime getWorkingTimes() {
        return workingTimes;
    }

    public void setWorkingTimes(WorkingTime workingTimes) {
        this.workingTimes = workingTimes;
    }
}
