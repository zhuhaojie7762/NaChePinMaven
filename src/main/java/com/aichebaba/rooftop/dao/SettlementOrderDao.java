package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SettlementOrder;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class SettlementOrderDao extends GeneralCodeDao<SettlementOrder, String> {
    public SettlementOrderDao(){
        super("settlement_order", SettlementOrder.class, "SE", "seq_settlement");
    }

    public SettlementOrder save(SettlementOrder settlementOrder){
        if (StrKit.notBlank(settlementOrder.getCode())) {
            update(settlementOrder);
        } else {
            settlementOrder.setCode(getNextCode());
            settlementOrder.setCreated(new Date());
            add(settlementOrder);
        }
        return settlementOrder;
    }

    public List<SettlementOrder> findSettlementOrder(int supplierId, Collection<SettlementOrderStatus> statuses,
                                                     String totalCode, String supplierName, SettlementMethodType method, Long minSettlementMoney){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from settlement_order ")
                .append(" where 1 = 1 ");
        if (supplierId > 0) {
            sql.append(" and supplierId = ? ");
            paras.add(supplierId);
        }
        if (statuses != null) {
            sql.append(" and " + inSql("status", statuses.size()));
            for (SettlementOrderStatus status : statuses) {
                paras.add(status.getVal());
            }
        }
        if (StrKit.notBlank(totalCode)) {
            sql.append(" and totalCode = ? ");
            paras.add(totalCode);
        }
        if (StrKit.notBlank(supplierName)) {
            sql.append(" and supplierCompany like ? ");
            paras.add(StringUtils.likeAllValue(supplierName));
        }
        if (method != null) {
            sql.append(" and settlementMethod = ? ");
            paras.add(method.name());
        }
        if (minSettlementMoney != null && minSettlementMoney > 0) {
            sql.append(" and saleMoney - backMoney >= ?");
            paras.add(minSettlementMoney);
        }
        return findSQL(sql.toString(), paras.toArray());
    }

    public List<SettlementOrder> findSettlementOrderByCodes(Collection<String> codes) {
        return findByWhere(inSql("code", codes.size()), codes.toArray());
    }

    /**
     * He daoyuan
     * 供货商账单
     */
    public PageList<SettlementOrder> queryToBill(SettlementOrder settlementOrder, List<SettlementOrderStatus> statuses, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from settlement_order where 1 = 1");

        if (settlementOrder.getSupplierId() > 0) {
            sql.append(" and supplierId = ?");
            params.add(settlementOrder.getSupplierId());
        }
        if (StrKit.notBlank(settlementOrder.getCode())) {
            sql.append(" and `code` = ?");
            params.add(settlementOrder.getCode());
        }
        if (settlementOrder.getStartTime() != null && settlementOrder.getEndTime() != null) {
            sql.append(" and created >= ?");
            params.add(settlementOrder.getStartTime());
            sql.append(" and created <= ?");
            params.add(settlementOrder.getEndTime());
        }
        if (statuses != null && statuses.size() > 0) {
            sql.append(" and " + inSql("`status`", statuses.size()));
            List<Object> listIn = new ArrayList<>();
            for (SettlementOrderStatus status : statuses)
                listIn.add(status.getVal());
            params.addAll(listIn);
        }

        pageParam.putSort("created", "desc");
        return findPaging(sql, pageParam, params);
    }

    /**
     * 结算总汇 hdy
     * @param settlementOrder
     * @param statuses
     * @param pageParam
     * @return
     */
    public PageList<SettlementOrder> getSettlementCleanAccount(SettlementOrder settlementOrder, List<SettlementOrderStatus> statuses, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from settlement_order where 1 = 1");

        if(StrKit.notBlank(settlementOrder.getSupplierCompany())) {
            sql.append(" and supplierCompany like ?");
            params.add(StringUtils.likeAllValue(settlementOrder.getSupplierCompany()));
        }
        if(StrKit.notBlank(settlementOrder.getAlipayCode())) {
            sql.append(" and alipayCode like ?");
            params.add(StringUtils.likeAllValue(settlementOrder.getAlipayCode()));
        }
        if(StrKit.notBlank(settlementOrder.getAlipayName())) {
            sql.append(" and alipayName like ?");
            params.add(StringUtils.likeAllValue(settlementOrder.getAlipayName()));
        }
        if(settlementOrder.getSettlementMethod() != null) {
            sql.append(" and settlementMethod = ?");
            params.add(settlementOrder.getSettlementMethod().name());
        }
        if (settlementOrder.getStartTime() != null && settlementOrder.getEndTime() != null) {
            sql.append(" and startTime >= ?");
            params.add(settlementOrder.getStartTime());
            sql.append(" and startTime <= ?");
            params.add(settlementOrder.getEndTime());

            sql.append(" and endTime >= ?");
            params.add(settlementOrder.getStartTime());
            sql.append(" and endTime <= ?");
            params.add(settlementOrder.getEndTime());
        }
        if (statuses != null && statuses.size() > 0) {
            sql.append(" and " + inSql("`status`", statuses.size()));
            List<Object> listIn = new ArrayList<>();
            for (SettlementOrderStatus status : statuses)
                listIn.add(status.getVal());
            params.addAll(listIn);
        }

        pageParam.putSort("created", "desc");
        return findPaging(sql, pageParam, params);
    }

    public SettlementOrder getLastSettlementOrder(int supplierId){
        StringBuilder sql = new StringBuilder();
        sql.append("select * from settlement_order ")
                .append("where supplierId = ? ")
                .append("and status = ? ")
                .append("order by created")
                .append(" limit 1 ");
        return getBySQL(sql.toString(), supplierId, SettlementOrderStatus.finish.getVal());
    }
}
