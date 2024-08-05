package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.MonthlySettlementDao;
import com.aichebaba.rooftop.model.MonthlySettlement;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthlyReportService {
    @Autowired
    private MonthlySettlementDao monthlyReportDao;

    /**
     * 分页获取统计信息
     * @param supplierId
     * @param pageParam
     * @return
     */
    public PageList<MonthlySettlement> getReport(int supplierId, PageParam pageParam){
        return monthlyReportDao.getReport(supplierId, pageParam);
    }
}
