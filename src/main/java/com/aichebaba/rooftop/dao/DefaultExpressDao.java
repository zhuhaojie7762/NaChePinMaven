package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.DefaultExpress;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultExpressDao extends Dao<DefaultExpress, Integer> {
    public DefaultExpressDao(){
        super("default_express", DefaultExpress.class);
    }

    public DefaultExpress getByCustomerId(int customerId){
        return getByWhere("customerId = ? ", customerId);
    }
}
