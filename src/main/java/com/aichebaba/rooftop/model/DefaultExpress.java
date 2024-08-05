package com.aichebaba.rooftop.model;

import java.io.Serializable;

public class DefaultExpress implements Serializable {
    private int customerId;
    private int expressId;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getExpressId() {
        return expressId;
    }

    public void setExpressId(int expressId) {
        this.expressId = expressId;
    }
}
