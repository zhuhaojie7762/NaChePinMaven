package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

/**
 * 结算单
 */
public class SettlementOrder implements Serializable {
    private String code;
    private Date startTime;
    private Date endTime;
    private int supplierId;
    private String supplierName;
    private String supplierCompany;
    private long saleMoney;
    private long backMoney;
    private String alipayCode;
    private String alipayName;
    private String alipayNo;
    private String alipayOrderCode;
    @EnumVal(EnumValType.Manual)
    private SettlementOrderStatus status;
    @EnumVal(EnumValType.Name)
    private SettlementMethodType settlementMethod;
    private String remark;
    private Date created;
    private String totalCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public long getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(long saleMoney) {
        this.saleMoney = saleMoney;
    }

    public long getBackMoney() {
        return backMoney;
    }

    public void setBackMoney(long backMoney) {
        this.backMoney = backMoney;
    }

    public String getAlipayCode() {
        return alipayCode;
    }

    public void setAlipayCode(String alipayCode) {
        this.alipayCode = alipayCode;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getAlipayNo() {
        return alipayNo;
    }

    public void setAlipayNo(String alipayNo) {
        this.alipayNo = alipayNo;
    }

    public String getAlipayOrderCode() {
        return alipayOrderCode;
    }

    public void setAlipayOrderCode(String alipayOrderCode) {
        this.alipayOrderCode = alipayOrderCode;
    }

    public SettlementOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementOrderStatus status) {
        this.status = status;
    }

    public SettlementMethodType getSettlementMethod() {
        return settlementMethod;
    }

    public void setSettlementMethod(SettlementMethodType settlementMethod) {
        this.settlementMethod = settlementMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTotalCode() {
        return totalCode;
    }

    public void setTotalCode(String totalCode) {
        this.totalCode = totalCode;
    }

    /**
     * 非持久属性
     */
    @Transient
    private Date lastTime;

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
