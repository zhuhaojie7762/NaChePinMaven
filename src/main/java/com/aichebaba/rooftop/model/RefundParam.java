package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

/**
 * he daoyuan 16.8.19
 * 退款查询参数
 * Created by Andy on 2016/8/19.
 */
public class RefundParam implements Serializable {
    private String nameParam;
    private String codeParam;
    private Date startTimeParam;
    private Date endTimeParam;
    @EnumVal(EnumValType.Name)
    private OrderStatus statusParam;
    private String paidAlpayCodeParam;
    private Integer expressIdParam;
    private String expressNameParam;
    private String expressCodeParam;

    public String getNameParam() {
        return nameParam;
    }

    public void setNameParam(String nameParam) {
        this.nameParam = nameParam;
    }

    public String getCodeParam() {
        return codeParam;
    }

    public void setCodeParam(String codeParam) {
        this.codeParam = codeParam;
    }

    public Date getStartTimeParam() {
        return startTimeParam;
    }

    public void setStartTimeParam(Date startTimeParam) {
        this.startTimeParam = startTimeParam;
    }

    public Date getEndTimeParam() {
        return endTimeParam;
    }

    public void setEndTimeParam(Date endTimeParam) {
        this.endTimeParam = endTimeParam;
    }

    public OrderStatus getStatusParam() {
        return statusParam;
    }

    public void setStatusParam(OrderStatus statusParam) {
        this.statusParam = statusParam;
    }

    public String getPaidAlpayCodeParam() {
        return paidAlpayCodeParam;
    }

    public void setPaidAlpayCodeParam(String paidAlpayCodeParam) {
        this.paidAlpayCodeParam = paidAlpayCodeParam;
    }

    public Integer getExpressIdParam() {
        return expressIdParam;
    }

    public void setExpressIdParam(Integer expressIdParam) {
        this.expressIdParam = expressIdParam;
    }

    public String getExpressNameParam() {
        return expressNameParam;
    }

    public void setExpressNameParam(String expressNameParam) {
        this.expressNameParam = expressNameParam;
    }

    public String getExpressCodeParam() {
        return expressCodeParam;
    }

    public void setExpressCodeParam(String expressCodeParam) {
        this.expressCodeParam = expressCodeParam;
    }
}
