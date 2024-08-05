package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.MonthlySettlement;
import com.aichebaba.rooftop.model.enums.SettlementState;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class MonthlySettlementDao extends GeneralCodeDao<MonthlySettlement, String> {
    public MonthlySettlementDao() {
        super("monthly_settlement", MonthlySettlement.class, "MR", "seq_monthly_settlement");
    }

    public PageList<MonthlySettlement> getReport(int supplierId, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();

        sql.append("select m.* from monthly_settlement m ").append(" where 1 = 1 ");
        if (supplierId > 0) {
            sql.append(" and m.supplierId = ? ");
            param.add(supplierId);
        }
        return findPaging(sql, pageParam, param);
    }

    public PageList<MonthlySettlement> findSettlements(Date month, String customerCode, String customerName,
            String customerPhone, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();
        sql.append("select o.* from monthly_settlement o join customer c on c.id=o.supplierId ")
                .append(" where 1=1");
        if (month != null) {
            sql.append(" and o.month = ?");
            param.add(month);
        }
        if (StrKit.notBlank(customerCode)) {
            sql.append(" and c.code like ?");
            param.add(StringUtils.likeValue(customerCode));
        }
        if (StrKit.notBlank(customerName)) {
            sql.append(" and c.name like ?");
            param.add(StringUtils.likeValue(customerName));
        }
        if (StrKit.notBlank(customerPhone)) {
            sql.append(" and c.phone like ?");
            param.add(customerPhone);
        }
        pageParam.putSort("o.month", "desc");
        return findPaging(sql, pageParam, param);
    }

    public MonthlySettlement save(MonthlySettlement monthly) {
        if (StrKit.isBlank(monthly.getCode())) {
            monthly.setCode(getNextCode());
            add(monthly);
        } else {
            update(monthly);
        }
        return monthly;
    }

    public void payProvider(String code, long payMoney, String remark) {
        MonthlySettlement settlement = getByPK(code);
        if (settlement != null) {
            settlement.setPayMoney(payMoney);
            settlement.setRemark(remark);
            settlement.setSettlementState(SettlementState.payed);
            update(settlement);
        }
    }
}
