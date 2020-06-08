package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WorkingTimes specify the activity hours of a WorkingArea,
 */
public class WorkingTime {

    // Starting time of the active time range.
    @JsonProperty("from")
    String from;
    // Duration in minutes of the time range.
    @JsonProperty("duration")
    Integer duration;

    public WorkingTime() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
