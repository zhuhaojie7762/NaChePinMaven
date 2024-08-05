package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Auth;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class AuthDao extends Dao<Auth, Integer> {
    public AuthDao() {
        super("auth", Auth.class);
    }
}
