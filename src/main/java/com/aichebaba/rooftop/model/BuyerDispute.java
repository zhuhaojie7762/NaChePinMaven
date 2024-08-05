package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andy on 2016/12/7.
 */
public class BuyerDispute implements Serializable {
    private Long sid;
    private String orderId;
    private Integer clientId;
    private String clientName;
    private Integer adminId;
    private String adminName;
    @EnumVal(EnumValType.Manual)
    private OrderStatus refundStatusCode;
    private Integer goodsStatus;
    private String reason;
    private Long money;
    private String remark;
    private String trackingName;
    private String trackingNumber;
    private Date createTime;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public OrderStatus getRefundStatusCode() {
        return refundStatusCode;
    }

    public void setRefundStatusCode(OrderStatus refundStatusCode) {
        this.refundStatusCode = refundStatusCode;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getTrackingName() {
        return trackingName;
    }

    public void setTrackingName(String trackingName) {
        this.trackingName = trackingName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
