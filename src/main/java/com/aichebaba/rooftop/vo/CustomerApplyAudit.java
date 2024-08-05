package com.aichebaba.rooftop.vo;

import java.io.Serializable;
import java.util.Date;

import com.aichebaba.rooftop.model.enums.CustomerAuditState;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

public class CustomerApplyAudit implements Serializable {
    private int id;
    private int customerId;
    private String code;
    private String name;
    private String username;
    private String phone;
    @EnumVal(EnumValType.Manual)
    private CustomerType type;
    private Date created;

    @EnumVal(EnumValType.Ordinal)
    private CustomerAuditState state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public CustomerAuditState getState() {
        return state;
    }

    public void setState(CustomerAuditState state) {
        this.state = state;
    }
}
