package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.SettlementCheckLogDao;
import com.aichebaba.rooftop.model.SettlementCheckLog;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Andy on 2016/8/24.
 */
@Service
public class SettlementCheckLogService {
    @Autowired
    private SettlementCheckLogDao settlementCheckLogDao;

    public SettlementCheckLog save(SettlementCheckLog settlementCheckLog) {
        if(settlementCheckLog.getId() > 0){
            settlementCheckLogDao.update(settlementCheckLog);
        }else {
            Object o = settlementCheckLogDao.add(settlementCheckLog);
            settlementCheckLog.setId(Integer.parseInt(o.toString()));
        }
        return settlementCheckLog;
    }

    public List<SettlementCheckLog> findByTotalCode(String code){
        return settlementCheckLogDao.findByTotalCode(code);
    }

    /**
     * 获取审核结束时间
     * @param totalCode
     * @return
     */
    public Date getCheckFinishTime(String totalCode){
        SettlementCheckLog checkLog = settlementCheckLogDao.getByWhere("totalCode = ? and status = ?", totalCode, SettlementOrderStatus.wait_pay.getVal());
        if(checkLog == null){
            return null;
        }else{
            return checkLog.getCreatedTime();
        }
    }

    /**
     * 获取最近操作时间
     * @param totalCode
     * @return
     */
    public Date getLastTime(String totalCode){
        return settlementCheckLogDao.getLastTime(totalCode);
    }
}
