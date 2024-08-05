package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SettlementCheckLog;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Andy on 2016/8/24.
 */
@Repository
public class SettlementCheckLogDao extends Dao<SettlementCheckLog, Integer> {

    public SettlementCheckLogDao() {
        super("settlement_check_log", SettlementCheckLog.class);
    }

    public List<SettlementCheckLog> findByTotalCode(String code){
        StringBuilder sql = new StringBuilder();
        sql.append("select * from settlement_check_log ")
                .append("where totalCode = ? ")
                .append("order by createdTime desc ");
        return findSQL(sql.toString(), code);
    }

    public Date getLastTime(String totalCode){
        StringBuilder sql = new StringBuilder();
        sql.append(" select max(createdTime) createdTime from settlement_check_log ")
                .append("where totalCode = ? ");
        return getSingle(sql.toString(), totalCode);
    }
}
