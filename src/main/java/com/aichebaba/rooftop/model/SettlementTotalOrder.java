package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

public class SettlementTotalOrder implements Serializable {
    private String code;
    private Date startTime;
    private Date endTime;
    @EnumVal(EnumValType.Manual)
    private SettlementOrderStatus status;
    private String remark;
    private Date created;
    @EnumVal(EnumValType.Name)
    private SettlementMethodType settlementMethod;

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

    public SettlementOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SettlementOrderStatus status) {
        this.status = status;
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

    public SettlementMethodType getSettlementMethod() {
        return settlementMethod;
    }

    public void setSettlementMethod(SettlementMethodType settlementMethod) {
        this.settlementMethod = settlementMethod;
    }

    /**
     * 非持久属性
     */
    @Transient
    private Date checkFinishTime;
    @Transient
    private Date backTime;

    public Date getCheckFinishTime() {
        return checkFinishTime;
    }

    public void setCheckFinishTime(Date checkFinishTime) {
        this.checkFinishTime = checkFinishTime;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }
}
