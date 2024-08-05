package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SettlementTotalOrder;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class SettlementTotalOrderDao extends GeneralCodeDao<SettlementTotalOrder, String> {
    public SettlementTotalOrderDao(){
        super("settlement_total_order", SettlementTotalOrder.class, "ST", "seq_settlement_total");
    }

    public List<SettlementTotalOrder> findSettlementTotalOrder(String code, Collection<SettlementOrderStatus> statuses, Date startTime, Date endTime){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from settlement_total_order ")
                .append(" where 1 = 1 ");
        if(StrKit.notBlank(code)){
            sql.append(" and code like ? ");
            paras.add(StringUtils.likeAllValue(code));
        }
        if(statuses != null){
            sql.append(" and " + inSql("status", statuses.size()));
            for(SettlementOrderStatus status : statuses){
                paras.add(status.getVal());
            }
        }
        if(startTime != null){
            sql.append(" and created >= ? ");
            paras.add(startTime);
        }
        if(endTime != null){
            sql.append(" and created <= ? ");
            paras.add(endTime);
        }
        return findSQL(sql.toString(), paras.toArray());
    }
}
