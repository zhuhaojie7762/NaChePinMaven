package com.aichebaba.rooftop.model;

import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Collection;

/**
 * 商品SKU属性类
 */
public class SkuProp implements Serializable {

    private int id;

    private String name;

    private String nameEn;

    private boolean hasImg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public boolean getHasImg() {
        return hasImg;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }

    @Transient
    private Collection<SkuPropValue> propValues;

    public Collection<SkuPropValue> getPropValues() {
        return propValues;
    }

    public void setPropValues(Collection<SkuPropValue> propValues) {
        this.propValues = propValues;
    }
}
