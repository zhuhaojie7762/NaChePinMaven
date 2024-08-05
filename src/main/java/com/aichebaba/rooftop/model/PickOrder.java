package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.PickOrderStatus;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andy on 2016/7/15.
 */
public class PickOrder implements Serializable {
    private String code;
    private Date created;
    private int pickerId;
    @EnumVal(EnumValType.Manual)
    private PickOrderStatus status;
    private Date lastTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getPickerId() {
        return pickerId;
    }

    public void setPickerId(int pickerId) {
        this.pickerId = pickerId;
    }

    public PickOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PickOrderStatus status) {
        this.status = status;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
