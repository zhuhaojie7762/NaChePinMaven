package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.RoleAuth;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class RoleAuthDao extends Dao<RoleAuth, RoleAuth> {
    public RoleAuthDao() {
        super("role_auth", RoleAuth.class);
    }

    public List<RoleAuth> getRoleAuth(Collection<Integer> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("roleId", roleIds.size()), roleIds.toArray());
    }
}
