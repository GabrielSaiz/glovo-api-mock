package com.grsm.poc.glovo.api.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grsm.poc.glovo.api.entity.WorkingAreas;

@RestController
@RequestMapping("/b2b/working-areas")
public class WorkingAreasController {

    /**
     * Get Working Areas
     * Returns the characteristics of our working areas.
     * Use this data to check for valid pickup and delivery locations and times on your side.
     * A way of doing this can be found in com.google.maps.android.PolyUtil::containsLocation
     * <p>
     * We recommend you to use aggressive caching for the results of this endpoint in order to avoid unnecessary
     * server-to-server traffic that could make you activate rate limiting.
     *
     * Request
     *
     * GET /b2b/working-areas
     * Response showing a single WorkingArea
     *
     * {
     *   "workingAreas": [
     *     {
     *       "code": "BCN",
     *       "polygons": ["<ENCODED POLYLINE>", "<ENCODED POLYLINE>"],
     *       "workingTime": { "from": "09:00", "duration": 120 }
     *     }
     *   ]
     * }
     *
     * @return
     */
    @GetMapping("")
    public WorkingAreas getWorkingAreas(){

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read json file and convert to Working Areas object
        WorkingAreas workingAreas = null;
        try {

            workingAreas = objectMapper.readValue(
                    new File(getClass().getClassLoader().getResource("static/working-areas-response.json").getFile()),
                    WorkingAreas.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workingAreas;
    }
}
