package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.ExpressHelper;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aichebaba.rooftop.model.enums.OrderStatus.PICKING;
import static com.aichebaba.rooftop.model.enums.OrderStatus.WAIT_PICKING;
import static com.aichebaba.rooftop.model.enums.OrderStatus.WAIT_RECEIVE;

@Controller
@Scope("prototype")
public class BuyOrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Value("${trade.prefix:T}")
    private String tradePrefix;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private GoodsService goodsService;

    @ActionKey("list.html")
    public void list() {
        String brand = getPara("brand");                        //品牌
        String tradeCode = getPara("tradeCode");                //总订单号
        String orderCode = getPara("orderCode");                //子订单号
        String purchaseComment = getPara("purchaseComment");    //进货商备注
        String goodsItemNo = getPara("goodsItemNo");            //商品货号
        int expressId = getParaToInt("express", 0);             //快递公司id
        String goodsName = getPara("goodsName");                //商品名称
        String receiveName = getPara("receiveName");            //收货人姓名
        String expressCode = getPara("expressCode");            //快递单号
        Date startTime = getParaToDate("startTime", null);      //下单时间-开始
        Date endTime = getParaToDate("endTime", null);          //下单时间-结束
        String statusVal = getPara("status", "");               //交易状态
        OrderStatus status = null;
        if (StrKit.notBlank(statusVal)) {
            status = OrderStatus.valueOf(Integer.parseInt(statusVal));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("brand", brand);
        params.put("tradeCode", tradeCode);
        params.put("orderCode", orderCode);
        params.put("purchaseComment", purchaseComment);
        params.put("goodsItemNo", goodsItemNo);
        params.put("expressId", expressId);
        params.put("goodsName", goodsName);
        params.put("receiveName", receiveName);
        params.put("expressCode", expressCode);
        params.put("createdFrom", startTime);
        params.put("createdEnd", endTime);
        params.put("status", status);
        params.put("purchaseId", curCustomerId());

        PageList<Order> orderPageList = orderService.findOrderList(params, getPageParam());
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orderPageList.getData(), Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());
        for (Trade trade : trades) {
            int quantity = 0;
            trade.setOrders(orderMultimap.get(trade.getId()));
            trade.setProvince(provinceService.getProvince(trade.getProvinceId()));
            trade.setCity(provinceService.getCity(trade.getCityId()));
            trade.setCounty(provinceService.getCounty(trade.getCountyId()));
            for (Order order : trade.getOrders()) {
//                if (order.getStatus().equals(WAIT_RECEIVE)) {
                    /*
                    设置这值是给页面退款限制最大退款金额用
                    最大退款金额=总金额-订单中优惠的金额*（需退款商品原价/订单原价）
                    */
                    if (trade.getCouponId() > 0 && trade.getUseType().equals(UseType.PAYMENT)) {
                        long couponFee = trade.getCouponFee() * order.getMoney() / trade.getTotalPayment();
                        order.setRefundFee(order.getPayment() - couponFee);
                        order.setCouponFee(couponFee);
                    } else {
                        order.setRefundFee(order.getPayment());
                    }
//                }
                quantity += order.getQuantity();
            }
            trade.setQuantity(quantity);
        }
        PageList<Trade> pager = new PageList<>(trades, orderPageList.getPage());
        setAttr("tradePrefix", tradePrefix);
        setAttr("pager", pager);
        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);

        render("list.html");
    }

    /**
     * 退款申请
     */
    public void applyRefund() {
        String code = getPara("code");
        Double refundFee = getParaToDouble("refundFee");

        if (refundFee == null) {
            error("请输入退款金额");
            return;
        }

        long refundFeeFen = (long) (refundFee * 100);
        if (refundFeeFen < 0) {
            error("请输入退款金额");
            return;
        }

        Order order = orderService.getOrderByCode(code);
        if (order.getIsSpecial()) {
            error("该商品为特殊商品，不能退款。");
            return;
        }
        if (!order.getStatus().equals(OrderStatus.WAIT_RECEIVE)) {
            error("已完成的订单不能退款");
            return;
        }
        if (!order.getStatus().equals(OrderStatus.WAIT_RECEIVE)) {
            error("订单不可申请退款:" + order.getStatus().getName());
            return;
        }

        BigDecimal fee = new BigDecimal(getPara("refundFee")).multiply(new BigDecimal(100));
        BigDecimal money = new BigDecimal(order.getMoney());
        if(fee.compareTo(money) > 0){
            error("退款金额不得大于货款");
            return;
        }

        String reason = getPara("reason");
        String remark = getPara("remark");
        orderService.applyRefund(code, refundFeeFen, reason + ":" + remark);
        ok("退款申请成功");
    }

    /**
     * 退货申请
     */
    public void applyRefundGoods() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        if (order.getIsSpecial()) {
            error("该商品为特殊商品，不能退货。");
            return;
        }
        if (!order.getStatus().equals(OrderStatus.WAIT_RECEIVE)) {
            error("已完成的订单不能退货");
            return;
        }
        String reason = getPara("reason");
        String remark = getPara("remark");
        orderService.refundApplication(code, reason + ":" + remark);
        ok("退货申请成功");
    }

    /**
     * @创建时间： 2016年4月3日
     * @相关参数：
     * @功能描述： 取消订单
     */
    /* 废弃 2016-12-09
    @Tx
    public void applyCancelTrade() {
        int tradeId = getParaToInt("tradeId");
        Trade trade = orderService.getTrade(tradeId);
        if (trade == null) {
            error("请选中要取消的订单");
            return;
        }

        if (!WAIT_PICKING.equals(trade.getStatus())) {
            error("对不起，商品已拣货完成，无法取消！");
            return;
        }

        List<Order> orders = orderService.getOrdersByTradeId(tradeId);
        for (Order order : orders) {
            if (!WAIT_PICKING.equals(order.getStatus()) && !PICKING.equals(order.getStatus())) {
                error("对不起，部分商品已拣货完成，无法取消！");
                return;
            }
        }
        String reason = getPara("reason");
        String remark = getPara("remark");
        orderService.applyCancelTrade(trade, orders, reason + ":" + remark);
        ok("取消申请成功");
    }
    */

    /**
     * 退货发回
     */
    public void goodsBack() {
        String code = getPara("code");
        String refundExpressCompany = getPara("refundExpressCompany");
        String refundExpressCode = getPara("refundExpressCode");
        if (StrKit.isBlank(refundExpressCode)) {
            error("请填写运单号");
            return;
        }
        Order order = orderService.getOrderByCode(code);
        order.setRefundExpressCompany(refundExpressCompany);
        order.setRefundExpressCode(refundExpressCode);
        order.setStatus(OrderStatus.REFUND_GOODS_DELIVERY_ING);
        order.setRefundConsignTime(new Date());
        orderService.update(order);
        ok("退货发回填写成功");
    }

    /**
     * @创建时间： 2016年4月3日
     * @相关参数：
     * @功能描述： 退款
     */
    public void goodsMoneyBack() {
        String code = getPara("code");
        String refundExpressCompany = getPara("refundExpressCompany");
        String refundExpressCode = getPara("refundExpressCode");
        if (StrKit.isBlank(refundExpressCode)) {
            error("请填写运单号");
            return;
        }
        Order order = orderService.getOrderByCode(code);
        order.setRefundExpressCompany(refundExpressCompany);
        order.setRefundExpressCode(refundExpressCode);
        order.setStatus(OrderStatus.REFUND_GOODS_DELIVERY_ING);
        order.setRefundConsignTime(new Date());
        orderService.update(order);
        ok("退款发回填写成功");
    }

    /**
     * 订单详细
     */
    @ActionKey("{id}.html")
    public void details() {
        int id = getUrlParaToInt("id", 0);
        Trade trade = orderService.getTrade(id);
        setAttr("trade", trade);
        if(trade.getCouponId() > 0) {
            Coupon coupon = couponService.getCoupon(trade.getCouponId());
            setAttr("coupon", coupon);
        }else{
            setAttr("coupon", null);
        }
        List<Order> orders = orderService.getOrdersByTradeId(id);
        for(Order order : orders){
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            if(goods != null){
                order.setHeadImg(goods.getHeadImgUrl1());
            }
        }
        setAttr("orders", orders);
        County county = provinceService.getCountyInfo(trade.getCountyId());
        setAttr("county", county);

        SendOrder sendOrder = orderService.getSendOrderByTradeId(id);
        setAttr("sendOrder", sendOrder);

        // 物流API对接 add by 2016-5-25
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(sendOrder.getExpressId());

        String expressMark = (expressCompany == null ? "tiantian" : expressCompany.getCode());
        JSONObject expressJson = ExpressHelper.getExpressResult(expressMark, sendOrder.getExpressCode());
        setAttr("expressJson", expressJson);
        render("detail.html");
    }

    @Tx
    public void closeTrade(){
        int id = getParaToInt("id", 0);
        orderService.closeTrade(id);
        ok("关闭交易成功");
    }

    /**
     * 修改进货商备注
     */
    @Tx
    public void editPurchaseComment(){
        int tradeId = getParaToInt("tradeId", 0);
        String comment = getPara("comment");

        Trade trade = orderService.getTrade(tradeId);
        if(trade != null) {
            trade.setPurchaseComment(comment);
            orderService.updateTrade(trade);
        }
        ok("保存成功");
    }
}
