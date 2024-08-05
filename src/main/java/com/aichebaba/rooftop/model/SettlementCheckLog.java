package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

/**
 * Created by Andy on 2016/8/24.
 */
public class SettlementCheckLog implements Serializable {
    private int id;
    private String totalCode;
    @EnumVal(EnumValType.Manual)
    private SettlementOrderStatus status;
    private String remark;
    private Date createdTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTotalCode() {
        return totalCode;
    }

    public void setTotalCode(String totalCode) {
        this.totalCode = totalCode;
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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
