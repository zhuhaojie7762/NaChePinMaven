package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.Transient;

/**
 * SKU属性值
 */
public class SkuPropValue implements Serializable {

    public static final Function<SkuPropValue, Integer> propIdValue = new Function<SkuPropValue, Integer>() {
        @Override
        public Integer apply(SkuPropValue v) {
            return v.getSkuPropId();
        }
    };

    private int id;

    private String goodsCode;

    private int skuPropId;

    private String value;

    private String img;

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

    public int getSkuPropId() {
        return skuPropId;
    }

    public void setSkuPropId(int skuPropId) {
        this.skuPropId = skuPropId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Transient
    private boolean disable;

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }
}
