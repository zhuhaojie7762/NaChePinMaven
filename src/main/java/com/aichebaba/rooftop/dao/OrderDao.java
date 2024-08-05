package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.RefundParam;
import com.aichebaba.rooftop.model.SettlementOrder;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.search.QueryTimeParam;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.SqlHelper;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.vo.ActivityJobCustomerST;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Repository
public class OrderDao extends GeneralCodeDao<Order, String> {

    public OrderDao() {
        super("orders", Order.class, "DD", "seq_order");
    }

    public PageList<Order> findOrders(String code, String goodsCode, String goodsName, int sellerId, int buyerId, String goodsItemNo,
                                      String sellerPhone, String buyerPhone, OrderStatus status, Date createdFrom, Date createdEnd,
                                      PageParam pageParam) {
        List<OrderStatus> statusList = status == null ? null : Arrays.asList(status);

        return findOrders(code, goodsCode, goodsName, sellerId, buyerId, goodsItemNo, sellerPhone, buyerPhone, statusList,
                createdFrom, createdEnd, pageParam);
    }

    public PageList<Order> findOrders(String code, String goodsCode, String goodsName, int sellerId, int buyerId, String goodsItemNo,
                                      String sellerPhone, String buyerPhone, Collection<OrderStatus> statusList, Date createdFrom,
                                      Date createdEnd, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select o.*, c.alipayName, c.alipayCode from `orders` o")
                .append(" join customer c on o.buyerId=c.id")
                .append(" where 1=1");
        if (StrKit.notBlank(code)) {
            sql.append(" and o.code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(goodsCode)) {
            sql.append(" and o.goodsCode = ?");
            paras.add(goodsCode);
        }
        if (StrKit.notBlank(goodsName)) {
            sql.append(" and o.goodsName like ?");
            paras.add(StringUtils.likeAllValue(goodsName));
        }
        if (sellerId > 0) {
            sql.append(" and o.sellerId = ?");
            paras.add(sellerId);
        }
        if (buyerId > 0) {
            sql.append(" and o.buyerId = ?");
            paras.add(buyerId);
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo like ?");
            paras.add(StringUtils.likeValue(goodsItemNo));
        }
        if (StrKit.notBlank(sellerPhone)) {
            sql.append(" and o.sellerPhone like ?");
            paras.add(StringUtils.likeValue(sellerPhone));
        }
        if (StrKit.notBlank(buyerPhone)) {
            sql.append(" and o.buyerPhone like ?");
            paras.add(StringUtils.likeValue(buyerPhone));
        }
        if (statusList != null && !statusList.isEmpty()) {
            sql.append(" and ").append(inSql("o.status", statusList.size()));
            List<Object> statusValues = new ArrayList<>();
            for (OrderStatus s : statusList) {
                statusValues.add(s.getVal());
            }
            paras.addAll(statusValues);
        }
        if (createdFrom != null) {
            sql.append(" and o.created >= ?");
            paras.add(createdFrom);
        }
        if (createdEnd != null) {
            sql.append(" and o.created <= ?");
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
            String temp = sFormat.format(createdEnd);
            SimpleDateFormat sFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                createdEnd = sFormat2.parse(temp + " 23:59:59");
            }catch (ParseException ex){

            }
            paras.add(createdEnd);
        }
        pageParam.putSort("o.created", "desc");
        return findPaging(sql, pageParam, paras);
    }

    /**
     * 分页查询订单信息(进货商/供货商订单列表用)
     * @param params            参数如下
     *        brand             品牌名      模糊查询
     *        tradeId           总订单ID    模糊查询
     *        orderCode         子订单号    模糊查询
     *        purchaseComment   进货商备注   模糊查询
     *        supplierComment   供货商备注   模糊查询
     *        managerComment    管理员备注   模糊查询
     *        goodsName         商品名称    模糊查询
     *        goodsItemNO       商品货号    模糊查询
     *        expressId         快递公司ID
     *        expressCode       快递单号    模糊查询
     *        receiveName       收货人姓名   模糊查询
     *        createdFrom       创建时间（开始）
     *        createdEnd        创建时间（结束）
     *        status            订单状态
     *        purchaseId        进货商ID
     *        supplierId        供货商ID
     * @param pageParam
     * @return
     */
    public PageList<Order> findOrderList(Map<String, Object> params, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select o.*, g.headImgUrl1 headImg, g.brand, c.alipayName, c.alipayCode")
                .append(" from `orders` o ")
                .append(" join goods g on o.goodsCode = g.code")
                .append(" join customer c on o.buyerId=c.id")
                .append(" join trade t on o.tradeId = t.id")
                .append(" where 1=1");
        String strTmp;
        Integer intTmp;
        //品牌名
        strTmp = (String) params.get("brand");
        if(StrKit.notBlank(strTmp)){
            sql.append(" and g.brand like ? ");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //总订单ID
        intTmp = (Integer) params.get("tradeId");
        if(intTmp != null && intTmp != 0){
            sql.append(" and o.tradeId like ? ");
            paras.add(StringUtils.likeValue(intTmp.toString()));
        }
        //子订单号
        strTmp = (String) params.get("orderCode");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and o.code like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //进货商名
        strTmp = (String) params.get("purchaseName");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and c.name like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //供货商名
        strTmp = (String) params.get("supplierName");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and o.sellerId in (select id from customer where name like ?)");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //进货商备注
        strTmp = (String) params.get("purchaseComment");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and t.purchaseComment like ?");
            paras.add(StringUtils.likeAllValue(strTmp));
        }
        //供货商备注
        strTmp = (String) params.get("supplierComment");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and t.supplierComment like ?");
            paras.add(StringUtils.likeAllValue(strTmp));
        }
        //管理员备注
        strTmp = (String) params.get("managerComment");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and t.managerComment like ?");
            paras.add(StringUtils.likeAllValue(strTmp));
        }
        //商品名称
        strTmp = (String) params.get("goodsName");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and o.goodsName like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //商品货号
        strTmp = (String) params.get("goodsItemNo");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and o.goodsItemNo like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //快递公司
        intTmp = (Integer) params.get("expressId");
        if(intTmp != 0){
            sql.append(" and t.expressId = ? ");
            paras.add(intTmp);
        }
        //快递单号
        strTmp = (String) params.get("expressCode");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and t.expressCode like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //收货人姓名
        strTmp = (String) params.get("receiveName");
        if (StrKit.notBlank(strTmp)) {
            sql.append(" and t.receiveName like ?");
            paras.add(StringUtils.likeValue(strTmp));
        }
        //订单开始时间
        Date dateTmp;
        dateTmp = (Date) params.get("createdFrom");
        if (dateTmp != null) {
            sql.append(" and o.created >= ?");
            paras.add(dateTmp);
        }
        //订单结束时间
        dateTmp = (Date) params.get("createdEnd");
        if (dateTmp != null) {
            sql.append(" and o.created <= ?");
            paras.add(DateUtil.getDayEnd(dateTmp));
        }
        //订单状态
        OrderStatus status = (OrderStatus) params.get("status");
        if (status != null) {
            sql.append(" and o.status = ? ");
            paras.add(status.getVal());
        }
        //进货商ID
        intTmp = (Integer) params.get("purchaseId");
        if(intTmp != null && intTmp != 0){
            sql.append(" and o.buyerId = ? ");
            paras.add(intTmp);
        }
        //供货商ID
        intTmp = (Integer) params.get("supplierId");
        if(intTmp != null && intTmp != 0){
            sql.append(" and o.sellerId = ? ");
            paras.add(intTmp);
        }

        pageParam.putSort("o.created", "desc");
        return findPaging(sql, pageParam, paras);
    }

    public List<Order> getOrdersByTradeAndCreatedRange(int tradeId, Date createdFrom, Date createdEnd) {
        return findByWhere("tradeId=? and created >=? and created <?", tradeId, createdFrom, createdEnd);
    }

    public List<Order> getOrderByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("code", codes.size()), codes.toArray());
    }

    public List<Order> getOrdersByTradeIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("tradeId", ids.size()), ids.toArray());
    }

    public List<Order> getOrdersByPickAndCreatedRange(int pickOrderId, Date createdFrom, Date createdEnd) {
        return findSQL(
                "select o.* from orders o join trade t on o.tradeId=t.id where t.pickOrderId=? and o.created>=? and o.created<?",
                pickOrderId, createdFrom, createdEnd);
    }

    public List<Order> findOrders(int customerId, Date payTimeFrom, Date payTimeEnd, Collection<OrderStatus> statuses) {
        if (payTimeFrom == null || payTimeEnd == null) {
            return Collections.emptyList();
        }
        List<Order> orders;
        List<Object> paras = new ArrayList<>();
        paras.add(customerId);
        paras.add(payTimeFrom);
        paras.add(payTimeEnd);

        if (statuses == null) {
            paras.add(OrderStatus.WAIT_PICKING.getVal());
            orders = findSQL("select o.* from orders o JOIN trade t on o.tradeId = t.id where o.sellerId = ? and t.consignTime >= ? and t.consignTime < ? and o.`status` >= ?", paras.toArray());
            //orders = findByWhere("sellerId=? and payTime >= ? and payTime < ? and status >= ? ", paras.toArray());
        } else {
            for (OrderStatus status : statuses) {
                paras.add(status.getVal());
            }
            orders = findSQL("select o.* from orders o JOIN trade t on o.tradeId = t.id where o.sellerId = ? and t.consignTime >= ? and t.consignTime < ? and " + inSql("o.status", statuses.size()), paras.toArray());
            //orders = findByWhere("sellerId=? and payTime >= ? and payTime < ? and " + inSql("status", statuses.size()), paras.toArray());
        }
        return orders;
    }

    public List<Order> findMonthOrders(int customerId, Date endTimeFrom, Date endTimeEnd, Collection<OrderStatus> statuses) {
        if (statuses.isEmpty() || endTimeFrom == null || endTimeEnd == null) {
            return Collections.emptyList();
        }
        List<Object> paras = new ArrayList<>();
        paras.add(customerId);
        paras.add(endTimeFrom);
        paras.add(endTimeEnd);
        for (OrderStatus status : statuses) {
            paras.add(status.getVal());
        }
        return findByWhere("sellerId=? and endTime >= ? and endTime <= ? and " + inSql("status", statuses.size()),
                paras.toArray());
    }

    public PageList<Order> findMonthOrderPager(int customerId, Date endTimeFrom, Date endTimeEnd,
                                               Collection<OrderStatus> statuses, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append(" select * from orders where sellerId=? and endTime >= ? and endTime <= ? and ").append(inSql("status", statuses.size()));
        paras.add(customerId);
        paras.add(endTimeFrom);
        paras.add(endTimeEnd);
        for (OrderStatus status : statuses) {
            paras.add(status.getVal());
        }

        return findPaging(sql, pageParam, paras);
    }

    public PageList<Order> findPickingOrders(Date createdFrom, Date createdEnd, Collection<OrderStatus> statuses,
                                             String code, String goodsCode, String buyerPhone, String pickUser,
                                             String goodsItemNo, String sellerPhone, String sellerAddress,
                                             Boolean pickingCancel,
                                             PageParam pageParam) {
        if ((createdFrom == null || createdEnd == null) || statuses == null || statuses.isEmpty()) {
            return new PageList<Order>(null, 1, 20, 1);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select o.*, s.emergencyContact, s.emergencyPhone, s.name sellerName, s.address, s.fullAddress, s.pickAddress from orders o")
                .append(" left join user pu on o.pickerId=pu.id")
                .append(" left join customer s on s.id = o.sellerId")
                .append(" where o.created >=? and  o.created <=? ");

        List<Object> params = new ArrayList<>();
        params.add(createdFrom);
        params.add(createdEnd);
        for (OrderStatus status : statuses) {
            params.add(status.getVal());
        }

        if (pickingCancel != null && pickingCancel) {
            sql.append(" and (").append(inSql("o.status", statuses.size())).append("  or (o.status in (?, ?, ?) and o.picking = ?))");
            params.add(WAIT_AGREE_CANCEL.getVal());
            params.add(WAIT_REFUND_BY_CANCEL.getVal());
            params.add(CLOSED_BY_CANCEL.getVal());
            params.add(true);
        } else {
            sql.append(inSql("and o.status", statuses.size()));
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and o.code like ?");
            params.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(goodsCode)) {
            sql.append(" and o.goodsCode like ?");
            params.add(StringUtils.likeValue(goodsCode));
        }
        if (StrKit.notBlank(buyerPhone)) {
            sql.append(" and o.buyerPhone like ?");
            params.add(StringUtils.likeValue(buyerPhone));
        }
        if (StrKit.notBlank(pickUser)) {
            sql.append(" and pu.name like ?");
            params.add(StringUtils.likeValue(pickUser));
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo like ?");
            params.add(StringUtils.likeValue(goodsItemNo));
        }
        if (StrKit.notBlank(sellerPhone)) {
            sql.append(" and o.sellerPhone like ?");
            params.add(StringUtils.likeValue(sellerPhone));
        }
        if (StrKit.notBlank(sellerAddress)) {
            sql.append(" and s.pickAddress like ?");
            params.add(StringUtils.likeAllValue(sellerAddress));
        }
        pageParam.putSort("o.created", "asc");
        return findPaging(sql, pageParam, params);
    }

    public PageList<Order> findWaitPickOrders(Date createdFrom, Date createdEnd, String brand, String goodsItemNo,
                                              String goosdName, int tradeId, String orderCode, String buyerName,
                                              String sellerName, int expressId, OrderStatus status, PageParam pageParam) {

        StringBuilder sql = new StringBuilder();
        sql.append("select o.*, g.brand, s.emergencyPhone, s.name sellerName, s.address, s.fullAddress, s.pickAddress from orders o")
                .append(" left join user pu on o.pickerId=pu.id")
                .append(" left join customer s on s.id = o.sellerId")
                .append(" left join customer b on b.id = o.buyerId")
                .append(" join goods g on o.goodsCode = g.code ")
                .append(" join trade t on o.tradeId = t.id ")
                .append(" where 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if(createdFrom != null){
            sql.append(" and o.created >= ? ");
            params.add(createdFrom);
        }
        if(createdEnd != null){
            sql.append(" and o.created <= ? ");
            params.add(createdEnd);
        }
        if (StrKit.notBlank(brand)) {
            sql.append(" and g.brand like ?");
            params.add(StringUtils.likeValue(brand));
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo = ?");
            params.add(goodsItemNo);
        }
        if (StrKit.notBlank(goosdName)) {
            sql.append(" and o.goodsCode like ?");
            params.add(StringUtils.likeValue(goosdName));
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
            params.add(StringUtils.likeValue(buyerName));
        }
        if (StrKit.notBlank(sellerName)) {
            sql.append(" and s.name like ?");
            params.add(StringUtils.likeValue(sellerName));
        }
        if (expressId > 0) {
            sql.append(" and t.expressId = ?");
            params.add(expressId);
        }
        if(status != null){
            sql.append(" and o.status = ? ");
            params.add(status.getVal());
        }
        pageParam.putSort("o.created", "asc");
        return findPaging(sql, pageParam, params);
    }

    public List<Order> findPickingOrders(Date createdFrom, Date createdEnd, Collection<OrderStatus> statuses,
                                         String code, String goodsCode, String buyerPhone, String pickUser,
                                         String goodsItemNo, String sellerPhone, String sellerAddress,
                                         Boolean pickingCancel) {
        if ((createdFrom == null || createdEnd == null) || statuses == null || statuses.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select o.*, s.emergencyContact, s.emergencyPhone, s.name sellerName, s.address, s.fullAddress, s.pickAddress from orders o")
                .append(" left join user pu on o.pickerId=pu.id")
                .append(" left join customer s on s.id = o.sellerId")
                .append(" where o.created >=? and  o.created <=? ");

        List<Object> params = new ArrayList<>();
        params.add(createdFrom);
        params.add(createdEnd);
        for (OrderStatus status : statuses) {
            params.add(status.getVal());
        }

        if (pickingCancel != null && pickingCancel) {
            sql.append(" and (").append(inSql("o.status", statuses.size())).append("  or (o.status in (?, ?, ?) and o.picking = ?))");
            params.add(WAIT_AGREE_CANCEL.getVal());
            params.add(WAIT_REFUND_BY_CANCEL.getVal());
            params.add(CLOSED_BY_CANCEL.getVal());
            params.add(true);
        } else {
            sql.append(inSql("and o.status", statuses.size()));
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and o.code like ?");
            params.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(goodsCode)) {
            sql.append(" and o.goodsCode like ?");
            params.add(StringUtils.likeValue(goodsCode));
        }
        if (StrKit.notBlank(buyerPhone)) {
            sql.append(" and o.buyerPhone like ?");
            params.add(StringUtils.likeValue(buyerPhone));
        }
        if (StrKit.notBlank(pickUser)) {
            sql.append(" and pu.name like ?");
            params.add(StringUtils.likeValue(pickUser));
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo like ?");
            params.add(StringUtils.likeValue(goodsItemNo));
        }
        if (StrKit.notBlank(sellerPhone)) {
            sql.append(" and o.sellerPhone like ?");
            params.add(StringUtils.likeValue(sellerPhone));
        }
        if (StrKit.notBlank(sellerAddress)) {
            sql.append(" and s.pickAddress like ?");
            params.add(StringUtils.likeAllValue(sellerAddress));
        }
        sql.append(" order by o.created asc");
        return findSQL(sql.toString(), params.toArray());
    }

    public PageList<Order> findPickingOrders(int pickUserId, OrderStatus status, String code, String goodsCode,
                                             int sellerId, int buyerId, String goodsItemNo, String sellerPhone,
                                             String buyerPhone, Date payTimeFrom,
                                             Date payTimeEnd, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select o.*, s.emergencyContact, s.emergencyPhone, s.name sellerName, s.address, s.fullAddress, s.pickAddress from `orders`")
                .append(" o join customer s on o.sellerId=s.id where o.pickerId=? and o.status=?");
        paras.add(pickUserId);
        paras.add(status.getVal());
        if (StrKit.notBlank(code)) {
            sql.append(" and o.code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(goodsCode)) {
            sql.append(" and o.goodsCode = ?");
            paras.add(goodsCode);
        }
        if (sellerId > 0) {
            sql.append(" and o.sellerId = ?");
            paras.add(sellerId);
        }
        if (buyerId > 0) {
            sql.append(" and o.buyerId = ?");
            paras.add(buyerId);
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo like ?");
            paras.add(StringUtils.likeValue(goodsItemNo));
        }
        if (StrKit.notBlank(sellerPhone)) {
            sql.append(" and o.sellerPhone like ?");
            paras.add(StringUtils.likeValue(sellerPhone));
        }
        if (StrKit.notBlank(buyerPhone)) {
            sql.append(" and o.buyerPhone like ?");
            paras.add(StringUtils.likeValue(buyerPhone));
        }
        if (payTimeFrom != null) {
            sql.append(" and o.payTime >= ?");
            paras.add(payTimeFrom);
        }
        if (payTimeEnd != null) {
            sql.append(" and o.payTime <= ?");
            paras.add(payTimeEnd);
        }
        return findPaging(sql, pageParam, paras);
    }

    public PageList<Order> findPickedOrders(String pickName, String code, String goodsCode, String goodsItemNo, String sellerAddress,
                                            String sellerPhone, String buyerPhone, Date createdFrom, Date createdEnd, PageParam pageParam) {
        if ((createdFrom == null || createdEnd == null)) {
            return new PageList<Order>(null, 1, 20, 1);
        }
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select o.*,  s.emergencyContact, s.emergencyPhone, s.name sellerName, s.address, s.fullAddress, s.pickAddress from `orders`")
                .append(" o join customer s on o.sellerId=s.id left join user pu on o.pickerId=pu.id where o.status >=? and o.created >=? and o.created <=? ");
        paras.add(OrderStatus.PICK_FINISHED.getVal());
        paras.add(createdFrom);
        paras.add(createdEnd);
        if (StrKit.notBlank(pickName)) {
            sql.append(" and pu.name like ?");
            paras.add(StringUtils.likeValue(pickName));
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and o.code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(goodsCode)) {
            sql.append(" and o.goodsCode = ?");
            paras.add(goodsCode);
        }
        if (StrKit.notBlank(goodsItemNo)) {
            sql.append(" and o.goodsItemNo like ?");
            paras.add(StringUtils.likeValue(goodsItemNo));
        }
        if (StrKit.notBlank(sellerAddress)) {
            sql.append(" and s.pickAddress like ?");
            paras.add(StringUtils.likeAllValue(sellerAddress));
        }
        if (StrKit.notBlank(sellerPhone)) {
            sql.append(" and o.sellerPhone like ?");
            paras.add(StringUtils.likeValue(sellerPhone));
        }
        if (StrKit.notBlank(buyerPhone)) {
            sql.append(" and o.buyerPhone like ?");
            paras.add(StringUtils.likeValue(buyerPhone));
        }
        sql.append(" order by created asc");
        return findPaging(sql, pageParam, paras);
    }

    public Order getOrderInfoByCode(String code) {
        StringBuilder sql = new StringBuilder();
        sql.append("select o.*, t.payTime, t.consignTime from order o ")
                .append("left join trade t on o.tradeId = t.id ").append(" where o.code = ? ");
        return getBySQL(sql.toString(), code);
    }

    public List<Order> getOrdersByTradeId(int tradeId) {
        return findByWhere("tradeId=?", tradeId);
    }

    public List<Order> getOrdersByTradeId(int tradeId, OrderStatus status) {
        return findByWhere("tradeId=? and status=?", tradeId, status.getVal());
    }

    public List<Order> getOrdersBySendOrderId(int sendOrderId) {
        return findByWhere("sendOrderId=?", sendOrderId);
    }

    public void updatePicker(Collection<Integer> buyerIds, int newPickerId) {
        if (buyerIds.isEmpty()) {
            return;
        }
        List<Object> paras = new ArrayList<>();
        paras.add(newPickerId);
        paras.add(newPickerId);
        paras.add(OrderStatus.WAIT_PICKING.getVal());
        paras.addAll(buyerIds);
        update("update orders o join send_order so on o.sendOrderId=so.id " +
                "set so.sendUserId=?, o.pickerId=? " +
                "where o.`status` = ? and " + inSql("buyerId", buyerIds.size()), paras.toArray());
    }

    public Multimap<Integer, Order> findOrdersByTradeIds(Collection<Integer> tradeIds) {
        if (tradeIds.isEmpty()) {
            return ArrayListMultimap.create();
        }
        List<Order> orders = findByWhere(inSql("tradeId", tradeIds.size()), tradeIds.toArray());
        return Multimaps.index(orders, Order.tradeIdValue);
    }

    public List<Order> getWaitReceiveOrders(Date endTime) {
        return findSQL("select o.* from orders o join trade t on o.tradeId=t.id " +
                "where t.consignTime <=? and o.`status`=?", endTime, OrderStatus.WAIT_RECEIVE.getVal());
    }

    public void finishedOrdersByTradeIds(Collection<Integer> tradeIds) {
        if (tradeIds.isEmpty()) {
            return;
        }

        List<Object> params = new ArrayList<>();
        params.add(OrderStatus.FINISHED.getVal());
        params.add(new Date());
        params.addAll(tradeIds);
        updateByWhere("status=? and endTime=?", inSql("tradeId", tradeIds.size()), params.toArray());
    }

    public PageList<Order> findPaidOrders(Date payTimeFrom, Date payTimeEnd, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from orders o where 1=1");
        if (payTimeFrom != null) {
            sql.append(" and o.payTime >= ?");
            params.add(payTimeEnd);
        }
        if (payTimeEnd != null) {
            sql.append(" and o.payTime <= ?");
            params.add(payTimeEnd);
        }
        return findPaging(sql, pageParam, params);
    }

    public PageList<Order> findRefundOrders(Date applyRefundTimeFrom, Date applyRefundTimeEnd, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from orders o where 1=1");
        if (applyRefundTimeFrom != null) {
            sql.append(" and o.applyRefundTime >= ?");
            params.add(applyRefundTimeFrom);
        }
        if (applyRefundTimeEnd != null) {
            sql.append(" and o.applyRefundTime <= ?");
            params.add(applyRefundTimeEnd);
        }
        return findPaging(sql, pageParam, params);
    }

    public PageList<Order> findOrders(String sellerCode, String sellerUsername, String sellerName,
                                      Collection<OrderStatus> statuses, long paymentFrom, long paymentTo,
                                      Date createdFrom, Date createdTo, PageParam pageParam) {
        if (statuses == null || statuses.isEmpty()) {
            return new PageList<>(null, 0, 10, 1);
        }
        StringBuilder sql = new StringBuilder("select o.*, ")
                .append(" s.code sellerCode, s.username sellerUsername, s.supplierCompany sellerCompany, s.name sellerName, ")
                .append(" b.Code buyerCode, b.username buyerUsername, b.name buyerName ")
                .append(" from orders o join customer s on o.sellerId=s.id ")
                .append(" join customer b on o.buyerId = b.id ")
                .append(" where 1 = 1 ")
                .append(" and " + inSql("o.status", statuses.size()));
        List<Object> args = new ArrayList<>();
        for (OrderStatus status : statuses) {
            args.add(status.getVal());
        }
        if (StrKit.notBlank(sellerCode)) {
            sql.append(" and s.code like ?");
            args.add(StringUtils.likeValue(sellerCode));
        }
        if (StrKit.notBlank(sellerUsername)) {
            sql.append(" and s.username like ?");
            args.add(StringUtils.likeValue(sellerUsername));
        }
        if (StrKit.notBlank(sellerName)) {
            sql.append(" and s.name like ?");
            args.add(StringUtils.likeValue(sellerName));
        }
        if (paymentFrom > 0) {
            sql.append(" and o.money >= ?");
            args.add(paymentFrom);
        }
        if (paymentTo > 0) {
            sql.append(" and o.money <= ?");
            args.add(paymentTo);
        }
        if (createdFrom != null) {
            sql.append(" and o.created >=?");
            args.add(createdFrom);
        }
        if (createdTo != null) {
            sql.append(" and o.created <=?");
            args.add(createdTo);
        }
        sql.append(" order by o.created ");
        return findPaging(sql, pageParam, args);
    }

    /**
     * 查找订单
     * @param sellerCode        供货商编号
     * @param sellerUsername    供货商账号
     * @param sellerName        供货商名称
     * @param buyerUsername     进货商账号
     * @param buyerName         进货商名称
     * @param statuses          订单状态
     * @param paymentFrom       金额-开始
     * @param paymentTo         金额-结束
     * @param createdFrom       创建时间-开始
     * @param createdTo         创建时间-结束
     * @return
     */
    public List<Order> findOrders(String sellerCode, String sellerUsername, String sellerName,int buyerId, String buyerUsername, String buyerName,
                                  Collection<OrderStatus> statuses, long paymentFrom, long paymentTo,
                                  Date createdFrom, Date createdTo) {
        if (statuses == null || statuses.isEmpty()) {
            return new ArrayList<Order>();
        }
        StringBuilder sql = new StringBuilder("select o.*, ")
                .append(" s.code sellerCode, s.username sellerUsername, s.supplierCompany sellerCompany, s.name sellerName, ")
                .append(" b.Code buyerCode, b.username buyerUsername, b.name buyerName ")
                .append(" from orders o join customer s on o.sellerId=s.id ")
                .append(" join customer b on o.buyerId = b.id ")
                .append(" where 1 = 1 ")
                .append(" and " + inSql("o.status", statuses.size()));
        List<Object> args = new ArrayList<>();
        for (OrderStatus status : statuses) {
            args.add(status.getVal());
        }
        if (StrKit.notBlank(sellerCode)) {
            sql.append(" and s.code like ?");
            args.add(StringUtils.likeValue(sellerCode));
        }
        if (StrKit.notBlank(sellerUsername)) {
            sql.append(" and s.username like ?");
            args.add(StringUtils.likeValue(sellerUsername));
        }
        if (StrKit.notBlank(sellerName)) {
            sql.append(" and s.name like ?");
            args.add(StringUtils.likeValue(sellerName));
        }
        if (buyerId > 0) {
            sql.append(" and o.buyerId = ? ");
            args.add(buyerId);
        }
        if (StrKit.notBlank(buyerUsername)) {
            sql.append(" and b.username like ?");
            args.add(StringUtils.likeAllValue(buyerUsername));
        }
        if (StrKit.notBlank(buyerName)) {
            sql.append(" and b.name like ? ");
            args.add(StringUtils.likeAllValue(buyerName));
        }
        if (paymentFrom > 0) {
            sql.append(" and o.money >= ?");
            args.add(paymentFrom);
        }
        if (paymentTo > 0) {
            sql.append(" and o.money <= ?");
            args.add(paymentTo);
        }
        if (createdFrom != null) {
            sql.append(" and o.created >=?");
            args.add(createdFrom);
        }
        if (createdTo != null) {
            sql.append(" and o.created <=?");
            args.add(createdTo);
        }
        return findList(sql.toString(), args.toArray());
    }

    public Map<Integer, ActivityJobCustomerST> getActivityJobCustomerST(Collection<Integer> customerIds, Date from,
                                                                        Date end) {
        if (customerIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Object> args = new ArrayList<>();
        args.add(FINISHED.getVal());
        args.addAll(customerIds);
        args.add(from);
        args.add(end);
        String sql = "select buyerId, sum(money) money, count(1) num from orders where status=?" +
                " and " + inSql("buyerId", customerIds.size()) +
                " and created >=? and created <?" +
                " group by buyerId";

        List<ActivityJobCustomerST> list = findSQL(sql, ActivityJobCustomerST.class, args.toArray());

        return Maps.uniqueIndex(list, o -> o.getBuyerId());
    }

    /**
     * 查找拣货单
     * @param code
     * @return
     */
    public List<Order> findPickOrderCode(String code) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from orders o where 1=1");
        if (StrKit.notBlank(code)) {
            sql.append(" and o.pickOrderCode = ?");
            params.add(code);
        }
        return findSQL(sql.toString(), params.toArray());
    }

    /**
     * 获取指定供货商的结算金额
     * @param sellerId      供货商ID
     * @param startTime     结算开始时间
     * @param endTime       结算结束时间
     * @return
     */
    public Map<String, Object> getSettlementInfo(int sellerId, Date startTime, Date endTime){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();

        sql.append("select sum(sale) saleMoney, sum(back) backMoney from (");
        //获取销售额
        sql.append("select o.money sale, 0 back from orders o join trade t on o.tradeId = t.id")
                .append(" where o.sellerId = ? ")
                .append(" and t.consignTime >= ? ")
                .append(" and t.consignTime < ? ")
                .append(" and o.`status` >= ? ");
        paras.add(sellerId);
        paras.add(startTime);
        paras.add(endTime);
        paras.add(OrderStatus.WAIT_RECEIVE.getVal());

        //获取取消订单退款、退货退款额
        sql.append(" union all ")
                .append(" select 0 sale, ")
                .append(" supplierMoney back ")
                .append(" from orders ")
                .append(" where sellerId = ? ")
                .append(" and refundConfirmTime >= ? ")
                .append(" and refundConfirmTime < ? ")
                .append(" and status >= ? ");
        paras.add(sellerId);
        paras.add(startTime);
        paras.add(endTime);
        paras.add(OrderStatus.WAIT_RECEIVE.getVal());

        sql.append(") a ");

        return find(sql.toString(), paras.toArray());
    }

    /**
     * 获取退款订单信息
     * @param supplierId    供应商ID
     * @param startTime     结算开始时间
     * @param endTime       结算结束时间
     * @return
     */
    public List<Order> findRefundOrders(int supplierId, Date startTime, Date endTime) {
        if (endTime == null || endTime == null) {
            return Collections.emptyList();
        }
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        //获取取消订单退款额、退货退款额
        sql.append(" select * from orders ")
                .append(" where sellerId = ? ")
                .append(" and refundConfirmTime >= ? ")
                .append(" and refundConfirmTime < ? ")
                .append(" and status > ? ");
        paras.add(supplierId);
        paras.add(startTime);
        paras.add(endTime);
        paras.add(OrderStatus.WAIT_PICKING.getVal());

        return findSQL(sql.toString(), paras.toArray());
    }

    /**
     * 退款处理
     */
    public PageList<Order> listByRefund(RefundParam refundParam, Collection<OrderStatus> statusList, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> strs = new ArrayList<>();
        sql.append("select o.* from `orders` o")
                .append(" left join customer c on o.buyerId=c.id")
                .append(" left join trade t on o.tradeId=t.id where 1=1");

        if (statusList != null && !statusList.isEmpty()) {
            sql.append(" and ").append(inSql("o.status", statusList.size()));
            List<Object> statusValues = new ArrayList<>();
            for (OrderStatus s : statusList) {
                statusValues.add(s.getVal());
            }
            strs.addAll(statusValues);
        }

        if (StrKit.notBlank(refundParam.getNameParam())) {
            sql.append(" and c.`name` = ?");
            strs.add(refundParam.getNameParam());
        }
        if (StrKit.notBlank(refundParam.getCodeParam())) {
            sql.append(" and o.`code` = ?");
            strs.add(refundParam.getCodeParam());
        }
        if (refundParam.getStartTimeParam() != null && refundParam.getEndTimeParam() != null) {
            sql.append(" and o.applyRefundTime >= ?");
            strs.add(refundParam.getStartTimeParam());
            sql.append(" and o.applyRefundTime <= ?");
            strs.add(refundParam.getEndTimeParam());
        }
        if (refundParam.getStatusParam() != null) {
            sql.append(" and o.`status` = ?");
            strs.add(refundParam.getStatusParam().getVal());
        }
        if (StrKit.notBlank(refundParam.getPaidAlpayCodeParam())) {
            sql.append(" and t.paidAlipayCode = ?");
            strs.add(refundParam.getPaidAlpayCodeParam());
        }
        if (refundParam.getExpressIdParam() != null && refundParam.getExpressIdParam() != 0) {
            sql.append(" and t.expressId = ?");
            strs.add(refundParam.getExpressIdParam());
        }
        if (StrKit.notBlank(refundParam.getExpressCodeParam())) {
            sql.append(" and t.expressCode = ?");
            strs.add(refundParam.getExpressCodeParam());
        }

        pageParam.putSort("o.created", "desc");
        return findPaging(sql, pageParam, strs);
    }

    /**
     * @author he daoyuan
     * @param setOrd
     * @return Map<String, Object>
     */
    public Map<String, Object> getSettlementOrder(SettlementOrder setOrd){
        StringBuilder sql = new StringBuilder();
        // 统计订单销售额
        sql.append("select sum(sale) saleMoney, sum(back) backMoney from (select o.money sale, 0 back from orders o join trade t on o.tradeId = t.id where");
        List<Object> params = new ArrayList<>();
        sql.append(" o.sellerId = ?");
        params.add(setOrd.getSupplierId());
        if (setOrd.getStartTime() != null && setOrd.getEndTime() != null) {
            sql.append(" and t.consignTime >= ?");
            params.add(setOrd.getStartTime());
            sql.append(" and t.consignTime <= ?");
            params.add(setOrd.getEndTime());
        }
        sql.append(" and o.`status` >= ").append(OrderStatus.WAIT_RECEIVE.getVal());

        // 统计订单退款额
        sql.append(" union all select 0 sale, supplierMoney back from orders where");
        sql.append(" sellerId = ?");
        params.add(setOrd.getSupplierId());
        if (setOrd.getStartTime() != null && setOrd.getEndTime() != null) {
            sql.append(" and refundConfirmTime >= ?");
            params.add(setOrd.getStartTime());
            sql.append(" and refundConfirmTime < ?");
            params.add(setOrd.getEndTime());
        }
        sql.append(" and status >= " + OrderStatus.WAIT_RECEIVE.getVal() + ") as a");

        return find(sql.toString(), params.toArray());
    }

    /**
     * 获取商品数量
     * @param tradeId
     * @return
     */
    public Long getGoodsNumByTradeId(Integer tradeId){
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(quantity) quantity from orders ")
                .append(" where tradeId = ? ");
        BigDecimal quantity = getSingle(sql.toString(), tradeId);
        return (quantity == null ? 0 : quantity.longValue());
    }

    /**
     * 收集进货商的退款退货相关的订单(包含退款退货信息查询功能)  16-11-29 增 he
     * @param order
     * @param queryTime
     * @param refundFlag
     * @param pageParam
     * @return
     */
    public PageList<Order> gatherBuyerDispute(Order order, QueryTimeParam queryTime, List<OrderStatus> refundFlag, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from orders where 1 = 1");

        List<Object> params = new ArrayList<>();
        if (!StrKit.isBlank(order.getCode())) {
            sql.append(" and code = ?");
            params.add(order.getCode());
        }
        if (order.getBuyerId() > 0) {
            sql.append(" and buyerId = ?");
            params.add(order.getBuyerId());
        }
        if (order.getTradeId() > 0) {
            sql.append(" and tradeId = ?");
            params.add(order.getTradeId());
        }
        if (order.getStatus() != null) {
            sql.append(" and status = ?");
            params.add(order.getStatus().getVal());
        }
        if (refundFlag != null && !refundFlag.isEmpty()) {
            sql.append(" and ").append(inSql("applyRefundFlag", refundFlag.size()));
            params.addAll(SqlHelper.fieldInGroup(refundFlag));
        }
        if (queryTime.getStartCreated() != null && queryTime.getEndCreated() != null) {
            sql.append(" and created >= ?");
            params.add(queryTime.getStartCreated());
            sql.append(" and created <= ?");
            params.add(queryTime.getEndCreated());
        }
        if (queryTime.getStartRefund() != null && queryTime.getEndRefund() != null) {
            sql.append(" and applyRefundTime >= ?");
            params.add(queryTime.getStartRefund());
            sql.append(" and applyRefundTime <= ?");
            params.add(queryTime.getEndRefund());
        }

        pageParam.putSort("code", "desc");
        return findPaging(sql, pageParam, params);
    }

    /**
     * 获取指定客户第一次采购时间
     * @param buyerId   客户ID
     * @return 第一次采购时间
     */
    public Date getFirstBuyTime(int buyerId){
        StringBuilder sql = new StringBuilder();
        sql.append("select min(payTime) from orders ")
                .append(" where buyerId = ? ")
                .append(" and status >= ? ");
        return getSingle(sql.toString(), buyerId, OrderStatus.WAIT_PICKING.getVal());
    }
}
