package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.PickOrderStatus;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.DateUtil;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.aichebaba.rooftop.model.enums.OrderStatus.PICKING;
import static com.aichebaba.rooftop.model.enums.OrderStatus.WAIT_PICKING;

@Controller
@Scope("prototype")
public class PickMyTodayController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private PickOrderService pickOrderService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private CouponService couponService;

    public void index() throws ParseException {
        String brand = getPara("brand");
        String goodsItemNo = getPara("itemNo");
        String goodsName = getPara("goodsName");
        String tradeCode = getPara("tradeCode");
        String orderCode = getPara("orderCode");
        String buyerName = getPara("buyerName");
        String sellerName = getPara("sellerName");
        int expressId = getParaToInt("expressId", 0);
        String strFrom = getPara("createdFrom");
        String strEnd = getPara("createdEnd");
        Date createdFrom = null;
        Date createdEnd = null;
        if(StrKit.notBlank(strFrom)){
            createdFrom = DateUtil.parse(strFrom + ":00");
        }
        if(StrKit.notBlank(strEnd)){
            createdEnd = DateUtil.parse(strEnd + ":59");
        }

        //获取待拣货的总订单
        PageList<Trade> pager = orderService.findWaitPickTrades(createdFrom, createdEnd, brand, goodsItemNo,
                goodsName, tradeCode, orderCode, buyerName, sellerName, expressId, getPageParam());
        Collection<Integer> tradeIds = Collections2.transform(pager.getData(), Trade.ID_VALUE);
        //根据总订单获取子订单
        List<Order> orders = orderService.getOrdersByTradeIds(tradeIds);
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orders, Order.tradeIdValue);

        for (Trade trade : pager.getData()) {
            trade.setOrders(orderMultimap.get(trade.getId()));
            if(trade.getCouponId() > 0){
                Coupon coupon = couponService.getCoupon(trade.getCouponId());
                trade.setCoupon(coupon);
            }
            for (Order order : trade.getOrders()) {
                Goods goods = goodsService.getByCode(order.getGoodsCode());
                if (goods != null) {
                    order.setHeadImg(goods.getHeadImgUrl1());
                    order.setBrand(goods.getBrand());
                }
                Customer seller = customerService.getById(order.getSellerId());
                if (seller != null) {
                    order.setSellerName(seller.getName());
                    order.setSellerCompany(seller.getSupplierCompany());
                    order.setPickAddress(seller.getPickAddress());
                }
            }
        }
//        PageList<Trade> pager = new PageList<>(trades, orderPageList.getPage());
        setAttr("pager", pager);
        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("express", expressCompanies);
        render("today_list.html");
    }

    @Tx
    public void export() throws UnsupportedEncodingException, ParseException {
        synchronized (PickMyTodayController.class) {
            String code = getPara("orderCode");
            String goodsCode = getPara("goodsCode");
            String goodsItemNo = getPara("itemNo");
            String sellerPhone = getPara("sellerPhone");
            String buyerPhone = getPara("buyerPhone");
            Boolean picking = getPara("picking", "").equals("0") ? null : getParaToBoolean("picking", false);
            String picker = getPara("picker");
            String sellerAddress = getPara("sellerAddress");
            Date createdFrom;
            String createdFromV = getPara("createdFrom");
            String codes = getPara("codes");
            if (StrKit.notBlank(createdFromV)) {
                createdFrom = DateUtils.parseDate(createdFromV, "yyyy-MM-dd HH:mm");
            } else {
                createdFrom = DateUtil.getDayStart(new Date());
            }
            Date createdEnd;
            String createdEndV = getPara("createdEnd");
            if (StrKit.notBlank(createdEndV)) {
                createdEnd = DateUtils.parseDate(createdEndV, "yyyy-MM-dd HH:mm");
            } else {
                createdEnd = DateUtil.getDayEnd(new Date());
            }
            String from = DateFormatUtils.format(createdFrom, "yyyyMMdd");
            String end = DateFormatUtils.format(createdEnd, "yyyyMMdd");
            List<OrderStatus> statuses = new ArrayList<>();
            if (picking != null) {
                if (picking) {
                    statuses.add(PICKING);
                } else {
                    statuses.add(WAIT_PICKING);
                }
            } else {
                statuses.add(PICKING);
                statuses.add(WAIT_PICKING);
            }
            PageParam pageParam = getPageParam();
            PageList<Order> pager;
            List<Order> orders = new ArrayList<>();
            if (StrKit.notBlank(codes)) {
                List<Order> list = orderService.getOrderByCodes(Lists.newArrayList(Splitter.on(',').split(codes)));
                for (Order o : list) {
                    if (o.getStatus().equals(WAIT_PICKING) || PICKING.equals(o.getStatus())) {
                        if (o.getPickerId() == 0 || o.getPickerId() == getCurUserId()) {
                            o.setStatus(PICKING);
                            o.setPickerId(getCurUserId());
                            o.setStartPickTime(new Date());
                            Customer seller = customerService.getById(o.getSellerId());
                            if (seller != null) {
                                o.setPickAddress(seller.getPickAddress());
                            }
                            orders.add(o);
                            orderService.update(o);
                        }
                    }
                }
            } else {
                do {
                    pager = orderService.findPickingOrders(createdFrom, createdEnd, statuses, code, goodsCode,
                            buyerPhone, picker, goodsItemNo, sellerPhone, sellerAddress, false, pageParam);
                    for (Order o : pager.getData()) {
                        if (o.getStatus().equals(WAIT_PICKING) || PICKING.equals(o.getStatus())) {
                            if (o.getPickerId() == 0 || o.getPickerId() == getCurUserId()) {
                                o.setStatus(PICKING);
                                o.setPickerId(getCurUserId());
                                o.setStartPickTime(new Date());
                                orders.add(o);
                                orderService.update(o);
                            }
                        }
                    }
                    pageParam.setCurNo(pageParam.getCurNo() + 1);
                } while (pager.getPage().getCurNo() <= pager.getPage().getTotalPage());
            }

            if (orders.isEmpty()) {
                renderText("没有可导出的单据或单据已被其他用户导出");
            } else {
                setAttr("from", from);
                setAttr("end", end);
                setAttr("items", orders);
                excelRender("pick.xls", "快递拣货单" + from + "-" + end + ".xls");
            }
        }
    }

    @ActionKey(method = RequestMethod.POST)
    @Tx
    public void picked() {
        String code = getPara("code");
        orderService.picked(code, getCurUser());
        ok("操作成功");
    }

    @Tx
    public void batchPick() throws UnsupportedEncodingException, ParseException {
        synchronized (PickMyTodayController.class) {
            String codes = getPara("codes");
            String redirectCode;
            //选择订单的时候
            if (StrKit.notBlank(codes)) {
                String[] codeList = codes.split(",");
                List<Integer> ids = new ArrayList<>();
                for (String code : codeList) {
                    ids.add(Trade.code2id(code));
                }
                List<Trade> trades = orderService.getTradesByIds(ids);
                PickOrder pickOrder = pickOrderService.createPickOrder(getCurUserId());
                redirectCode = pickOrder.getCode();
                for (Trade trade : trades) {
                    List<Order> orders = orderService.getOrdersByTradeId(trade.getId(), OrderStatus.WAIT_PICKING);      // S 待拣货对退款退货处理
                    for (Order order : orders) {
                        doPick(order, pickOrder);
                    }
                    trade.setPickOrderCode(pickOrder.getCode());
                    trade.setStatus(OrderStatus.PICKING);
                    orderService.updateTrade(trade);
                }
            } else {
                //什么都没选的时候
                String brand = getPara("brand");
                String goodsItemNo = getPara("itemNo");
                String goodsName = getPara("goodsName");
                String tradeCode = getPara("tradeCode");
                String orderCode = getPara("orderCode");
                String buyerName = getPara("buyerName");
                String sellerName = getPara("sellerName");
                int expressId = getParaToInt("expressId", 0);
                String strFrom = getPara("createdFrom");
                String strEnd = getPara("createdEnd");
                Date createdFrom = null;
                Date createdEnd = null;
                if(StrKit.notBlank(strFrom)){
                    createdFrom = DateUtil.parse(strFrom + ":00");
                }
                if(StrKit.notBlank(strEnd)){
                    createdEnd = DateUtil.parse(strEnd + ":59");
                }

                //获取待拣货的总订单
                PageList<Trade> pager = orderService.findWaitPickTrades(createdFrom, createdEnd, brand, goodsItemNo,
                        goodsName, tradeCode, orderCode, buyerName, sellerName, expressId, getPageParam());
                Collection<Integer> tradeIds = Collections2.transform(pager.getData(), Trade.ID_VALUE);
                //根据总订单获取子订单
                List<Order> orders = orderService.getOrdersByTradeIds(tradeIds);
                Multimap<Integer, Order> orderMultimap = Multimaps.index(orders, Order.tradeIdValue);

                PickOrder pickOrder = pickOrderService.createPickOrder(getCurUserId());
                redirectCode = pickOrder.getCode();
                for (Trade trade : pager.getData()) {
                    trade.setOrders(orderMultimap.get(trade.getId()));
                    for (Order order : trade.getOrders()) {
                        if (order.getStatus().equals(OrderStatus.WAIT_PICKING)) {
                            doPick(order, pickOrder);
                        }
                    }
                    trade.setPickOrderCode(pickOrder.getCode());
                    trade.setStatus(OrderStatus.PICKING);
                    orderService.updateTrade(trade);

                }
            }
            redirect("/admin/picklist/picking/pickOrder?pickCode=" + redirectCode);
        }
    }

    private void doPick(Order order, PickOrder pickOrder) {
        order.setPickerId(super.getCurUser().getId());
        order.setStartPickTime(new Date());
        order.setPickOrderCode(pickOrder.getCode());
        order.setStatus(OrderStatus.PICKING);
        orderService.update(order);
    }

    public void checkPickOrder() throws UnsupportedEncodingException, ParseException {
        synchronized (PickMyTodayController.class) {
            String codes = getPara("codes");
            if(StrKit.notBlank(codes)){
                String[] codeList = codes.split(",");
                List<Integer> ids = new ArrayList<>();
                for(String code : codeList){
                    ids.add(Trade.code2id(code));
                }
                List<Trade> trades = orderService.getTradesByIds(ids);
                for(Trade trade : trades){
                    if(StrKit.notBlank(trade.getPickOrderCode())){
                        error("有订单已经生成拣货单，请刷新后重试");
                        return;
                    }
                }
            }else {
                String brand = getPara("brand");
                String goodsItemNo = getPara("itemNo");
                String goodsName = getPara("goodsName");
                String tradeCode = getPara("tradeCode");
                String orderCode = getPara("orderCode");
                String buyerName = getPara("buyerName");
                String sellerName = getPara("sellerName");
                int expressId = getParaToInt("expressId", 0);
                String strFrom = getPara("createdFrom");
                String strEnd = getPara("createdEnd");
                Date createdFrom = null;
                Date createdEnd = null;
                if(StrKit.notBlank(strFrom)){
                    createdFrom = DateUtil.parse(strFrom + ":00");
                }
                if(StrKit.notBlank(strEnd)){
                    createdEnd = DateUtil.parse(strEnd + ":59");
                }

                List<OrderStatus> statuses = new ArrayList<>();
                PageList<Trade> pager = orderService.findWaitPickTrades(createdFrom, createdEnd, brand, goodsItemNo,
                        goodsName, tradeCode, orderCode, buyerName, sellerName, expressId, getPageParam());

                if (pager.getData().size() == 0) {
                    error("无可拣货订单，请刷新后重试");
                    return;
                }
            }
            ok("");
        }
    }

    public void detail() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        String spec = order.getSpecPropValue();
        if(spec.length() > 0) {
            spec = spec.substring(0, spec.length() - 1);
            spec = spec.replace(";", "<br />");
        }
        order.setSpecPropValue(spec);
        Goods goods = goodsService.getByCode(order.getGoodsCode());
        order.setHeadImg(goods.getHeadImgUrl1());
        setAttr("order", order);
        SendOrder sendOrder = orderService.getSendOrderById(order.getSendOrderId());
        setAttr("sendOrder", sendOrder);


        Trade trade = orderService.getTrade(order.getTradeId());
        setAttr("trade", trade);
        setAttr("province", provinceDao.getById(trade.getProvinceId()));

        Customer seller = customerService.getById(order.getSellerId());
        setAttr("seller", seller);
        Customer buyer = customerService.getById(order.getBuyerId());
        setAttr("buyer", buyer);
    }
}
