package com.aichebaba.rooftop.model;

import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 * 地区表(村落)
 */
public class Area implements Serializable{

    private int id;

    private String name;

    private int pickUserId;

    private String remark;

    private Date created;

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

    public int getPickUserId() {
        return pickUserId;
    }

    public void setPickUserId(int pickUserId) {
        this.pickUserId = pickUserId;
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

    private String pickUserName;

    @Transient
    public String getPickUserName() {
        return pickUserName;
    }

    public void setPickUserName(String pickUserName) {
        this.pickUserName = pickUserName;
    }
}
