package com.grsm.poc.glovo.api.entity;

import java.util.List;

/**
 * List of Orders
 */
public class Orders {

    List<Order> orders;

    public Orders() {
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
