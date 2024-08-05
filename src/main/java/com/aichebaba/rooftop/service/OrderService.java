package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.OrderDao;
import com.aichebaba.rooftop.dao.SendOrderDao;
import com.aichebaba.rooftop.dao.TradeDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.model.search.QueryTimeParam;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.aichebaba.rooftop.vo.ActivityJobCustomerST;
import com.google.common.collect.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import static com.aichebaba.rooftop.model.Order.tradeIdValue;
import static com.aichebaba.rooftop.model.enums.OrderStatus.*;
import static com.aichebaba.rooftop.utils.DateUtil.getDayEnd;
import static com.aichebaba.rooftop.utils.DateUtil.getDayStart;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Service
public class OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Value("${sms.providerStockingMsg}")
    private String providerStockingMsg;

    @Value("${order.auto.finish.hours}")
    private int finishedHours;

    @Autowired
    private TradeDao tradeDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private SendOrderDao sendOrderDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private CouponService couponService;

    /**
     * 分页查询订单信息
     *
     * @param code        订单编号
     * @param goodsCode   商品编号
     * @param goodsName   商品名称
     * @param sellerId    供货商ID
     * @param buyerId     进货商ID
     * @param goodsItemNo 商品货号
     * @param sellerPhone 供货商电话
     * @param buyerPhone  进货商电话
     * @param status      订单状态
     * @param createdFrom 创建时间（开始）
     * @param createdEnd  创建时间（结束）
     * @param pageParam   分页设置
     * @return
     */
    public PageList<Order> findOrders(String code, String goodsCode, String goodsName, int sellerId, int buyerId, String goodsItemNo,
                                      String sellerPhone, String buyerPhone, OrderStatus status, Date createdFrom, Date createdEnd,
                                      PageParam pageParam) {
        PageList<Order> orderPageList = orderDao.findOrders(code, goodsCode, goodsName, sellerId, buyerId, goodsItemNo, sellerPhone, buyerPhone, status, createdFrom, createdEnd, pageParam);
        for(Order order : orderPageList.getData()){
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            if(goods != null) {
                order.setHeadImg(goods.getHeadImgUrl1());
            }
        }
        return orderPageList;
    }

    public PageList<Order> findOrders(String code, String goodsCode, int sellerId, String goodsItemNo,
                                      String sellerPhone, String buyerPhone, OrderStatus status, PageParam pageParam) {
        PageList<Order> orderPageList = orderDao.findOrders(code, goodsCode, null, sellerId, 0, goodsItemNo, sellerPhone, buyerPhone, status, null, null, pageParam);
        for(Order order : orderPageList.getData()){
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            if(goods != null) {
                order.setHeadImg(goods.getHeadImgUrl1());
            }
        }
        return orderPageList;
    }

    /**
     * 分页查询订单信息(进货商/供货商订单列表用)
     * @param params            参数如下
     *        brand             品牌名      模糊查询
     *        tradeCode         总订单号    模糊查询
     *        orderCode         子订单号    模糊查询
     *        purchaseName      进货商名    模糊查询
     *        supplierName      供货商名    模糊查询
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
    public PageList<Order> findOrderList(Map<String, Object> params, PageParam pageParam){

        int tradeId = 0;
        if(StrKit.notBlank((String) params.get("tradeCode"))){
            String tmp = (String) params.get("tradeCode");
            tmp = tmp.toUpperCase().replace(Trade.mark, "");
            try{
              tradeId = Integer.parseInt(tmp);
            } catch (Exception ex){
                tradeId = -1;
            }
        }
        params.put("tradeId", tradeId);
        return orderDao.findOrderList(params, pageParam);

    }

    public PageList<Order> findPickingOrders(Date createdFrom, Date createdEnd, Collection<OrderStatus> statuses,
                                             String code, String goodsCode, String buyerPhone, String pickUser,
                                             String goodsItemNo, String sellerPhone, String sellerAddress,
                                             Boolean pickingCancel,
                                             PageParam pageParam) {
        return orderDao.findPickingOrders(createdFrom, createdEnd, statuses, code, goodsCode, buyerPhone, pickUser,
                goodsItemNo, sellerPhone, sellerAddress, pickingCancel, pageParam);
    }

    /**
     * 获取待拣货的子订单
     * @param createdFrom   开始时间
     * @param createdEnd    结束时间
     * @param brand         品牌名
     * @param goodsItemNo   商品货号
     * @param goodsName     商品名称
     * @param tradeCode     总订单编号
     * @param orderCode     子订单编号
     * @param buyerName     买家名称
     * @param sellerName    卖家名称
     * @param expressId     快递公司ID
     * @return
     */
    public PageList<Order> findWaitPickOrders(Date createdFrom, Date createdEnd, String brand, String goodsItemNo,
                                             String goodsName, String tradeCode, String orderCode, String buyerName,
                                             String sellerName, int expressId, PageParam pageParam) {
        int tradeId = 0;
        if(StrKit.notBlank(tradeCode)){
            tradeId = Trade.code2id(tradeCode);
        }
        return orderDao.findWaitPickOrders(createdFrom, createdEnd, brand, goodsItemNo, goodsName, tradeId, orderCode,
                buyerName, sellerName, expressId, OrderStatus.WAIT_PICKING, pageParam);
    }

    /**
     * 获取待拣货的总订单
     * @param createdFrom   开始时间
     * @param createdEnd    结束时间
     * @param brand         品牌名
     * @param goodsItemNo   商品货号
     * @param goodsName     商品名称
     * @param tradeCode     总订单编号
     * @param orderCode     子订单编号
     * @param buyerName     买家名称
     * @param sellerName    卖家名称
     * @param expressId     快递公司ID
     * @param pageParam
     * @return
     */
    public PageList<Trade> findWaitPickTrades(Date createdFrom, Date createdEnd, String brand, String goodsItemNo,
                                              String goodsName, String tradeCode, String orderCode, String buyerName,
                                              String sellerName, int expressId, PageParam pageParam) {
        int tradeId = 0;
        if(StrKit.notBlank(tradeCode)){
            tradeId = Trade.code2id(tradeCode);
        }
        return tradeDao.findWaitPickOrders(createdFrom, createdEnd, brand, goodsItemNo, goodsName, tradeId, orderCode,
                buyerName, sellerName, expressId, OrderStatus.WAIT_PICKING, pageParam);
    }

    /**
     * 获取待发货的总订单
     * @param tradeCode
     * @param orderCode
     * @param pageParam
     * @return
     */
    public PageList<Trade> findUnPickingTrades(String tradeCode, String orderCode, PageParam pageParam) {
        int tradeId = 0;
        if(StrKit.notBlank(tradeCode)){
            tradeId = Trade.code2id(tradeCode);
        }
        return tradeDao.findWaitSendTrades(tradeId, orderCode, pageParam);
    }

    public List<Order> findPickingOrders(Date createdFrom, Date createdEnd, Collection<OrderStatus> statuses,
                                             String code, String goodsCode, String buyerPhone, String pickUser,
                                             String goodsItemNo, String sellerPhone, String sellerAddress,
                                             Boolean pickingCancel) {
        return orderDao.findPickingOrders(createdFrom, createdEnd, statuses, code, goodsCode, buyerPhone, pickUser,
                goodsItemNo, sellerPhone, sellerAddress, pickingCancel);
    }

    public PageList<Order> findPickedOrders(String pickerName, String code, String goodsCode,
                                            String goodsItemNo, String sellerAddress, String sellerPhone, String buyerPhone, Date createdFrom, Date createdEnd,
                                            PageParam pageParam) {
        return orderDao.findPickedOrders(pickerName, code, goodsCode, goodsItemNo, sellerAddress, sellerPhone, buyerPhone, createdFrom, createdEnd, pageParam);
    }

    public PageList<Order> findOrders(String code, String goodsCode, int sellerId, String goodsItemNo,
                                      String sellerPhone, String buyerPhone, List<OrderStatus> statusList, PageParam pageParam) {
        PageList<Order> orderPageList = orderDao.findOrders(code, goodsCode, null, sellerId, 0, goodsItemNo, sellerPhone, buyerPhone, statusList, null, null, pageParam);
        for(Order order : orderPageList.getData()){
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            if(goods != null) {
                order.setHeadImg(goods.getHeadImgUrl1());
            }
        }
        return orderPageList;
    }

    public List<Order> getOrdersByTradeAndCreatedRange(int tradeId, Date createdFrom, Date createdEnd) {
        return orderDao.getOrdersByTradeAndCreatedRange(tradeId, createdFrom, createdEnd);
    }

    public List<Order> getOrderByCodes(Collection<String> codes) {
        return orderDao.getOrderByCodes(codes);
    }

    public List<Order> getOrdersByTradeId(int tradeId) {
        return orderDao.getOrdersByTradeId(tradeId);
    }

    public List<Order> getOrdersByTradeId(int tradeId, OrderStatus status) {
        return orderDao.getOrdersByTradeId(tradeId, status);
    }

    public List<Order> getOrdersBySendOrderId(int sendOrderId) {
        return orderDao.getOrdersBySendOrderId(sendOrderId);
    }

    public Order getOrderByCode(String code) {
        return orderDao.getByPK(code);
    }

    public Order getOrderInfoByCode(String code) {
        return orderDao.getOrderInfoByCode(code);
    }

    public SendOrder getSendOrderById(int id) {
        return sendOrderDao.getById(id);
    }

    public SendOrder getSendOrderByTradeId(int tradeId) {
        return sendOrderDao.getSendOrderByTradeId(tradeId);
    }

    public void dealRefundApply(Order order, int curUserId) {
        orderDao.update(order);
        logger.info("deal refund apply : code-{}, refundDeal-{}, disagreeReason-{}, negotiationResult-{}," + " negotiationrefundMoney-{}, refundUserId-{}", order.getCode(), order.getRefundDeal(), order.getDisagreeRefundReason(), order.getNegotiationResult(), order.getNegotiationRefundMoney(), order.getRefundUserId());
    }

    public List<Order> getOrdersByTradeIds(Collection<Integer> tradeIds){
        return orderDao.getOrdersByTradeIds(tradeIds);
    }

    public boolean addTrade(Trade trade, List<Order> orders) throws SQLException {
        Object tradeId = tradeDao.add(trade);
        Integer id = Integer.valueOf(tradeId.toString());
        trade.setId(id);
        Customer buyer = customerService.getById(trade.getBuyerId());
        Integer sendOrderId = addSendOrder(trade);
        for (Order order : orders) {
            order.setTradeId(id);
            //废掉地主功能
//            if (area != null) {
//                order.setPickerId(area.getPickUserId());
//            }
            order.setSendOrderId(sendOrderId);
            order.setCreated(trade.getCreated());
            order.setCode(orderDao.getNextCode());
            // 锁库存 付款实际减库存
            if (!skuService.lockStock(order.getSkuId(), order.getQuantity())) {
                return false;
            }
        }
        orderDao.adds(orders);
        return true;
    }

    private Integer addSendOrder(Trade trade) {
        SendOrder sendOrder = new SendOrder();
        sendOrder.setCustomerId(trade.getBuyerId());
        sendOrder.setBuyerAddress(trade.getReceiveAddress());
        sendOrder.setBuyerName(trade.getReceiveName());
        sendOrder.setProvinceId(trade.getProvinceId());
        sendOrder.setCityId(trade.getCityId());
        sendOrder.setCountyId(trade.getCountyId());
        sendOrder.setBuyerPhone(trade.getReceivePhone());
        sendOrder.setBuyerPostCode(trade.getZip());
        sendOrder.setOrderCnt(trade.getOrderCnt());
        sendOrder.setWeight(trade.getWeight());
        sendOrder.setFreight(trade.getPostFee());
        sendOrder.setTradeId(trade.getId());
        sendOrder.setStatus(SendOrderStatus.PICKING);
        sendOrder.setExpressId(trade.getExpressId());
        sendOrder.setExpressName(trade.getExpressName());
        sendOrder = sendOrderDao.save(sendOrder);
        return sendOrder.getId();
    }

    public Trade getTrade(int tradeId) {
        return tradeDao.getById(tradeId);
    }

    /**
     * 退货申请
     *
     * @param code   订单编号
     * @param reason 退货理由
     */
    public void refundApplication(String code, String reason) {
        Order order = orderDao.getByPK(code);
        order.setStatus(OrderStatus.APPLY_REFUND_GOODS);
        order.setRefundDeal("");
        order.setDisagreeRefundReason("");
        order.setApplyRefundTime(new Date());
        order.setRefundReason(reason);
        update(order);
    }

    public void applyRefund(String code, long refundFee, String reason) {
        Order order = orderDao.getByPK(code);
        order.setStatus(OrderStatus.APPLY_REFUND);
        order.setRefundDeal("");
        order.setDisagreeRefundReason("");
        order.setApplyRefundTime(new Date());
        order.setRefundFee(refundFee);
        order.setRefundReason(reason);
        update(order);
    }

    /**
     * @创建时间： 2016年4月3日
     * @相关参数： @param Trade 订单
     * @相关参数： @param moneyRefundReason 退款理由
     * @功能描述： 退款保存
     */
    public void applyCancelTrade(Trade trade, List<Order> orders, String cancelReason) {
        trade.setStatus(WAIT_AGREE_CANCEL);
        tradeDao.update(trade);
        for (Order order : orders) {
            if (order.getStatus().equals(PICKING)) {
                order.setPicking(true);
            }
            order.setStatus(WAIT_AGREE_CANCEL);
            order.setApplyRefundTime(new Date());
            //最大退款金额=总金额-订单中优惠的金额*（需退款商品原价/订单原价）
            if (trade.getCouponId() > 0 && trade.getUseType().equals(UseType.PAYMENT)) {
                order.setRefundFee(order.getPayment() - (trade.getCouponFee() * order.getMoney() / trade.getTotalPayment()));
            } else {
                order.setRefundFee(order.getPayment());
            }
            order.setRefundDeal("");
            order.setDisagreeRefundReason("");
            order.setRefundReason(cancelReason);
            update(order);
        }
        Order order = orders.get(orders.size() - 1);
        if (trade.getCouponId() > 0 && trade.getUseType().equals(UseType.POST_FEE)) {
            order.setRefundFee(order.getPayment() + trade.getPostFee() - trade.getCouponFee());
        }else{
            order.setRefundFee(order.getPayment() + trade.getPostFee());
        }
        update(order);
    }

    public void update(Order order) {
        orderDao.update(order);
    }

    public void paid(int tradeId, String alipayNo, String paidAlipayCode) {
        Trade trade = getTrade(tradeId);
        List<Order> orders = orderDao.getOrdersByTradeId(tradeId);
        trade.setStatus(OrderStatus.WAIT_PICKING);
        trade.setAlipayNo(alipayNo);
        Date payTime = new Date();
        for (Order order : orders) {
            order.setStatus(OrderStatus.WAIT_PICKING);
            order.setPayTime(payTime);
            order.setPayment(order.getMoney());
            orderDao.update(order);
        }
        trade.setPayTime(payTime);
        trade.setPayment(trade.getTotalPayment() + trade.getPostFee() - trade.getCouponFee());
        trade.setPaidAlipayCode(paidAlipayCode);
        tradeDao.update(trade);
        //更新采购累计金额及订单数
        customerService.addUpPurchase(trade.getBuyerId(), trade.getTotalPayment(), trade.getOrderCnt());
        //减去锁住的数量
        for (Order order : orders) {
            goodsService.minusStock(order.getGoodsCode(), order.getQuantity());
            skuService.minusStock(order.getSkuId(), order.getQuantity());
        }
        //发送通知供货商短信
        for (Order order : orders) {
            Customer seller = customerService.getById(order.getSellerId());
            String msg = TemplateUtils.parseText(providerStockingMsg, ImmutableMap.<String, Object>of("name", seller.getName(), "order", order, "tradeCode", trade.getCode()));
            SMSUtil.sendSmsMsg2(seller.getPhone(), msg);
            try {
                Thread.sleep(10);
            }catch (InterruptedException ex){}
        }
    }

    public PageList<Trade> findTrades(String buyerName, Collection<OrderStatus> statuses, PageParam pageParam) {
        return tradeDao.findTrades(buyerName, statuses, pageParam);
    }

    public List<Trade> getTradesByPickOrderId(int pickOrderId) {
        return tradeDao.getTradesByPickOrderId(pickOrderId);
    }

    public List<Trade> getTradesByIds(Collection<Integer> ids) {
        return tradeDao.getTradesByIds(ids);
    }

    public void updateTradeStatus(List<Trade> trades) {
        for (Trade trade : trades) {
            tradeDao.update(trade);
        }
    }

    public void updateTrade(Trade trade) {
        tradeDao.update(trade);
    }

    public void updateOrdersStatus(List<Order> orders) {
        for (Order o : orders) {
            orderDao.update(o);
        }
    }

    public void picked(String code, User curUser) {
        Order order = orderDao.getByPK(code);
        if (order != null && order.getStatus().equals(OrderStatus.PICKING)) {
            order.setStatus(OrderStatus.PICK_FINISHED);
            order.setPickTime(new Date());
            orderDao.update(order);
            deliverService.updateSendOrderPickStatus(order.getSendOrderId());
        }
    }

    public void finishedTrades() {
        Date endTime = LocalDateTime.now().minusHours(finishedHours).toDate();
        logger.debug("{} finished trade start ...", endTime);

        List<Trade> trades = tradeDao.findWaitFinishedByBeforeTime(endTime);

        Collection<Integer> tradeIds = Collections2.transform(trades, Trade.ID_VALUE);

        Multimap<Integer, Order> orderMap = orderDao.findOrdersByTradeIds(tradeIds);
        List<Integer> finishedIds = new ArrayList<>();
        for (Trade trade : trades) {
            Collection<Order> orders = orderMap.get(trade.getId());
//            Collection<Integer> buyerIds = Collections2.transform(orders, Order.buyerIdValue);
//            Map<Integer, Customer> idBuyers = customerService.getCustomersByIds(buyerIds);
            boolean canFinished = true;
            for (Order o : orders) {
                if (CLOSED_BY_CANCEL.equals(o.getStatus()) || CLOSED_BY_REFUND_GOODS.equals(o.getStatus())
                        || CLOSED_BY_REFUND.equals(o.getStatus())) {
                    continue;
                }
                if (!o.getStatus().equals(WAIT_RECEIVE) && !o.getStatus().equals(REFUND_GOODS_REFUSE)
                        && !o.getStatus().equals(REFUND_REFUSE)) {
                    canFinished = false;
                    break;
                }
            }
            if (canFinished) {
                finishedIds.add(trade.getId());
                trade.setStatus(OrderStatus.FINISHED);
                tradeDao.update(trade);
                for (Order o : orders) {
                    if (CLOSED_BY_CANCEL.equals(o.getStatus()) || CLOSED_BY_REFUND_GOODS.equals(o.getStatus())
                            || CLOSED_BY_REFUND.equals(o.getStatus())) {
                        continue;
                    }
//                    Customer buyer = idBuyers.get(o.getBuyerId());

//                    buyer.setTotalPurchaseMoney(buyer.getTotalPurchaseMoney() + o.getMoney());
//                    buyer.setTotalPurchaseCount(buyer.getTotalPurchaseCount() + 1);
//                    buyer.setLastPurchaseTime(o.getCreated());
//                    customerService.save(buyer);
//                    customerService.upLevel(buyer);

                    o.setStatus(OrderStatus.FINISHED);
                    o.setRefundFee(0);
                    o.setEndTime(new Date());
                    orderDao.update(o);
                }
            }
        }

        logger.debug("finished trade end: {}", finishedIds.size());
    }

    public void finishedOrders() {

        Date endTime = LocalDateTime.now().minusHours(finishedHours).toDate();
        logger.debug("{} finished order start ...", endTime);

        List<Order> orders = orderDao.getWaitReceiveOrders(endTime);
        Collection<Integer> tradeIds = Collections2.transform(orders, Order.tradeIdValue);
        List<Trade> trades = tradeDao.getTradesByIds(tradeIds);
        Map<Integer, Trade> idTrades = Maps.uniqueIndex(trades, Trade.ID_VALUE);

//        Collection<Integer> buyerIds = Collections2.transform(orders, Order.buyerIdValue);
//        Map<Integer, Customer> idBuyers = customerService.getCustomersByIds(buyerIds);
        int cnt = 0;
        for (Order order : orders) {
            Trade trade = idTrades.get(order.getTradeId());
            if (trade.getStatus().equals(OrderStatus.FINISHED)) {
//                Customer buyer = idBuyers.get(order.getBuyerId());

                //累计进货商的历史进货总额及下单数
//                buyer.setTotalPurchaseMoney(buyer.getTotalPurchaseMoney() + order.getMoney());
//                buyer.setTotalPurchaseCount(buyer.getTotalPurchaseCount() + 1);
//                buyer.setLastPurchaseTime(order.getCreated());
//                customerService.save(buyer);
//                customerService.upLevel(buyer);

                order.setStatus(OrderStatus.FINISHED);
                order.setEndTime(new Date());
                order.setRefundFee(0);
                orderDao.update(order);
                cnt++;
            }
        }

        logger.debug("finished order end: {}", cnt);
    }

    /**
     * 关闭订单
     * @param tradeId
     */
    public void closeTrade(int tradeId) {
        //关闭总订单
        Trade trade = tradeDao.getById(tradeId);
        trade.setStatus(OrderStatus.CLOSED);
        if (trade.getCouponId() > 0) {
            //如果该订单使用优惠券，则还原优惠券
            Coupon coupon = couponService.getCoupon(trade.getCouponId());
            coupon.setUsed(false);
            coupon.setUsedTime(new Date());
            couponService.saveCoupon(coupon);
        }
        tradeDao.update(trade);
        //关闭所有子订单
        List<Order> orders = orderDao.getOrdersByTradeId(tradeId);
        for(Order order : orders){
            order.setStatus(OrderStatus.CLOSED);
            orderDao.update(order);
        }
    }

    public PageList<Order> findOrders(String sellerCode, String sellerUsername, String sellerName,
                                      Collection<OrderStatus> statuses, BigDecimal paymentFrom, BigDecimal paymentTo,
                                      Date createdFrom, Date createdTo, PageParam pageParam) {
        return orderDao.findOrders(sellerCode, sellerUsername, sellerName, statuses,
                paymentFrom.multiply(BigDecimal.valueOf(100)).longValue(),
                paymentTo.multiply(BigDecimal.valueOf(100)).longValue(), createdFrom, getDayStart(addDays(createdTo, 1)), pageParam);
    }

    public List<Order> findOrders(String sellerCode, String sellerUsername, String sellerName,
                                      Collection<OrderStatus> statuses, BigDecimal paymentFrom, BigDecimal paymentTo,
                                      Date createdFrom, Date createdTo) {
        return orderDao.findOrders(sellerCode, sellerUsername, sellerName, 0, null , null, statuses,
                paymentFrom.multiply(BigDecimal.valueOf(100)).longValue(),
                paymentTo.multiply(BigDecimal.valueOf(100)).longValue(), createdFrom, getDayStart(addDays(createdTo, 1)));
    }

    /**
     * 获取订单列表
     * @param buyerId           进货商ID
     * @param buyerUsername     进货商账号
     * @param buyerName         进货商名称
     * @param statuses          订单状态
     * @param createdFrom       创建时间-开始
     * @param createdTo         创建时间-结束
     * @return
     */
    public List<Order> findOrders(int buyerId, String buyerUsername, String buyerName,
                                  Collection<OrderStatus> statuses,
                                  Date createdFrom, Date createdTo) {
        return orderDao.findOrders(null,null, null, buyerId, buyerUsername, buyerName,  statuses,
                0, 0, createdFrom, getDayEnd(createdTo));
    }

    public Map<Integer, ActivityJobCustomerST> getActivityJobCustomerST(Collection<Integer> customerIds, Date from,
                                                                        Date end) {
        return orderDao.getActivityJobCustomerST(customerIds, from, end);
    }

    public List<Order> findPickOrderCode(String code) {
        return orderDao.findPickOrderCode(code);
    }

    public PageList<Order> listByRefund(RefundParam refundParam, Collection<OrderStatus> statusList, PageParam pageParam) {
        return orderDao.listByRefund(refundParam, statusList, pageParam);
    }

    public Long getGoodsNumByTradeId(Integer tradeId){
        return orderDao.getGoodsNumByTradeId(tradeId);
    }

    public PageList<Order> gatherBuyerDispute(Order order, QueryTimeParam queryTime, List<OrderStatus> refundFlag, PageParam pageParam) {
        return orderDao.gatherBuyerDispute(order, queryTime, refundFlag, pageParam);
    }

    /**
     * 计算运费
     * @param orders
     */
    public void calcPostFee(Collection<Order> orders) {
        Map<String, Order> codeOrders = Maps.uniqueIndex(orders, Order.codeValue);
        Multimap<Integer, Order> tradeOrders = Multimaps.index(orders, tradeIdValue);
        List<Trade> trades = getTradesByIds(tradeOrders.keySet());
        for (Trade trade : trades) {
            //获取指定总订单下的子订单
            Collection<Order> tmpOrders = tradeOrders.get(trade.getId());
            //如果对象子订单没有包含指定总订单下的所有子订单，则获取重新获取总订单下的所有子订单
            if (trade.getOrderCnt() != tmpOrders.size()) {
                tmpOrders = getOrdersByTradeId(trade.getId());
            }

            if (tmpOrders.size() == 1) {
                //如果子订单数只有一条，则该子订单运费=总订单运费
                Order o = Iterables.get(tmpOrders, 0);
                o.setPostFee(trade.getPostFee());
            } else {

                //如果子订单数大于一条，则该子订单运费按商品金额比例分配
                int totalW = 0;
                for (Order order : tmpOrders) {
                    totalW += order.getWeight() * order.getQuantity();
                }
                long totalPostFee = 0;
                for (int i = 0; i < tmpOrders.size(); i++) {
                    Order order = Iterables.get(tmpOrders, i);
                    long postFee = 0;
                    if (i < tmpOrders.size() - 1) {
                        postFee = Math.round(order.getWeight() * order.getQuantity() / (totalW * 1.0) * trade.getPostFee());
                        totalPostFee += postFee;
                    } else {
                        postFee = trade.getPostFee() - totalPostFee;
                    }
                    if (codeOrders.containsKey(order.getCode())) {
                        codeOrders.get(order.getCode()).setPostFee(postFee);
                    }
                }
            }
        }
    }

    /**
     * 获取进货商第一次采购时间
     */
    public Date getFirstBuyTime(int buyerId){
        return orderDao.getFirstBuyTime(buyerId);
    }


    public List<Order> getTradingRecord(String code) {
        return orderDao.findByWhere("goodsCode = ? and `status` > 30 order by payTime desc", code);
    }
}
