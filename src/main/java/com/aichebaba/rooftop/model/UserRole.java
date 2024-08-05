package com.aichebaba.rooftop.model;

import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.Sql;

import java.io.Serializable;
import java.util.Date;

@Sql(insertSQL = "insert IGNORE into user_role (userId, departId, roleId, updateUserId, updated, createUserId, created) values(?, ?, ?, ?, ?, ?, ?)")
public class UserRole implements Serializable {

    public static final Function<UserRole, Integer> ROLE_ID_VALUE = new Function<UserRole, Integer>() {
        @Override
        public Integer apply(UserRole r) {
            return r.getRoleId();
        }
    };
    private int userId;
    private int roleId;
    private int departId;
    private int updateUserId;
    private Date updated;
    private int createUserId;
    private Date created;

    public UserRole() {

    }

    public UserRole(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getDepartId() {
        return departId;
    }

    public void setDepartId(int departId) {
        this.departId = departId;
    }

    public int getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(int updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserRole userRole = (UserRole) o;

        if (userId != userRole.userId)
            return false;
        if (roleId != userRole.roleId)
            return false;
        return departId == userRole.departId;

    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + roleId;
        result = 31 * result + departId;
        return result;
    }
}
