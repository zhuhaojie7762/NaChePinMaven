package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.ext.HttpHelper;
import com.aichebaba.rooftop.model.County;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.ExpressHelper;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 订单列表页
     */
    public void index() {

        String brand = getPara("brand");                        //品牌
        String tradeCode = getPara("tradeCode");                //总订单号
        String orderCode = getPara("orderCode");                //子订单号
        String purchaseName = getPara("purchaseName");          //进货商名称
        String supplierName = getPara("supplierName");          //供货商名称
        String managerComment = getPara("managerComment");      //管理员备注
        String goodsItemNo = getPara("goodsItemNo");            //商品货号
        int expressId = getParaToInt("expressId", 0);           //快递公司id
        String goodsName = getPara("goodsName");                //商品名称
        String receiveName = getPara("receiveName");            //收货人姓名
        String expressCode = getPara("expressCode");            //快递单号
        Date createdFrom = getParaToDate("createdFrom", null);  //下单时间-开始
        Date createdEnd = getParaToDate("createdEnd", null);    //下单时间-结束
        String statusVal = getPara("status", "");               //交易状态
        OrderStatus status = null;
        if (StrKit.notBlank(statusVal)) {
            status = OrderStatus.valueOf(Integer.parseInt(statusVal));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("brand", brand);
        params.put("tradeCode", tradeCode);
        params.put("orderCode", orderCode);
        params.put("purchaseName", purchaseName);
        params.put("supplierName", supplierName);
        params.put("managerComment", managerComment);
        params.put("goodsItemNo", goodsItemNo);
        params.put("expressId", expressId);
        params.put("goodsName", goodsName);
        params.put("receiveName", receiveName);
        params.put("expressCode", expressCode);
        params.put("createdFrom", createdFrom);
        params.put("createdEnd", createdEnd);
        params.put("status", status);
        params.put("purchaseId", curCustomerId());

        PageList<Order> orderPageList = orderService.findOrderList(params, getPageParam());
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orderPageList.getData(), Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());

        for (Trade trade : trades) {
            trade.setOrders(orderMultimap.get(trade.getId()));
            int quantity = 0;
            for (Order order : trade.getOrders()) {
                quantity += order.getQuantity();
            }
            trade.setQuantity(quantity);
        }
        PageList<Trade> pager = new PageList<>(trades, orderPageList.getPage());
        setAttr("pager", pager);
        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);
//        setAttr("statusList", OrderStatus.values());
        render("list.html");
    }

    /**
     * 订单详情页
     */
    /**
     * 分类管理
     */
    @ActionKey("classManage.html")
    public void classManage(){render("classManage.html");}

    /**
     * 分类属性管理
     */
    @ActionKey("attributeMag.html")
    public void attributeMag(){render("attributeMag.html");}

    /**
     * 货品查询
     */
    @ActionKey("goodsSearch.html")
    public void goodsSearch(){render("goodsSearch.html");}

    /**
     * 已下架的货品
     */
    @ActionKey("yixiajiaGoods.html")
    public void yixiajiaGoods(){render("yixiajiaGoods.html");}

    /**
     * 待上架的货品
     */
    @ActionKey("daishangjia.html")
    public void daishangjia(){render("daishangjia.html");}

    /**
     * 销售中的货品
     */
    @ActionKey("xiaoshouzhong.html")
    public void xiaoshouzhong(){render("xiaoshouzhong.html");}

    /**
     * 发布货品
     */
    @ActionKey("fabugoods.html")
    public void fabugoods(){render("fabugoods.html");}

    /**
     * 货品新增页
     */
    @ActionKey("newAddGoods.html")
    public void newAddGoods(){render("newAddGoods.html");}

    /**
     * 分类管理
     */
    @ActionKey("newClass.html")
    public void newClass(){render("newClass.html");}

    public void detail() {
        int id = getParaToInt("id", 0);
        Trade trade = orderService.getTrade(id);
        setAttr("trade", trade);
        List<Order> orders = orderService.getOrdersByTradeId(id);
        for(Order order : orders){
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            if(goods != null){
                order.setHeadImg(goods.getHeadImgUrl1());
            }
            Customer supplier = customerService.getById(order.getSellerId());
            if(supplier != null){
                order.setSellerName(supplier.getName());
                order.setSellerCompany(supplier.getSupplierCompany());
                order.setSellerPhone(supplier.getPhone());
                order.setSellerAddress(supplier.getProvince() + supplier.getCity() + supplier.getArea() + supplier.getAddress());
            }
        }
        setAttr("orders", orders);

        SendOrder sendOrder = orderService.getSendOrderByTradeId(id);
        setAttr("sendOrder", sendOrder);
        County county = provinceService.getCountyInfo(trade.getCountyId());
        setAttr("county", county);
        setAttr("province", provinceDao.getById(trade.getProvinceId()));

        /**
         * He Daoyuan 2016-5-17
         */
        Customer buyCustomer = customerService.getById(trade.getBuyerId());
        setAttr("buyCustomer", buyCustomer);

        // 物流API对接 add by 2016-5-25
        System.out.println(sendOrder.getExpressId());
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(sendOrder.getExpressId());
        String expressMark = (expressCompany == null ? "tiantian" : expressCompany.getCode());
        JSONObject expressJson = ExpressHelper.getExpressResult(expressMark, sendOrder.getExpressCode());
        setAttr("expressJson", expressJson);
    }

    /**
     * 修改价格页
     */
    public void edit(){
        int tradeId = getParaToInt("tradeId");
        Trade trade = orderService.getTrade(tradeId);
        List<Order> orders = orderService.getOrdersByTradeId(tradeId);
        setAttr("trade", trade);
        setAttr("orders", orders);
        String html = TemplateUtils.html("/admin/order/editPrice.html", getRequest());
        ok("", html);
    }

    /**
     * 修改管理员备注
     */
    @Tx
    public void editManagerComment(){
        int tradeId = getParaToInt("tradeId", 0);
        String comment = getPara("comment");

        Trade trade = orderService.getTrade(tradeId);
        if(trade != null) {
            trade.setManagerComment(comment);
            orderService.updateTrade(trade);
        }
        ok("保存成功");
    }

    /**
     * 修改价格
     */
    @Tx
    public void changePrice(){
        String[] orderCodes = getParaValues("orderCode");
        String[] newPrices = getParaValues("newPrice");
        int tradeId = getParaToInt("tradeId", 0);
        Double newPostFee = getParaToDouble("newPostFee", 0d);
        String reason = getPara("reason");
        Long totalMoney = 0l;
        for (int i = 0; i < orderCodes.length; i++) {
            String orderCode = orderCodes[i];
            Order order = orderService.getOrderByCode(orderCode);
            if (StrKit.notBlank(newPrices[i])) {
                Double newPrice = Double.parseDouble(newPrices[i]);
                Long tmpPrice = Math.round(newPrice * 100);
                //如果新价格与原来价格不同，则修改价格
                if (order.getPrice() != tmpPrice) {
                    //保存旧价格
                    if (order.getUpdatePriceTime() == null) {
                        order.setOldPrice(order.getPrice());
                    }
                    order.setPrice(tmpPrice);
                    order.setMoney(tmpPrice * order.getQuantity());
                    order.setUpdatePriceTime(new Date());
                    order.setUpdatePriceUserId(curCustomerId());
                    orderService.update(order);
                }
                //合计商品总价
                totalMoney += order.getMoney();
            }
        }
        Trade trade = orderService.getTrade(tradeId);
        //保存旧价格
        if (trade.getUpdatePriceTime() == null) {
            trade.setOldTotalPayment(trade.getTotalPayment());
            trade.setOldPostFee(trade.getPostFee());
            trade.setOldPayment(trade.getPayment());
        }
        //更新商品总金额
        trade.setTotalPayment(totalMoney);
        Long tmpPostFee = Math.round(newPostFee * 100);

        //使用抵运费券的时，运费需加上抵运费券抵去的价格
        if (trade.getCouponId() > 0 && trade.getUseType().equals(UseType.POST_FEE)) {
            tmpPostFee += trade.getCouponFee();
        }
        //更新邮费
        trade.setPostFee(tmpPostFee);

        //更新支付金额
        trade.setPayment(trade.getTotalPayment() + trade.getPostFee() - trade.getCouponFee());

        trade.setUpdatePriceReason(reason);
        trade.setUpdatePriceTime(new Date());
        trade.setUpdatePriceUserId(curCustomerId());
        orderService.updateTrade(trade);
        ok("修改成功");
    }
}
