package com.aichebaba.rooftop.controller.web.dispute;

import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 退款退货辅助
 * Created by 何道元 on 2016/11/24.
 */
public class RefundAssist {
    private static Logger logger = LoggerFactory.getLogger(RefundAssist.class);

    public static Long getDiscount(Trade trade, List<Order> orders) {
        Long couponFee = 0l;
        if (trade.getCouponId() != 0) {
            if (orders == null) {
                logger.error("trade " + trade.getId() + " is not existed order");
                throw new RuntimeException("exception order, trade is can't find order");
            }
            long orderNum = orders.size();
            couponFee = trade.getCouponFee() / orderNum;
        }
        return couponFee;
    }

    public static Long getMaxRefund(Trade trade, Order order, List<Order> orders) {
        return order.getMoney() - getDiscount(trade, orders) + trade.getPostFee();
    }

    public static String getDetailPrice(Trade trade, Order order, List<Order> orders) {
        return (double) order.getMoney()/100 + "-" + (double) getDiscount(trade, orders)/100 + "+" + (double) trade.getPostFee()/100 + "= " + (double) getMaxRefund(trade, order, orders)/100;
    }

    public static void setImgGroup(Order order, String[] imgs) {
        if (imgs != null && imgs.length != 0) {
            StringBuilder builder = new StringBuilder();
            for (String img : imgs) {
                builder.append(img + "#");
            }
            order.setRefundDisputeImg(builder.deleteCharAt(builder.length() - 1).toString());
        }
    }

    public static void setImgGroup(Order order, List<String> imgs) {
        setImgGroup(order, imgs.toArray(new String[imgs.size()]));
    }

    public static String checkOrder(Order order) {
        if (order.getIsSpecial()) {
            return "该商品为特殊商品，不能退款";
        }
        if ((int) order.getStatus().getVal() < (int) OrderStatus.WAIT_PICKING.getVal()) {
            return "亲，该订单在待付款中，可直接关闭交易";
        }
        if (order.getStatus().equals(OrderStatus.APPLY_REFUND) || order.getStatus().equals(OrderStatus.APPLY_REFUND_GOODS)) {
            return "亲，该商品已在退款退货中，请耐心等待";
        }
        if (order.getStatus().equals(OrderStatus.FINISHED)) {
            return "已完成的订单不能退款";
        }
        return null;
    }
}
