package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.vo.PurchaseStatistics;
import com.aichebaba.rooftop.vo.PurchaseStatistics30;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.aichebaba.rooftop.model.Order.tradeIdValue;


@Controller
@Scope("prototype")
public class StatisticsPurchaseController extends BaseController {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StatisticsPurchaseController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 进货商统计
     */
    public void index(){
        String purchaseCode = getPara("purchaseCode");          //进货商账号
        String purchaseName = getPara("purchaseName");          //进货商名称
        Long minMoney = getParaToLong("minMoney", 0l);          //总商品金额开始
        Long maxMoney = getParaToLong("maxMoney", 0l);          //总商品金额结束
        Date startTime = getParaToDate("startTime", null);      //下单时间-开始
        Date endTime = getParaToDate("endTime", null);          //下单时间-结束

        List<OrderStatus> statuses = new ArrayList<>();
        statuses.addAll(OrderStatus.DEALINGS);
        statuses.addAll(OrderStatus.REFUNDS);
        statuses.add(OrderStatus.FINISHED);

        List<Order> orders = new ArrayList<>();

        //获取范围内订单
        if(startTime != null) {
            orders = orderService.findOrders(0, purchaseCode, purchaseName, statuses, startTime, endTime);
        }
        //计算运费
        orderService.calcPostFee(orders);
        Map<Integer, PurchaseStatistics> statisticsMap = new HashMap<>();
        statistics(statisticsMap, orders);
        List<PurchaseStatistics> statisticses = new ArrayList<>();
        for(PurchaseStatistics statistics : statisticsMap.values()){
            if(statistics.getTotalGoodsMoney() >= minMoney *100 && (maxMoney == 0 || (maxMoney != 0 && statistics.getTotalGoodsMoney() <= maxMoney * 100))){
                statisticses.add(statistics);
            }
        }
        setAttr("statistics", statisticses);

        render("list.html");
    }

    /**
     * 按订单统计
     * @param statisticsMap
     * @param orders
     */
    protected void statistics(Map<Integer, PurchaseStatistics> statisticsMap, Collection<Order> orders) {
        //按子订单统计
        for (Order order : orders) {
            //获取进货商
            PurchaseStatistics stc = getStatistics(statisticsMap, order);
            //统计数据
            calcOrder(order, stc);
        }

        //按总订单统计
        Collection<Integer> tradeIds = Collections2.transform(orders, Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(tradeIds);
        for(Trade trade : trades){
            //获取进货商
            PurchaseStatistics stc = getStatistics(statisticsMap, trade);
            //统计数据
            calcTrade(trade, stc);
        }

    }

    /**
     * 按订单统计
     * @param statisticsMap
     * @param orders
     */
    protected void statistics(Map<Integer, PurchaseStatistics30> statisticsMap, Collection<Order> orders, Date endTime) {
        //按子订单统计
        for (Order order : orders) {
            //获取进货商
            PurchaseStatistics30 stc = getStatistics30(statisticsMap, order);
            //统计数据
            calcOrder30(order, stc, endTime);
        }

        //按总订单统计
        Collection<Integer> tradeIds = Collections2.transform(orders, Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(tradeIds);
        for(Trade trade : trades){
            //获取进货商
            PurchaseStatistics30 stc = getStatistics30(statisticsMap, trade);
            //统计数据
            calcOrder30(trade, stc, endTime);
        }
    }

    /**
     * 获取进货商
     * @param statisticsMap
     * @param order
     * @return
     */
    private PurchaseStatistics getStatistics(Map<Integer, PurchaseStatistics> statisticsMap, Order order) {
        if (!statisticsMap.containsKey(order.getBuyerId())) {
            //设置进货商信息
            PurchaseStatistics ps = new PurchaseStatistics();
            ps.setBuyerId(order.getBuyerId());
            ps.setBuyerName(order.getBuyerName());
            ps.setBuyerUsername(order.getBuyerUsername());
            Customer buyer = customerService.getById(order.getBuyerId());
            if(buyer != null) {
                ps.setHistoryTotalMoney(buyer.getTotalPurchaseMoney());
                ps.setHistoryTotalCnt(buyer.getTotalPurchaseCount());
                ps.setLastPurchaseTime(buyer.getLastPurchaseTime());
            }else{
                logger.error("找不到客户orderCode=" + order.getCode() + ",buyId=" + String.valueOf(order.getBuyerId()));
            }
            ps.setFirstPurchaseTime(orderService.getFirstBuyTime(order.getBuyerId()));

            statisticsMap.put(order.getBuyerId(), ps);
        }
        return statisticsMap.get(order.getBuyerId());
    }

    /**
     * 获取进货商
     * @param statisticsMap
     * @param trade
     * @return
     */
    private PurchaseStatistics getStatistics(Map<Integer, PurchaseStatistics> statisticsMap, Trade trade) {
        if (!statisticsMap.containsKey(trade.getBuyerId())) {
            //设置进货商信息
            PurchaseStatistics ps = new PurchaseStatistics();
            ps.setBuyerId(trade.getBuyerId());
            Customer buyer = customerService.getById(trade.getBuyerId());
            if(buyer != null) {
                ps.setBuyerName(buyer.getName());
                ps.setBuyerUsername(buyer.getUsername());
                ps.setHistoryTotalMoney(buyer.getTotalPurchaseMoney());
                ps.setHistoryTotalCnt(buyer.getTotalPurchaseCount());
                ps.setLastPurchaseTime(buyer.getLastPurchaseTime());
            }else{
                logger.error("找不到客户tradeCode=" + trade.getCode() + ",buyId=" + String.valueOf(trade.getBuyerId()));
            }
            ps.setFirstPurchaseTime(orderService.getFirstBuyTime(trade.getBuyerId()));

            statisticsMap.put(trade.getBuyerId(), ps);
        }
        return statisticsMap.get(trade.getBuyerId());
    }

    /**
     * 获取进货商(统计近30天+7天用)
     * @param statisticsMap
     * @param order
     * @return
     */
    private PurchaseStatistics30 getStatistics30(Map<Integer, PurchaseStatistics30> statisticsMap, Order order) {
        if (!statisticsMap.containsKey(order.getBuyerId())) {
            //设置进货商信息
            PurchaseStatistics30 ps = new PurchaseStatistics30();
            ps.setBuyerId(order.getBuyerId());
            ps.setBuyerName(order.getBuyerName());
            ps.setBuyerUsername(order.getBuyerUsername());

            statisticsMap.put(order.getBuyerId(), ps);
        }
        return statisticsMap.get(order.getBuyerId());
    }

    /**
     * 获取进货商(统计近30天+7天用)
     * @param statisticsMap
     * @param trade
     * @return
     */
    private PurchaseStatistics30 getStatistics30(Map<Integer, PurchaseStatistics30> statisticsMap, Trade trade) {
        if (!statisticsMap.containsKey(trade.getBuyerId())) {
            //设置进货商信息
            PurchaseStatistics30 ps = new PurchaseStatistics30();
            ps.setBuyerId(trade.getBuyerId());
            Customer buyer = customerService.getById(trade.getBuyerId());
            if(buyer != null) {
                ps.setBuyerName(buyer.getName());
                ps.setBuyerUsername(buyer.getUsername());
            }

            statisticsMap.put(trade.getBuyerId(), ps);
        }
        return statisticsMap.get(trade.getBuyerId());
    }

    /**
     * 按总订单统计数据
     * @param trade
     * @param stc
     */
    private void calcTrade(Trade trade, PurchaseStatistics stc) {
        //统计优惠券
        if (trade.getCouponId() > 0) {
            stc.setCouponMoney(stc.getCouponMoney() + trade.getCouponFee());
        }
        //统计打包袋
        stc.setPackFee(stc.getPackFee() + trade.getPackFee());
        //统计打包袋优惠金额
        stc.setPackSubsidy(stc.getPackSubsidy() + trade.getPackSubsidy());
    }

    /**
     * 统计数据
     * @param order
     * @param stc
     */
    private void calcOrder(Order order, PurchaseStatistics stc) {
        if (order.getGoodsItemNo().equals("NCP001")) {
            //统计空包
            stc.setNullOrderCnt(stc.getNullOrderCnt() + 1l);
            stc.setNullGoodsCnt(stc.getNullGoodsCnt() + order.getQuantity());
            stc.setNullGoodsMoney(stc.getNullGoodsMoney() + order.getMoney());
        } else {
            //统计实物
            stc.setRealOrderCnt(stc.getRealOrderCnt() + 1l);
            stc.setRealGoodsCnt(stc.getRealGoodsCnt() + order.getQuantity());
            stc.setRealGoodsMoney(stc.getRealGoodsMoney() + order.getMoney());

            //运费统计
            stc.setPostFee(stc.getPostFee() + order.getPostFee());
        }

        //退款/退货统计
        if (OrderStatus.isRefund2(order.getStatus())) {
            stc.setRefundMoney(stc.getRefundMoney() + order.getRefundFee());
        }
    }

    /**
     * 统计数据(近30天+7天用)
     * @param order
     * @param stc
     */
    private void calcOrder30(Order order, PurchaseStatistics30 stc, Date endTime) {

        /**
         * 统计近30天数据
         */
        if (order.getGoodsItemNo().equals("NCP001")) {
            //统计空包
            stc.setNullOrderCnt30(stc.getNullOrderCnt30() + 1l);
            stc.setNullGoodsCnt30(stc.getNullGoodsCnt30() + order.getQuantity());
            stc.setNullGoodsMoney30(stc.getNullGoodsMoney30() + order.getMoney());
        } else {
            //统计实物
            stc.setRealOrderCnt30(stc.getRealOrderCnt30() + 1l);
            stc.setRealGoodsCnt30(stc.getRealGoodsCnt30() + order.getQuantity());
            stc.setRealGoodsMoney30(stc.getRealGoodsMoney30() + order.getMoney());

            //运费统计
            stc.setPostFee30(stc.getPostFee30() + order.getPostFee());
        }

        //退款/退货统计
        if (OrderStatus.isRefund2(order.getStatus())) {
            stc.setRefundMoney30(stc.getRefundMoney30() + order.getRefundFee());
        }

        /**
         * 统计近7天数据
         */
        //获取7天前的开始日期
        Date startTime7 = DateUtil.getDayStart(DateUtil.dateAdd(endTime, -7));
        if(!order.getCreated().before(startTime7)) {
            if (order.getGoodsItemNo().equals("NCP001")) {
                //统计空包
                stc.setNullOrderCnt7(stc.getNullOrderCnt7() + 1l);
                stc.setNullGoodsCnt7(stc.getNullGoodsCnt7() + order.getQuantity());
                stc.setNullGoodsMoney7(stc.getNullGoodsMoney7() + order.getMoney());
            } else {
                //统计实物
                stc.setRealOrderCnt7(stc.getRealOrderCnt7() + 1l);
                stc.setRealGoodsCnt7(stc.getRealGoodsCnt7() + order.getQuantity());
                stc.setRealGoodsMoney7(stc.getRealGoodsMoney7() + order.getMoney());

                //运费统计
                stc.setPostFee7(stc.getPostFee7() + order.getPostFee());
            }

            //退款/退货统计
            if (OrderStatus.isRefund2(order.getStatus())) {
                stc.setRefundMoney7(stc.getRefundMoney7() + order.getRefundFee());
            }
        }
    }

    /**
     * 统计数据(近30天+7天用)
     * @param trade
     * @param stc
     */
    private void calcOrder30(Trade trade, PurchaseStatistics30 stc, Date endTime) {

        /**
         * 统计近30天数据
         */
        //统计优惠券
        if (trade.getCouponId() > 0) {
            stc.setCouponMoney30(stc.getCouponMoney30() + trade.getCouponFee());
        }
        //统计打包袋
        stc.setPackFee30(stc.getPackFee30() + trade.getPackFee());
        //统计打包袋优惠金额
        stc.setPackSubsidy30(stc.getPackSubsidy30() + trade.getPackSubsidy());

        /**
         * 统计近7天数据
         */
        //获取7天前的开始日期
        Date startTime7 = DateUtil.getDayStart(DateUtil.dateAdd(endTime, -7));
        if(!trade.getCreated().before(startTime7)) {
            //统计优惠券
            if (trade.getCouponId() > 0) {
                stc.setCouponMoney7(stc.getCouponMoney7() + trade.getCouponFee());
            }
            //统计打包袋
            stc.setPackFee7(stc.getPackFee7() + trade.getPackFee());
            //统计打包袋优惠金额
            stc.setPackSubsidy7(stc.getPackSubsidy7() + trade.getPackSubsidy());
        }
    }


    /**
     * 进货商统计明细
     */
    public void detail(){
        int id = getParaToInt("id", 0);
        Date startTime = getParaToDate("startTime", null);      //下单时间-开始
        Date endTime = getParaToDate("endTime", null);          //下单时间-结束

        List<OrderStatus> statuses = new ArrayList<>();
        statuses.addAll(OrderStatus.DEALINGS);
        statuses.addAll(OrderStatus.REFUNDS);
        statuses.add(OrderStatus.FINISHED);

        List<Order> orders = new ArrayList<>();

        if(startTime != null) {
            orders = orderService.findOrders(id, null, null, statuses, startTime, endTime);
        }

        Customer buyer = customerService.getById(id);
        setAttr("buyer", buyer);

        Multimap<Integer, Order> tradeOrders = Multimaps.index(orders, tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(tradeOrders.keySet());
        for (Trade trade : trades) {
            trade.setOrders(tradeOrders.get(trade.getId()));
        }
        setAttr("trades", trades);
        render("detail.html");
    }

    public void exportNow(){
        String username = getPara("username");                  //进货商账号
        String supplierName = getPara("supplierName");          //进货商名称
        Long minMoney = getParaToLong("minMoney", 0l);          //总商品金额开始
        Long maxMoney = getParaToLong("maxMoney", 0l);          //总商品金额结束
        Date startTime = getParaToDate("startTime", null);      //下单时间-开始
        Date endTime = getParaToDate("endTime", null);          //下单时间-结束

        List<OrderStatus> statuses = new ArrayList<>();
        statuses.addAll(OrderStatus.DEALINGS);
        statuses.addAll(OrderStatus.REFUNDS);
        statuses.add(OrderStatus.FINISHED);

        List<Order> orders = new ArrayList<>();

        //获取范围内订单
        if(startTime != null) {
            orders = orderService.findOrders(0, username, supplierName, statuses, startTime, endTime);
        }
        //计算运费
        orderService.calcPostFee(orders);
        Map<Integer, PurchaseStatistics> statisticsMap = new HashMap<>();
        statistics(statisticsMap, orders);
        List<PurchaseStatistics> statisticses = new ArrayList<>();
        for(PurchaseStatistics statistics : statisticsMap.values()){
            if(statistics.getTotalGoodsMoney() >= minMoney * 100 && (maxMoney == 0 || (maxMoney != 0 && statistics.getTotalGoodsMoney() <= maxMoney * 100))){
                statisticses.add(statistics);
            }
        }
        setAttr("startTime", DateUtil.getDateFormat(startTime,"yyyy-MM-dd"));
        setAttr("endTime", DateUtil.getDateFormat(endTime,"yyyy-MM-dd"));
        setAttr("items", statisticses);
        excelRender("purchase.xls", "进货商交易统计" + DateUtil.getDateFormat(startTime, "yyyyMMdd") + "-" + DateUtil.getDateFormat(endTime, "yyyyMMdd") + ".xls");
    }

    public void export30(){
        String username = getPara("username");                  //进货商账号
        String supplierName = getPara("supplierName");          //进货商名称
        Date startTime;
        Date endTime;

        List<OrderStatus> statuses = new ArrayList<>();
        statuses.addAll(OrderStatus.DEALINGS);
        statuses.addAll(OrderStatus.REFUNDS);
        statuses.add(OrderStatus.FINISHED);

        //获取进30天数据
        endTime = DateUtil.getDayEnd(new Date());
        startTime = DateUtil.dateAdd(endTime, -30);
        List<Order> orders = orderService.findOrders(0, username, supplierName, statuses, startTime, endTime);
        //计算运费
        orderService.calcPostFee(orders);
        Map<Integer, PurchaseStatistics30> statisticsMap = new HashMap<>();
        statistics(statisticsMap, orders, endTime);

        setAttr("today", DateUtil.getDateFormat(endTime,"yyyy-MM-dd"));
        setAttr("items", statisticsMap.values());
        excelRender("purchase30.xls", "进货商交易统计近30+7天" + DateUtil.getDateFormat(endTime,"yyyyMMdd") + ".xls");
    }
}
