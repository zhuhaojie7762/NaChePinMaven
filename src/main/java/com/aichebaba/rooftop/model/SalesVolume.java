package com.aichebaba.rooftop.model;

import java.io.Serializable;

public class SalesVolume implements Serializable {
    private int id;
    private String goodsCode;
    private String goodsName;
    private int thisWeek;
    private int lastWeek;
    private int thisMonth;
    private int lastMonth;
    private long totalCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(int thisWeek) {
        this.thisWeek = thisWeek;
    }

    public int getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(int lastWeek) {
        this.lastWeek = lastWeek;
    }

    public int getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(int thisMonth) {
        this.thisMonth = thisMonth;
    }

    public int getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(int lastMonth) {
        this.lastMonth = lastMonth;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
