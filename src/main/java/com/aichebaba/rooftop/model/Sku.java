package com.aichebaba.rooftop.model;

import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class Sku implements Serializable {

    public static final Function<Sku, String> PROPS_VALUE = new Function<Sku, String>() {
        @Override
        public String apply(Sku sku) {
            return sku.getProperties();
        }
    };

    private int id;

    /**
     * 商品Code
     */
    private String goodsCode;

    /**
     * SKU属性： 如：p:v;p:v;
     */
    private String properties;

    /**
     * 重量
     */
    private int weight;

    /**
     * 价格
     */
    private long price;

    /**
     * 建议零售价格
     */
    private long retailPrice;

    /**
     * 库存
     */
    private int stock;

    /**
     * 下单锁库存的数量
     */
    private int lockStock;

    /**
     * 商品图片
     */
    private String imgUrl;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 创建时间
     */
    private Date created;

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

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(long retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getLockStock() {
        return lockStock;
    }

    public void setLockStock(int lockStock) {
        this.lockStock = lockStock;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Transient
    public int getAvailableStock() {
        return stock - lockStock;
    }

    @Transient
    private String skuSpecValue;

    @Transient
    private String skuColorValue;

    @Transient
    private String skuSizeValue;

    public String getSkuSpecValue() {
        return skuSpecValue;
    }

    public void setSkuSpecValue(String skuSpecValue) {
        this.skuSpecValue = skuSpecValue;
    }

    public String getSkuColorValue() {
        return skuColorValue;
    }

    public void setSkuColorValue(String skuColorValue) {
        this.skuColorValue = skuColorValue;
    }

    public String getSkuSizeValue() {
        return skuSizeValue;
    }

    public void setSkuSizeValue(String skuSizeValue) {
        this.skuSizeValue = skuSizeValue;
    }
}
