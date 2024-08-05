package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.ProvinceService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.vo.ProviderStatistics;
import com.google.common.collect.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static com.aichebaba.rooftop.model.Order.tradeIdValue;
import static com.aichebaba.rooftop.model.enums.OrderStatus.FINISHED;
import static com.aichebaba.rooftop.utils.DateUtil.getMonthStart;

/**
 * @auther huwhy
 * @date 2016/4/27.
 */
@Controller
@Scope("prototype")
public class StatisticsSupplierController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceService provinceService;

    @Value("${trade.prefix:T}")
    private String tradePrefix;

    public void index() throws ParseException {
        String sellerCode = getPara("sellerCode");
        String sellerUsername = getPara("sellerUsername");
        String sellerName = getPara("sellerName");
        String status = getPara("status", "all");
        BigDecimal paymentFrom = getParaToBigDecimal("paymentFrom", 0);
        BigDecimal paymentTo = getParaToBigDecimal("paymentTo", 0);
        Date createdFrom = getParaToDate("createdFrom", getMonthStart(new Date()));
        createdFrom = DateUtil.getDayStart(createdFrom);
        Date createdEnd = getParaToDate("createdEnd", new Date());
        createdEnd = DateUtil.getDayEnd(createdEnd);
        setAttr("createdFrom", DateFormatUtils.format(createdFrom, "yyyy-MM-dd"));
        setAttr("createdEnd", DateFormatUtils.format(createdEnd, "yyyy-MM-dd"));

        List<OrderStatus> statuses = new ArrayList<>();
        if ("all".equals(status) || "0".equals(status)) {
            statuses.add(OrderStatus.FINISHED);
            statuses.add(OrderStatus.CLOSED);
            statuses.addAll(OrderStatus.DEALINGS);
            statuses.addAll(OrderStatus.REFUNDS);
        } else if ("ongoing".equals(status)) {
            statuses.addAll(OrderStatus.DEALINGS);
        } else if ("finished".equals(status)) {
            statuses.add(FINISHED);
        } else if ("refund".equals(status)) {
            statuses.addAll(OrderStatus.REFUNDS);
        }
        PageParam pageParam = getPageParam();

        Map<Integer, ProviderStatistics> statisticsMap = new HashMap<>();
        PageList<Order> details = null;
        ProviderStatistics total = new ProviderStatistics();
        List<Order> orders;

        orders = orderService.findOrders(sellerCode, sellerUsername, sellerName, statuses, paymentFrom,
                paymentTo, createdFrom, createdEnd);

        orderService.calcPostFee(orders);
        statistics(statisticsMap, orders);

        setSessionAttr("ps_statistics_map", statisticsMap);

        for (ProviderStatistics p : statisticsMap.values()) {
            total.setTotalPayment(total.getTotalPayment() + p.getTotalPayment());
            total.setTotalOrderNum(total.getTotalOrderNum() + p.getTotalOrderNum());
            total.setTotalMoney(total.getTotalMoney() + p.getTotalMoney());
            total.setTotalPostFee(total.getTotalPostFee() + p.getTotalPostFee());
        }

        details = orderService.findOrders(sellerCode, sellerUsername, sellerName, statuses, paymentFrom,
                paymentTo, createdFrom, createdEnd, pageParam);
        orderService.calcPostFee(details.getData());
//        for (Order order : details.getData()) {
//            calcOrder(order, total);
//        }
//        statisticsMap = getSessionAttr("ps_statistics_map");
        setAttr("pager", details);

        setAttr("tabParam", getPara("tabParam"));
        setAttr("total", total);
        setAttr("items", statisticsMap.values());
        render("list.html");
    }

    public void export() {

        Date createdFrom = getParaToDate("createdFrom", getMonthStart(new Date()));
        createdFrom = DateUtil.getDayStart(createdFrom);
        Date createdEnd = getParaToDate("createdEnd", new Date());
        createdEnd = DateUtil.getDayEnd(createdEnd);
        setAttr("createdFrom", DateFormatUtils.format(createdFrom, "yyyy-MM-dd"));
        setAttr("createdEnd", DateFormatUtils.format(createdEnd, "yyyy-MM-dd"));
        String tab = getPara("tab", "total");

        String from = DateFormatUtils.format(createdFrom, "yyyyMMdd");
        String end = DateFormatUtils.format(createdEnd, "yyyyMMdd");
        setAttr("from", from);
        setAttr("end", end);
        if ("total".equals(tab)) {
            Integer[] ids = getParaValuesToInt("ids");
            if (ids == null || ids.length == 0) {
                renderText("请选择数据");
                return;
            }
            exportTotal(from, end, ids);
        } else {
            exportDetail(createdFrom, createdEnd, from, end);
        }
    }

    private void exportDetail(Date createdFrom, Date createdEnd, String from, String end) {
        String code = getPara("codes");
        List<Order> orders = new ArrayList<>();
        if (StrKit.isBlank(code)) {
            String sellerCode = getPara("sellerCode");
            String sellerUsername = getPara("sellerUsername");
            String sellerName = getPara("sellerName");
            String status = getPara("status", "all");
            BigDecimal paymentFrom = getParaToBigDecimal("paymentFrom", 0);
            BigDecimal paymentTo = getParaToBigDecimal("paymentTo", 0);
            List<OrderStatus> statuses = new ArrayList<>();
            if ("all".equals(status) || "0".equals(status)) {
                statuses.add(OrderStatus.FINISHED);
                statuses.add(OrderStatus.CLOSED);
                statuses.addAll(OrderStatus.DEALINGS);
                statuses.addAll(OrderStatus.REFUNDS);
            } else if ("ongoing".equals(status)) {
                statuses.addAll(OrderStatus.DEALINGS);
            } else if ("finished".equals(status)) {
                statuses.add(FINISHED);
            } else if ("refund".equals(status)) {
                statuses.addAll(OrderStatus.REFUNDS);
            }

            orders = orderService.findOrders(sellerCode, sellerUsername, sellerName, statuses, paymentFrom,
                    paymentTo, createdFrom, createdEnd);
            orderService.calcPostFee(orders);

        } else {
            String[] codes = code.split(",");
            orders.addAll(orderService.getOrderByCodes(Arrays.asList(codes)));
            orderService.calcPostFee(orders);
            Collection<Integer> sellerIds = Collections2.transform(orders, Order.sellerIdValue);
            Map<Integer, Customer> sellerMap = customerService.getCustomersByIds(sellerIds);
            Collection<Integer> buyerIds = Collections2.transform(orders, Order.buyerIdValue);
            Map<Integer, Customer> buyerMap = customerService.getCustomersByIds(buyerIds);
            for (Order o : orders) {
                Customer seller = sellerMap.get(o.getSellerId());
                if (seller != null) {
                    o.setSellerCode(seller.getCode());
                    o.setSellerName(seller.getName());
                    o.setSellerUsername(seller.getUsername());
                    o.setSellerCompany(seller.getSupplierCompany());
                }
                Customer buyer = buyerMap.get(o.getBuyerId());
                if(buyer != null){
                    o.setBuyerCode(buyer.getCode());
                    o.setBuyerUsername(buyer.getUsername());
                    o.setBuyerName(buyer.getName());
                }
            }
        }
        if (orders.isEmpty()) {
            renderText("请选择数据");
        } else {
            Collection<Integer> tradeIds = Collections2.transform(orders, Order.tradeIdValue);
            List<Trade> trades = orderService.getTradesByIds(tradeIds);
            Map<Integer, Trade> tradeMap = Maps.uniqueIndex(trades, Trade.ID_VALUE);
            ProviderStatistics total = null;
            for (Order order : orders) {
                if (total == null) {
                    total = new ProviderStatistics();
                    total.setSellerUsername(order.getSellerUsername());
                }
                Trade trade = tradeMap.get(order.getTradeId());
                if(trade != null){
                    order.setAlipayNo(trade.getAlipayNo());
                    order.setAlipayment(trade.getPayment());
                    order.setAlipayOrderCode(tradePrefix + trade.getId());
                }
                calcOrder(order, total);
                SendOrder sendOrder = orderService.getSendOrderById(order.getSendOrderId());
                order.setSendOrder(sendOrder);
                if (sendOrder != null) {
                    Province province = provinceService.getProvince(sendOrder.getProvinceId());
                    if (province != null) {
                        sendOrder.setProvinceName(province.getName());
                    }
                    County county = provinceService.getCountyInfo(sendOrder.getCountyId());
                    if (county != null) {
                        sendOrder.setCityName(county.getCityName());
                        sendOrder.setCountyName(county.getName());
                    }
                }
            }
            setAttr("total", total);
            setAttr("exportDate", new Date());
            setAttr("items", orders);
            excelRender("sales_detail.xls", "供货商交易统计明细" + from + "-" + end + ".xls");
        }
    }

    protected void exportTotal(String from, String end, Integer[] ids) {
        Set<Integer> sellerIds = Sets.newHashSet(ids);
        Map<Integer, ProviderStatistics> statisticsMap = getSessionAttr("ps_statistics_map");
        ProviderStatistics total = new ProviderStatistics();
        List<ProviderStatistics> items = new ArrayList<>();
        for (ProviderStatistics stc : statisticsMap.values()) {
            if (sellerIds.contains(stc.getSellerId())) {
                items.add(stc);
                total.setTotalPayment(total.getTotalPayment() + stc.getTotalPayment());
                total.setTotalOrderNum(total.getTotalOrderNum() + stc.getTotalOrderNum());
                total.setTotalMoney(total.getTotalMoney() + stc.getTotalMoney());
                total.setTotalPostFee(total.getTotalPostFee() + stc.getTotalPostFee());

                total.setFinishedPayment(total.getFinishedPayment() + stc.getFinishedPayment());
                total.setFinishedMoney(total.getFinishedMoney() + stc.getFinishedMoney());
                total.setFinishedNum(total.getFinishedNum() + stc.getFinishedNum());
                total.setFinishedPostFee(total.getFinishedPostFee() + stc.getFinishedPostFee());

                total.setDealingPayment(total.getDealingPayment() + stc.getDealingPayment());
                total.setDealingNum(total.getDealingNum() + stc.getDealingNum());
                total.setDealingMoney(total.getDealingMoney() + stc.getDealingMoney());
                total.setDealingPostFee(total.getDealingPostFee() + stc.getDealingPostFee());

                total.setRefundFee(total.getRefundFee() + stc.getRefundFee());
                total.setRefundMoney(total.getRefundMoney() + stc.getRefundMoney());
                total.setRefundNum(total.getRefundNum() + stc.getRefundNum());
                total.setRefundPostFee(total.getRefundPostFee() + stc.getRefundPostFee());

                total.setClosedPayment(total.getClosedPayment() + stc.getClosedPayment());
                total.setClosedMoney(total.getClosedMoney() + stc.getClosedMoney());
                total.setClosedNum(total.getClosedNum() + stc.getClosedNum());
            }
        }
        setAttr("total", total);
        setAttr("exportDate", new Date());
        setAttr("items", items);
        excelRender("sales_total.xls", "供货商交易统计" + from + "-" + end + ".xls");
    }

    protected void statistics(Map<Integer, ProviderStatistics> statisticsMap, Collection<Order> orders) {
        for (Order order : orders) {
            ProviderStatistics stc = getStatistics(statisticsMap, order);
            calcOrder(order, stc);
        }
    }

    private void calcOrder(Order order, ProviderStatistics stc) {
        if (!OrderStatus.isClosed(order.getStatus())) {
            stc.setTotalPayment(stc.getTotalPayment() + order.getMoney() + order.getPostFee());
            stc.setTotalOrderNum(stc.getTotalOrderNum() + 1);
            stc.setTotalMoney(stc.getTotalMoney() + order.getMoney());
            stc.setTotalPostFee(stc.getTotalPostFee() + order.getPostFee());
        }
        if (OrderStatus.isFinished(order.getStatus())) {
            stc.setFinishedPayment(stc.getFinishedPayment() + order.getMoney() + order.getPostFee());
            stc.setFinishedMoney(stc.getFinishedMoney() + order.getMoney());
            stc.setFinishedNum(stc.getFinishedNum() + 1);
            stc.setFinishedPostFee(stc.getFinishedPostFee() + order.getPostFee());
        }

        if (OrderStatus.isDealing(order.getStatus())) {
            stc.setDealingPayment(stc.getDealingPayment() + order.getMoney() + order.getPostFee());
            stc.setDealingNum(stc.getDealingNum() + 1);
            stc.setDealingMoney(stc.getDealingMoney() + order.getMoney());
            stc.setDealingPostFee(stc.getDealingPostFee() + order.getPostFee());
        }

        if (OrderStatus.isRefund(order.getStatus())) {
            stc.setRefundFee(stc.getRefundFee() + order.getRefundFee());
            stc.setRefundMoney(stc.getRefundMoney() + order.getMoney());
            stc.setRefundNum(stc.getRefundNum() + 1);
            stc.setRefundPostFee(stc.getRefundPostFee() + order.getPostFee());
        }

        if (OrderStatus.isClosed(order.getStatus())) {
            stc.setClosedPayment(stc.getClosedPayment() + order.getMoney() + order.getPostFee());
            stc.setClosedMoney(stc.getClosedMoney() + order.getMoney());
            stc.setClosedNum(stc.getClosedNum() + 1);
        }
    }

    private ProviderStatistics getStatistics(Map<Integer, ProviderStatistics> statisticsMap, Order order) {
        if (!statisticsMap.containsKey(order.getSellerId())) {
            ProviderStatistics ps = new ProviderStatistics();
            ps.setSellerId(order.getSellerId());
            ps.setSellerCode(order.getSellerCode());
            ps.setSellerName(order.getSellerName());
            ps.setSellerUsername(order.getSellerUsername());
            statisticsMap.put(order.getSellerId(), ps);
        }
        return statisticsMap.get(order.getSellerId());
    }
}