package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SkuProp;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class SkuPropDao extends Dao<SkuProp, Integer> {

    public SkuPropDao() {
        super("sku_prop", SkuProp.class);
    }
}