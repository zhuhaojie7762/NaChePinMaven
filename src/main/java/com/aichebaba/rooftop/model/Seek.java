package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class Seek implements Serializable {
    private String code;
    private int customerId;
    private String contacts;
    private String phone;
    private String qq;
    private String wangwang;
    private String goodsName;
    private String quantity;
    private String material;
    private String price;
    private Date needTime;
    private String remark;
    private String imgUrl;
    @EnumVal(EnumValType.Manual)
    private SeekStatus status;
    private Date created;
    private Date closed;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWangwang() {
        return wangwang;
    }

    public void setWangwang(String wangwang) {
        this.wangwang = wangwang;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getNeedTime() {
        return needTime;
    }

    public void setNeedTime(Date needTime) {
        this.needTime = needTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public SeekStatus getStatus() {
        return status;
    }

    public void setStatus(SeekStatus status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    /**
     * 非持久属性
     */
    private String username;

    @Transient
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
