package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.SettlementState;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

public class MonthlySettlement implements Serializable {
    private String code;
    private Date month;
    private int supplierId;
    private String supplierCode;
    private String supplierCompany;
    private String alipayCode;
    private String tenpayCode;
    private Date startTime;
    private Date endTime;
    private int orderCnt;
    private long orderMoney;
    private int backCnt;
    private long backMoney;
    private int negotiationBackCnt;
    private long negotiationBackMoney;
    private long payMoney;
    @EnumVal(EnumValType.Manual)
    private SettlementState settlementState;
    private String remark;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public String getAlipayCode() {
        return alipayCode;
    }

    public void setAlipayCode(String alipayCode) {
        this.alipayCode = alipayCode;
    }

    public String getTenpayCode() {
        return tenpayCode;
    }

    public void setTenpayCode(String tenpayCode) {
        this.tenpayCode = tenpayCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(int orderCnt) {
        this.orderCnt = orderCnt;
    }

    public long getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(long orderMoney) {
        this.orderMoney = orderMoney;
    }

    public int getBackCnt() {
        return backCnt;
    }

    public void setBackCnt(int backCnt) {
        this.backCnt = backCnt;
    }

    public long getBackMoney() {
        return backMoney;
    }

    public void setBackMoney(long backMoney) {
        this.backMoney = backMoney;
    }

    public int getNegotiationBackCnt() {
        return negotiationBackCnt;
    }

    public void setNegotiationBackCnt(int negotiationBackCnt) {
        this.negotiationBackCnt = negotiationBackCnt;
    }

    public long getNegotiationBackMoney() {
        return negotiationBackMoney;
    }

    public void setNegotiationBackMoney(long negotiationBackMoney) {
        this.negotiationBackMoney = negotiationBackMoney;
    }

    public long getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(long payMoney) {
        this.payMoney = payMoney;
    }

    public SettlementState getSettlementState() {
        return settlementState;
    }

    public void setSettlementState(SettlementState settlementState) {
        this.settlementState = settlementState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
