package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.TruncType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;

public class ExpressCompany implements Serializable {
    private int id;
    private String name;
    private String code;
    private String password;
    private String contacts;
    private String phone;
    private String comment;
    private String imgUrl;
    @EnumVal(EnumValType.Manual)
    private ExpressType type;
    private Boolean display;
    private Boolean beDefault;
    private int discount;
    private int cutMoney;
    private int plusMoney;
    private int multiply;
    @EnumVal(EnumValType.Name)
    private TruncType trunc;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ExpressType getType() {
        return type;
    }

    public void setType(ExpressType type) {
        this.type = type;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Boolean getBeDefault() {
        return beDefault;
    }

    public void setBeDefault(Boolean beDefault) {
        this.beDefault = beDefault;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getCutMoney() {
        return cutMoney;
    }

    public void setCutMoney(int cutMoney) {
        this.cutMoney = cutMoney;
    }

    public int getPlusMoney() {
        return plusMoney;
    }

    public void setPlusMoney(int plusMoney) {
        this.plusMoney = plusMoney;
    }

    public int getMultiply() {
        return multiply;
    }

    public void setMultiply(int multiply) {
        this.multiply = multiply;
    }

    public TruncType getTrunc() {
        return trunc;
    }

    public void setTrunc(TruncType trunc) {
        this.trunc = trunc;
    }

    /**
     * 非持久属性
     */
    @Transient
    private Double postFee;

    public Double getPostFee() {
        return postFee;
    }

    public void setPostFee(Double postFee) {
        this.postFee = postFee;
    }
}
