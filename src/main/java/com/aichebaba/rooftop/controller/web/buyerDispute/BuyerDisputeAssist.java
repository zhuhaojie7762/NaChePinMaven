package com.aichebaba.rooftop.controller.web.buyerDispute;

import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by He Daoyuan on 2016/12/5.
 */
public class BuyerDisputeAssist {
    public static Map<String, String> getImgCroupMap(String refundImg) {
        Map<String, String> mapImg = new HashMap<>();
        if (!StrKit.isBlank(refundImg)) {
            String[] imgs = refundImg.split("#");
            for (int i = 0; i < imgs.length; i++) {
                mapImg.put("refundImg" + (i + 1), imgs[i]);
            }
        }
        return mapImg;
    }

    public static List<String> getImgCroupList(String refundImg) {
        List<String> listImg = new ArrayList<>();
        Map<String, String> map = getImgCroupMap(refundImg);
        for (String key : map.keySet()) {
            listImg.add(map.get(key));
        }
        return listImg;
    }

    public static String getReasonOrder(Order order) {
        return splitReason(order).get("refundReason");
    }

    public static String getRemarkOrder(Order order) {
        return splitReason(order).get("remarkReason");
    }

    public static Map<String, String> splitReason(Order order) {
        Map<String, String> reason = new HashMap<>();
        String str = order.getRefundReason();
        if (StrKit.notBlank(str)) {
            int flag = str.indexOf(":");
            reason.put("refundReason", str.substring(0, flag));
            reason.put("remarkReason", str.substring(flag + 1));
        }
        return reason;
    }

    public static String checkOrder(Order order) {
        if (order.getIsSpecial()) {
            return "该商品为特殊商品，不能退款";
        }
        if ((int) order.getStatus().getVal() < (int) OrderStatus.WAIT_PICKING.getVal()) {
            return "亲，该订单在待付款中，可直接关闭交易";
        }
        if (order.getStatus().equals(OrderStatus.FINISHED)) {
            return "已完成的订单不能退款";
        }
        return null;
    }

    public static OrderStatus getRefundType(Order order) {
        return order.getApplyRefundFlag().equals(OrderStatus.APPLY_REFUND_GOODS)? OrderStatus.REFUND_GOODS_CANCEL : OrderStatus.REFUND_CANCEL;
    }
}
