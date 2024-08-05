package com.aichebaba.rooftop.model;

import com.jfinal.plugin.activerecord.annotation.Sql;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
@Sql(insertSQL = "INSERT IGNORE INTO coupon(templateId, customerId, sendTime, used, usedTime, activityId) VALUES (?, ?, ?, ?, ?, ?);\n")
public class Coupon implements Serializable {
    private int id;

    private int templateId;

    private int customerId;

    private Date sendTime;

    private Date usedTime;

    private Boolean used;

    private int activityId;

    public Coupon() {
    }

    public Coupon(int templateId, int customerId, Date sendTime, int activityId) {
        this.templateId = templateId;
        this.customerId = customerId;
        this.sendTime = sendTime;
        this.activityId = activityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * 非持久属性
     */
    @Transient
    private CouponTemplate couponTemplate;

    public CouponTemplate getCouponTemplate() {
        return couponTemplate;
    }

    public void setCouponTemplate(CouponTemplate couponTemplate) {
        this.couponTemplate = couponTemplate;
    }
}
