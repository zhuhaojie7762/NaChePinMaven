package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Role;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDao extends Dao<Role, Integer> {
    public RoleDao() {
        super("roles", Role.class);
    }
}
