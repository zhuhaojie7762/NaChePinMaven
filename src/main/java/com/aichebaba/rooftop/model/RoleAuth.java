package com.aichebaba.rooftop.model;

import com.google.common.base.Function;

import java.io.Serializable;
import java.util.Date;

public class RoleAuth implements Serializable {
    public static final Function<RoleAuth, Integer> AUTH_ID_VALUE = new Function<RoleAuth, Integer>() {
        @Override
        public Integer apply(RoleAuth r) {
            return r.getAuthId();
        }
    };
    private int roleId;
    private int authId;
    private int authority;
    private int updateUserId;
    private Date updated;
    private int createUserId;
    private Date created;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
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
}
