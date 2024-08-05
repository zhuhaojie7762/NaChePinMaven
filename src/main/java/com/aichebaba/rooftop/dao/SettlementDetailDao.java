package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SettlementDetail;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SettlementDetailDao extends Dao<SettlementDetail, SettlementDetail> {
    public SettlementDetailDao(){
        super("settlement_detail", SettlementDetail.class);
    }
}
