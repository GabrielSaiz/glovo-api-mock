package com.grsm.poc.glovo.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The total price
 */
public class Total {

    @JsonProperty("amount")
    private Integer amount;
    @JsonProperty("currency")
    private String currency;

    public Total() {
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
