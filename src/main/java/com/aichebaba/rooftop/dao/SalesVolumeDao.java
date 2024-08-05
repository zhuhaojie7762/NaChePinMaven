package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SalesVolume;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class SalesVolumeDao extends Dao<SalesVolume, Integer> {
    public SalesVolumeDao() {
        super("sales_volume", SalesVolume.class);
    }

    public SalesVolume getByGooodsCode(String goodsCode){
        return getByWhere("goodsCode = ?" ,goodsCode);
    }
}
