package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * List of WorkingArea
 */

public class WorkingAreas {

    @JsonProperty("workingAreas")
    List<WorkingArea> workingAreas;

    public WorkingAreas() {
    }

    public List<WorkingArea> getWorkingAreas() {
        return workingAreas;
    }

    public void setWorkingAreas(List<WorkingArea> workingAreas) {
        this.workingAreas = workingAreas;
    }
}
