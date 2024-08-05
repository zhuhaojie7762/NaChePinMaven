package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.UserRole;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRoleDao extends Dao<UserRole, UserRole> {
    public UserRoleDao() {
        super("user_role", UserRole.class);
    }

    public List<UserRole> getUserRoles(int userId) {
        return findByWhere("userId=?", userId);
    }

    public List<Integer> getUsersByRole(int roleId) {
        return findSingleList("select userId from user_role where roleId=?", roleId);
    }
}
