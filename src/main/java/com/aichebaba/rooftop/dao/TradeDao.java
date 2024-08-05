package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.AllocatingStatus;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Repository
public class TradeDao extends Dao<Trade, Integer> {

    public TradeDao() {
        super("trade", Trade.class);
    }

    public List<Trade> getTradesByPickOrderId(int pickOrderId) {
        return findByWhere("pickOrderId=?", pickOrderId);
    }

    public Trade getTradeById(int id) {
        return getById(id);
    }

    public List<Trade> getTradesByIds(Collection<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("id", ids.size()) + " order by created desc", ids.toArray());
    }

    public List<Trade> findWaitFinishedByBeforeTime(Date endTime) {
        return findByWhere("consignTime <= ? and status in(?, ?, ?) limit 500", endTime, WAIT_RECEIVE.name(),
                REFUND_GOODS_REFUSE.name(), REFUND_REFUSE.name());
    }

    public void finishedTrades(Collection<Integer> ids) {
        if (ids.isEmpty()) {
            return;
        }

        List<Object> params = new ArrayList<>();
        params.add(OrderStatus.FINISHED.name());
        params.addAll(ids);
        updateByWhere("status=? ", inSql("id", ids.size()), params.toArray());
    }

    public PageList<Trade> findTrades(String buyerName, Collection<OrderStatus> statuses, PageParam pageParam) {
        StringBuilder sql = new StringBuilder("select t.* from trade t join customer c on t.buyerId=c.id where 1=1");
        List<Object> args = new ArrayList<>();
        if (StrKit.notBlank(buyerName)) {
            sql.append(" and c.name =?");
            args.add(buyerName);
        }
        if (statuses != null && statuses.size() > 0) {
            sql.append(" and ").append(inSql("t.status", statuses.size()));
            for (OrderStatus status : statuses) {
                args.add(status.name());
            }
        }
        pageParam.putSort("t.created", "desc");
        return findPaging(sql, pageParam, args);
    }

    public List<Trade> findTradesByPickOrderCode(String pickOrderCode, int tradeId) {
        StringBuilder sql = new StringBuilder();
        List<Object> para = new ArrayList<>();
        sql.append("select t.* from trade t ")
                .append(" where t.pickOrderCode = ? ");
        para.add(pickOrderCode);
        if (tradeId != 0) {
            sql.append(" and t.id = ? ");
            para.add(tradeId);
        }
        return findSQL(sql.toString(), para.toArray());
    }

    public PageList<Trade> findWaitSendTrades(int tradeId, String orderCode, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> para = new ArrayList<>();

        sql.append(" select t.* from trade t ")
                .append(" where 1 = 1 ")
                .append(" and t.status = ? ")
                .append(" and t.allocatingStatus = ? ");
        para.add(OrderStatus.PICKING.name());
        para.add(AllocatingStatus.unfinished.getVal());
        if (tradeId != 0) {
            sql.append(" and t.id = ? ");
            para.add(tradeId);
        }
        if (StrKit.notBlank(orderCode)) {
            sql.append(" and t.id in (select tradeId from orders where code = ? ) ");
            para.add(orderCode);
        }
        return findPaging(sql, pageParam, para);
    }

    public PageList<Trade> findWaitPickOrders(Date createdFrom, Date createdEnd, String brand, String goodsItemNo,
                                              String goodsName, int tradeId, String orderCode, String buyerName,
                                              String sellerName, int expressId, OrderStatus status, PageParam pageParam) {

        StringBuilder sql = new StringBuilder();
        sql.append("select t.* from trade t ")
                .append(" where id in (")
                .append(" select tradeId from orders o ")
                .append(" left join customer s on s.id = o.sellerId ")
                .append(" left join customer b on b.id = o.buyerId ")
                .append(" join goods g on o.goodsCode = g.code ")
                .append(" where 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(brand)) {
            sql.append(" and g.brand like ?");
            params.add(StringUtils.likeValue(brand));
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo = ?");
            params.add(goodsItemNo);
        }
        if (StrKit.notBlank(goodsName)) {
            sql.append(" and o.goodsName like ?");
            params.add(StringUtils.likeAllValue(goodsName));
        }
        if (tradeId != 0) {
            sql.append(" and o.tradeId = ?");
            params.add(tradeId);
        }
        if (StrKit.notBlank(orderCode)) {
            sql.append(" and o.code = ?");
            params.add(orderCode);
        }
        if (StrKit.notBlank(buyerName)) {
            sql.append(" and b.name like ?");
            params.add(StringUtils.likeAllValue(buyerName));
        }
        if (StrKit.notBlank(sellerName)) {
            sql.append(" and s.name like ?");
            params.add(StringUtils.likeAllValue(sellerName));
        }
        if (expressId > 0) {
            sql.append(" and t.expressId = ?");
            params.add(expressId);
        }
        if (status != null) {
            sql.append(" and o.status = ? ");
            params.add(status.getVal());
        }
        sql.append(" )");
        if (createdFrom != null) {
            sql.append(" and t.created >= ? ");
            params.add(createdFrom);
        }
        if (createdEnd != null) {
            sql.append(" and t.created <= ? ");
            params.add(createdEnd);
        }
        pageParam.putSort("t.created", "asc");
        return findPaging(sql, pageParam, params);
    }
}
