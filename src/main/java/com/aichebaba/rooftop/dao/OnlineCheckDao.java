package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.OnlineCheck;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class OnlineCheckDao extends Dao<OnlineCheck, Integer> {
    public OnlineCheckDao(){
        super("online_check", OnlineCheck.class);
    }
}
