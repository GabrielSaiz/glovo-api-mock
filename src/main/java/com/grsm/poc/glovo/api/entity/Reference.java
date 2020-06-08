package com.grsm.poc.glovo.api.entity;

/**
 * An object containing your own reference for the order
 */
public class Reference {

    /*
     * Your own reference string. Use this to associate any identifier you use for the items being shipped
     * (eg your order reference) with this Glovo shipment.
     */
    String id;

    public Reference() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
