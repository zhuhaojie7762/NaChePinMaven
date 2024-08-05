package com.aichebaba.rooftop.model;

import java.io.Serializable;

public class PostFeeStrategy implements Serializable{

    private int id;

    /**
     * 省ID
     */
    private int areaId;

    /**
     * 城市ID
     */
    private int cityId;

    /**
     * 序列
     */
    private int sn;

    /**
     * 最小重量(克) 开
     */
    private int minWeight;

    /**
     * 最大重量(克) 闭
     */
    private int maxWeight;

    /**
     * 固定运费 (分)
     */
    private int unitPrice;

    /**
     * 是否固定计算
     */
    private boolean fixed;

    /**
     * 非固定时，运费计算公式用补充重量(克)
     */
    private int plusWeight;

    /**
     * 运费计算时最小单位重量(克)
     */
    private int unitWeight;

    /**
     * 运费不足该金额时，以该金额为运费
     */
    private int minMoney;

    /**
     * 非固定时，运费计算公式用补充金额(分)
     */
    private int plusMoney;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(int minWeight) {
        this.minWeight = minWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean getFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public int getPlusWeight() {
        return plusWeight;
    }

    public void setPlusWeight(int plusWeight) {
        this.plusWeight = plusWeight;
    }

    public int getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(int unitWeight) {
        this.unitWeight = unitWeight;
    }

    public int getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(int minMoney) {
        this.minMoney = minMoney;
    }

    public int getPlusMoney() {
        return plusMoney;
    }

    public void setPlusMoney(int plusMoney) {
        this.plusMoney = plusMoney;
    }
}
