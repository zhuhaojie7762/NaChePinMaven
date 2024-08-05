package com.aichebaba.rooftop.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class SettlementExpress implements Serializable {
    private int expressId;
    private String expressName;
    private long quantity;
    private BigDecimal weight;
    private BigDecimal realityWeight;
    private BigDecimal freight;

    public int getExpressId() {
        return expressId;
    }

    public void setExpressId(int expressId) {
        this.expressId = expressId;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getRealityWeight() {
        return realityWeight;
    }

    public void setRealityWeight(BigDecimal realityWeight) {
        this.realityWeight = realityWeight;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }
}
