package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to a /b2b/orders/estimate Request.
 */
public class EstimatePrice {

    @JsonProperty("total")
    private Total total;

    public EstimatePrice() {
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }
}
