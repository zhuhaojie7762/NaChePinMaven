package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class SellOrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExpressCompanyService expressCompanyService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ProvinceService provinceService;

    @ActionKey("list.html")
    public void list() {

        String brand = getPara("brand");                        //品牌
        String tradeCode = getPara("tradeCode");                //总订单号
        String orderCode = getPara("orderCode");                //子订单号
        String supplierComment = getPara("supplierComment");    //进货商备注
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
        params.put("supplierComment", supplierComment);
        params.put("goodsItemNo", goodsItemNo);
        params.put("expressId", expressId);
        params.put("goodsName", goodsName);
        params.put("receiveName", receiveName);
        params.put("expressCode", expressCode);
        params.put("createdFrom", startTime);
        params.put("createdEnd", endTime);
        params.put("status", status);
        params.put("supplierId", curCustomerId());

        PageList<Order> orderPageList = orderService.findOrderList(params, getPageParam());
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orderPageList.getData(), Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());
        for (Trade trade : trades) {
            trade.setOrders(orderMultimap.get(trade.getId()));
        }
        PageList<Trade> pager = new PageList<>(trades, orderPageList.getPage());
        setAttr("pager", pager);

        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);

        render("list.html");
    }

    @ActionKey("{id}.html")
    public void detail() {
        int id = getUrlParaToInt("id", 0);
        Trade trade = orderService.getTrade(id);
        setAttr("trade", trade);

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

    /**
     * 修改供货商备注
     */
    @Tx
    public void editSupplierComment(){
        int tradeId = getParaToInt("tradeId", 0);
        String comment = getPara("comment");

        Trade trade = orderService.getTrade(tradeId);
        if(trade != null) {
            trade.setSupplierComment(comment);
            orderService.updateTrade(trade);
        }
        ok("保存成功");
    }
}
