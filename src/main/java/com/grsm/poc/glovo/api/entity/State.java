package com.grsm.poc.glovo.api.entity;

/**
 * State of the order
 */
public enum State {

    // The order will be activated on scheduleTime.
    SCHEDULED,
    // The order is either being delivered or about to be.
    ACTIVE,
    // The delivery has finished succesfully.
    DELIVERED,
    // The order is canceled and it wont be delivered.
    CANCELED

}