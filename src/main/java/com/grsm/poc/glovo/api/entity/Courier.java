package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Name and contact phone of the courier
 */
public class Courier {

    @JsonProperty("courier")
    private String courier;
    @JsonProperty("phone")
    private String phone;

    public Courier() {
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
