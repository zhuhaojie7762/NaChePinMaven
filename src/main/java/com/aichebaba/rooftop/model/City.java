package com.aichebaba.rooftop.model;

import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;

/**
 * @auther huwhy
 * @date 2016/5/5.
 */
public class City implements Serializable{
    private int id;
    private int provinceId;
    private String name;
    private boolean display;
    private String pinyin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    private String provinceName;

    @Transient
    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
