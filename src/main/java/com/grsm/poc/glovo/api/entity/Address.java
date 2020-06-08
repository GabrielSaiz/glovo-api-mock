package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Each of the points that are part of the order. Right now you can only create two-point orders.
 */
public class Address {

    // Latitude of the address.
    @JsonProperty
    Double lat;

    // 	Longitude of the address.
    @JsonProperty
    Double lon;

    // PICKUP or DELIVERY depending on what the courier is expected to do at this address.
    @JsonProperty
    AddressType type;

    // Street and number (e.g. 21 Baker St).
    @JsonProperty
    String label;

    // Floor / apartment (e.g. 2nd Floor or blue button of the intercom). Maximum length is 255 characters. Optional.
    @JsonProperty
    String details;

    /*
     * Phone of the sender / recipient at that address. Must contain a country code (for example +34 for Spain).
     * Text messages will be sent to this number to update the recipient of the order’s status. Optional.
     */
    @JsonProperty
    String contactPhone;

    // Name of the sender / recipient at that address. Optional.
    @JsonProperty
    String contactPerson;

    // 	Specific instructions for this particular address. Maximum length is 255 characters. Optional.
    @JsonProperty
    String instructions;

    public Address() {
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

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
