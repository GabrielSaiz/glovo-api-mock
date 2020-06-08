package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Order {

    // Unique Id of the order
    @JsonProperty
    String id;
    // A human readable identifier of the order
    @JsonProperty
    String code;
    // 	Description detailing the package to be delivered
    @JsonProperty
    String description;
    // Unix time converted to milliseconds of the scheduled activation time of the order. Optional.
    @JsonProperty
    Long scheduleTime;
    /*
     * Ordered list of addresses (pickups and deliveries) of the order.
     * For One Way Order you will specify one PICKUP address and one DELIVERY address.
     * Other delivery types Return Order, Multiple Pick-Up Order or Multiple Drop-Off Order
     * have different requirements.
     */
    @JsonProperty
    List<Address> addresses;
    // Current state of the order (one of SCHEDULED, ACTIVE, DELIVERED, CANCELED).
    @JsonProperty
    State state;
    // 	An object containing your own reference for the order. Optional.
    @JsonProperty
    Reference reference;

    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }
}
