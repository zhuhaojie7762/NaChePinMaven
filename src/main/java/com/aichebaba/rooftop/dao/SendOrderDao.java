package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.vo.SettlementExpress;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SendOrderDao extends GeneralCodeDao<SendOrder, Integer> {
    public SendOrderDao() {
        super("send_order", SendOrder.class, "FH", "seq_sendOrder");
    }

    public PageList<SendOrder> findSendOrders(String code, int sendUserId, SendOrderStatus status, Boolean isSend, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from send_order where 1=1");
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(code)) {
            sql.append(" and code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (sendUserId >= 0) {
            sql.append(" and sendUserId = ?");
            paras.add(sendUserId);
        }
        if (status != null) {
            sql.append(" and status=?");
            paras.add(status.name());
        }
        if (isSend != null) {
            sql.append(" and isSend = ?");
            paras.add(isSend);
        }
        pageParam.putSort("id", "desc");
        return findPaging(sql, pageParam, paras);
    }

    public PageList<SendOrder> findSendOrders2(int tradeId, Date fromTime, Date endTime, String sendCode, int expressId, String expressCode, Boolean isSend, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.* from send_order s ")
                .append(" left join trade t on t.id = s.tradeId ")
                .append(" where 1 = 1");
        List<Object> paras = new ArrayList<>();
        if (tradeId != 0) {
            sql.append(" and s.tradeId = ?");
            paras.add(tradeId);
        }
        if (StrKit.notBlank(sendCode)) {
            sql.append(" and s.code = ?");
            paras.add(sendCode);
        }
        if (fromTime != null) {
            sql.append(" and t.created >= ?");
            paras.add(fromTime);
        }
        if (endTime != null) {
            sql.append(" and t.created <= ?");
            paras.add(endTime);
        }
        if(expressId > 0){
            sql.append(" and s.expressId = ? ");
            paras.add(expressId);
        }
        if(StrKit.notBlank(expressCode)){
            sql.append(" and s.expressCode = ? ");
            paras.add(expressCode);
        }
        if (isSend != null) {
            sql.append(" and s.isSend = ?");
            paras.add(isSend);
        }
        pageParam.putSort("id", "desc");
        return findPaging(sql, pageParam, paras);
    }

    public List<SendOrder> findSendOrderList(String code, List<String> codes, Date startTime, Date endTime, int sendUserId, SendOrderStatus status, Boolean isSend, int expressId ) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from send_order where 1=1");
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(code)) {
            sql.append(" and code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (codes != null){
            sql.append(" and " + inSql("code", codes.size()));
            paras.addAll(codes);
        }
        if (startTime != null){
            sql.append(" and created >= ?");
            paras.add(startTime);
        }
        if (endTime != null){
            sql.append(" and created <= ?");
            paras.add(endTime);
        }
        if (sendUserId >= 0) {
            sql.append(" and sendUserId = ?");
            paras.add(sendUserId);
        }
        if (status != null) {
            sql.append(" and status=?");
            paras.add(status.name());
        }
        if (isSend != null) {
            sql.append(" and isSend = ?");
            paras.add(isSend);
        }
        if (expressId > 0) {
            sql.append(" and expressId = ?");
            paras.add(expressId);
        }
        sql.append(" order by id desc ");
        return findSQL(sql.toString(), paras.toArray());
    }

    public SendOrder save(SendOrder sendOrder) {
        if (sendOrder.getId() > 0) {
            update(sendOrder);
        } else {
            sendOrder.setCode(getNextCode());
            sendOrder.setCreated(new Date());
            Object id = add(sendOrder);
            sendOrder.setId(Integer.valueOf(id.toString()));
        }
        return sendOrder;
    }

    public SendOrder getSendOrderByTradeId(int tradeId) {
        return getByWhere("tradeId=?", tradeId);
    }

    public List<SettlementExpress> findTempSettlementExpress(Date startTime, Date endTime, String expressName) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();

        sql.append("select expressId, expressName, count(expressId) quantity, sum(weight) weight, ")
                .append(" sum(realityWeight) realityWeight, sum(freight) freight ")
                .append(" from send_order ")
                .append(" where 1 = 1 ")
                .append(" and status = ? ");
        paras.add(SendOrderStatus.FINISHED.name());
        if (startTime != null) {
            sql.append(" and created >= ? ");
            paras.add(startTime);
        }
        if (endTime != null) {
            sql.append(" and created <= ? ");
            paras.add(endTime);
        }
        if (StrKit.notBlank(expressName)) {
            sql.append(" and expressName like ? ");
            paras.add(StringUtils.likeAllValue(expressName));
        }
        sql.append(" group by expressId, expressName ");
        return findSQL(sql.toString(), SettlementExpress.class, paras.toArray());
    }
}
