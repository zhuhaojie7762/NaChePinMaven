package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.ExpressHelper;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

import static com.aichebaba.rooftop.model.enums.OrderStatus.PICKING;
import static com.aichebaba.rooftop.model.enums.OrderStatus.WAIT_PICKING;

@Controller
@Scope("prototype")
public class PickPeiOrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private PickOrderService pickOrderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProvinceService provinceService;

    /**
     * 未完成配货单列表
     */
    public void unfinished(){
        String code = getPara("code");
        PageList<PickOrder> pickOrders = pickOrderService.findUnfinishedPeiOrder(code, getPageParam());
        setAttr("pager", pickOrders);
        render("unfinished.html");
    }

    /**
     * 已完成配货单列表
     */
    public void finished(){
        String code = getPara("code");
        PageList<PickOrder> pickOrders = pickOrderService.findFinishedPeiOrder(code, getPageParam());
        setAttr("pager", pickOrders);
        render("finished.html");
    }

    /**
     * 配货单的订单列表
     * @throws ParseException
     */
    public void order() throws ParseException {
        String pickOrderCode = getPara("pickOrderCode");
        String tradeCode = getPara("tradeCode");

        PickOrder pickOrder = pickOrderService.getByCode(pickOrderCode);
        setAttr("pickOrder", pickOrder);

        //根据配货单号获取总订单
        List<Trade> trades = pickOrderService.findTradesByPickOrderCode(pickOrderCode, tradeCode);
        //根据总订单获取子订单
        Collection<Integer> tradeIds = Collections2.transform(trades, Trade.ID_VALUE);
        List<Order> orders = orderService.getOrdersByTradeIds(tradeIds);
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orders, Order.tradeIdValue);
        //根据子订单获取供货商
        Collection<Integer> supplierIds = Collections2.transform(orders, Order.sellerIdValue);
        Map<Integer, Customer> supplierMap = customerService.getCustomersByIds(supplierIds);
        //根据子订单获取商品
        Collection<String> goodsCodes = Collections2.transform(orders, Order.GOODS_CODE_VALUE);
        Map<String, Goods> goodsMap = goodsService.getGoodsByCodes(goodsCodes);

        for(Trade trade : trades){
            trade.setOrders(orderMultimap.get(trade.getId()));
            if(trade.getCouponId() > 0){
                Coupon coupon = couponService.getCoupon(trade.getCouponId());
                trade.setCoupon(coupon);
            }
            for (Order order : trade.getOrders()) {
                Customer supplier = supplierMap.get(order.getSellerId());
                order.setSupplierCompany(supplier.getSupplierCompany());
                Goods goods = goodsMap.get(order.getGoodsCode());
                order.setBrand(goods.getBrand());
                order.setHeadImg(goods.getHeadImgUrl1());
            }
        }
        setAttr("trades", trades);
        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);

        render("peiOrder.html");
    }

    /**
     * 订单详细
     */
    public void detail() {
        String code = getPara("code");
        int tradeId = Trade.code2id(code);
        Trade trade = orderService.getTrade(tradeId);
        setAttr("trade", trade);

        List<Order> orders = orderService.getOrdersByTradeId(tradeId);
        for(Order order : orders) {
//            String spec = order.getSpecPropValue();
//            if (spec.length() > 0) {
//                spec = spec.substring(0, spec.length() - 1);
//                spec = spec.replace(";", "<br />");
//            }
//            order.setSpecPropValue(spec);
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            order.setHeadImg(goods.getHeadImgUrl1());
            if (order.getPickerId() > 0) {
                User picker = userService.getById(order.getPickerId());
                if(picker != null) {
                    order.setPickerName(picker.getName());
                }
            }
            Customer supplier = customerService.getById(order.getSellerId());
            if(supplier != null){
                order.setSellerCompany(supplier.getSupplierCompany());
                order.setSellerAddress(supplier.getPickAddress());
            }
        }
        setAttr("orders", orders);
        SendOrder sendOrder = orderService.getSendOrderByTradeId(tradeId);
        setAttr("sendOrder", sendOrder);
        if(sendOrder != null) {
            County county = provinceService.getCountyInfo(sendOrder.getCountyId());
            setAttr("county", county);
        }

//        setAttr("province", provinceDao.getById(trade.getProvinceId()));
//
//        Customer seller = customerService.getById(order.getSellerId());
//        setAttr("seller", seller);
        Customer buyer = customerService.getById(trade.getBuyerId());
        setAttr("buyer", buyer);

        // 物流API对接
        if (sendOrder != null) {
            ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(sendOrder.getExpressId());
            String expressMark = (expressCompany == null ? "tiantian" : expressCompany.getCode());
            JSONObject expressJson = ExpressHelper.getExpressResult(expressMark, sendOrder.getExpressCode());
            setAttr("expressJson", expressJson);
        }
        if (StrKit.notBlank(trade.getPickOrderCode())) {
            PickOrder pickOrder = pickOrderService.getByCode(trade.getPickOrderCode());
            setAttr("pickOrder", pickOrder);
        }

        render("trade_detail.html");
    }

    /**
     * 未完成订单
     */
    public void unOrder() {
        String tradeCode = getPara("tradeCode");
        String orderCode = getPara("orderCode");

        //获取未完成的总订单
        PageList<Trade> pager = orderService.findUnPickingTrades(tradeCode, orderCode, getPageParam());
        Collection<Integer> tradeIds = Collections2.transform(pager.getData(), Trade.ID_VALUE);
        //根据总订单获取子订单
        List<Order> orders = orderService.getOrdersByTradeIds(tradeIds);

        Multimap<Integer, Order> orderMultimap = Multimaps.index(orders, Order.tradeIdValue);
//        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());
        //根据子订单获取供货商
        Collection<Integer> supplierIds = Collections2.transform(orders, Order.sellerIdValue);
        Map<Integer, Customer> supplierMap = customerService.getCustomersByIds(supplierIds);
        //根据子订单获取商品
        Collection<String> goodsCodes = Collections2.transform(orders, Order.GOODS_CODE_VALUE);
        Map<String, Goods> goodsMap = goodsService.getGoodsByCodes(goodsCodes);
        for (Trade trade : pager.getData()) {
            trade.setOrders(orderMultimap.get(trade.getId()));
            if(trade.getCouponId() > 0){
                Coupon coupon = couponService.getCoupon(trade.getCouponId());
                trade.setCoupon(coupon);
            }
            for (Order order : trade.getOrders()) {
                Goods goods = goodsMap.get(order.getGoodsCode());
                if (goods != null) {
                    order.setHeadImg(goods.getHeadImgUrl1());
                    order.setBrand(goods.getBrand());
                }
                Customer seller = supplierMap.get(order.getSellerId());
                if (seller != null) {
                    order.setSellerName(seller.getName());
                    order.setSellerCompany(seller.getSupplierCompany());
                    order.setPickAddress(seller.getPickAddress());
                }
            }
        }
        setAttr("pager", pager);
        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);
        render("unOrder.html");
    }
}
