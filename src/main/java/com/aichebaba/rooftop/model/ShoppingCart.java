package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class ShoppingCart implements Serializable {
    private int id;
    private int customerId;
    private String goodsCode;
    private int skuId;
    private String specPropValue;
    private String goodsName;
    private String goodsItemNo;
    private String color;
    private String size;
    private String fitCar;
    private int quantity;
    private Date created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    public String getSpecPropValue() {
        return specPropValue;
    }

    public void setSpecPropValue(String specPropValue) {
        this.specPropValue = specPropValue;
    }

    public String getGoodsItemNo() {
        return goodsItemNo;
    }

    public void setGoodsItemNo(String goodsItemNo) {
        this.goodsItemNo = goodsItemNo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFitCar() {
        return fitCar;
    }

    public void setFitCar(String fitCar) {
        this.fitCar = fitCar;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 非持久属性
     */
    private long price;
    private Boolean isSpecial;
    @EnumVal(EnumValType.Manual)
    private GoodsStatus goodsStatus;
    private Boolean followed;
    private String brand;
    private int sellerId;
    private String sellerName;
    private String headImgUrl;
    private String supplierCompany;

    @Transient
    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Transient
    public long getMoney() {
        return price * quantity;
    }

    @Transient
    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    @Transient
    public GoodsStatus getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(GoodsStatus goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    @Transient
    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    @Transient
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Transient
    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    @Transient
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    @Transient
    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    @Transient
    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }
}
