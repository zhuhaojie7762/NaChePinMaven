package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ExpressCompanyDao;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.*;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.service.refund.RefundDetectorService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import com.sun.org.apache.xpath.internal.operations.Or;
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
public class PickMyToday2Controller extends BaseController {

    @Autowired
    private PickOrderService pickOrderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private DeliverService deliverService;

    public void index() throws ParseException {
        PageList<PickOrder> pageList = pickOrderService.findPickOrder(getPageParam());
        setAttr("pager", pageList);

        render("today_list2.html");
    }

    // 拣货流程开始！

    /**
     * 拣货导出excel页面载入    10
     */
    public void pickOrder() {
        String pickCode = getPara("pickCode");
        String expressId = getPara("expressId", "0");

        // 在配货单中查询相关的快递公司
        List<Trade> trades = getTrades(pickCode);
        if (!expressId.equals("0")) {
            for (Iterator it = trades.iterator(); it.hasNext();) {
                Trade trade = (Trade) it.next();
                if (Integer.parseInt(expressId) != trade.getSendOrder().getExpressId()) {
                    it.remove();
                }
            }
        }
        // 返回查询者用的快递公司信息
        ExpressCompany expressInfo = expressCompanyService.getExpressCompanyById(Integer.parseInt(expressId));

        List<ExpressCompany> expressLists = getExpressCompany();

        setAttr("expressInfo", expressInfo);
        setAttr("expressLists", expressLists);
        setAttr("pickCode", pickCode);
        setAttr("trades", trades);
        render("toContinue.html");
    }

    /**
     * 填写快递单号页面载入   20
     */
    public void kuaiDiOrder() {
        String pickCode = getPara("pickCode");
        String expressId = getPara("expressId", "0");

        // 在配货单中查询相关的快递公司
        List<Trade> trades = getTrades(pickCode);
        if (!expressId.equals("0")) {
            for (Iterator it = trades.iterator(); it.hasNext();) {
                Trade trade = (Trade) it.next();
                if (Integer.parseInt(expressId) != trade.getSendOrder().getExpressId()) {
                    it.remove();
                }
            }
        }
        // 返回查询者用的快递公司信息
        ExpressCompany expressInfo = expressCompanyService.getExpressCompanyById(Integer.parseInt(expressId));

        List<ExpressCompany> expressLists = getExpressCompany();

        changeStatus(pickCode, PickOrderStatus.FILL_EXPRESS);

        setAttr("expressInfo", expressInfo);
        setAttr("expressLists", expressLists);
        setAttr("pickCode", pickCode);
        setAttr("trades", trades);
        render("kuaiDiMianDan.html");
    }

    /**
     * 下一步判断检查
     */
    public void examine() {
        String pickCode = getPara("pickCode");
        List<Trade> trades = getTrades(pickCode);
        for (Trade trade : trades) {
            if (StrKit.isBlank(trade.getExpressCode())) {
                error("亲，您有订单未完快递单号填写，请点重置按钮在批量填写快递单号里面进行填写！");
                return;
            }
        }
        ok("");
    }

    /**
     * 打印拣货单页面载入    30
     */
    public void printLoading() {
        String pickCode = getPara("pickCode");

        List<Trade> trades = getTrades(pickCode);
        changeStatus(pickCode, PickOrderStatus.PRINT_PICKING);

        setAttr("trades", trades);
        setAttr("pickCode", pickCode);
        render("printPage.html");
    }

    /**
     * 发货单页面载入  40
     */
    public void sendOrder() {
        String pickCode = getPara("pickCode");
        String tradeCode = getPara("tradeCode");

        List<ExpressCompany> expressLists = getExpressCompany();

        List<Trade> trades = new ArrayList<>();
        if(StrKit.notBlank(tradeCode)) {
            Trade trade = orderService.getTrade(Trade.code2id(tradeCode.toUpperCase()));
            if (trade != null) {
                List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
                for (Order order : orders) {
                    setOrderInfo(order);
                }
                trade.setOrders(orders);

                trades = new ArrayList<>();
                trades.add(trade);
                // 移除配货未完成订单
                for (Iterator it = trades.iterator(); it.hasNext();) {
                    Trade traded = (Trade) it.next();
                    if(traded.getAllocatingStatus() == AllocatingStatus.unfinished) {
                        it.remove();
                    }
                }
            }
            setAttr("setTradeCode", tradeCode.toUpperCase());
        } else {
            trades = getTrades(pickCode);
            // 配货单里面全部总订单都发货完才修改状态
            List<Order> orderList = orderService.findPickOrderCode(pickCode);
            int size = 0;
            for (Order order : orderList) {
                if(order.getStatus() == OrderStatus.WAIT_RECEIVE || RefundDetectorService.doInclude(order)){
                    ++size;
                }
            }
            if (orderList.size() != size) {
                changeStatus(pickCode, PickOrderStatus.DISTRIBUTION);
            }
        }

        setAttr("trades", trades);
        setAttr("pickCode", pickCode);
        setAttr("expressLists", expressLists);
        render("confirmPage.html");
    }

    /**
     * 拣货备注信息保存
     */
    public void remarkSave() {
        String remark = getPara("remark");
        String tradeCode = getPara("tradeCode");
        if (remark.length() > 50) {
            error("备注字数不能超过50个字！");
        }else {
            Trade trade = orderService.getTrade(Trade.code2id(tradeCode));
            trade.setRemark(remark);
            orderService.updateTrade(trade);
            ok("备注保存成功!");
        }
    }

    /**
     * 实际重量保存
     */
    public void weightSave() {
        String tradeCode = getPara("tradeCode");
        double realityWeight = getParaToDouble("realityWeight", 0.00);
        double temp = realityWeight * 1000;
        int rw = (int) temp;

        Trade trade = orderService.getTrade(Trade.code2id(tradeCode));
        trade.setRealityWeight(rw);
        orderService.updateTrade(trade);

        SendOrder sendOrder = orderService.getSendOrderByTradeId(trade.getId());
        sendOrder.setRealityWeight(rw);
        deliverService.updateSendOrder(sendOrder);

        ok("提交完成!");
    }

    /**
     * 批量填写快递号载入
     */
    public void picksLoading() {
        String codes = getPara("codes");
        List<Integer> ids = new ArrayList<>();
        for (String code : codes.split(",")) {
            ids.add(Trade.code2id(code));
        }

        List<Trade> trades = orderService.getTradesByIds(ids);

        for (Trade trade : trades) {
            trade.setSendOrder(orderService.getSendOrderByTradeId(trade.getId()));
            trade.setOrders(orderService.getOrdersByTradeId(trade.getId()));
        }

        List<ExpressCompany> expressLists = getExpressCompany();

        setAttr("trades", trades);
        setAttr("expressLists", expressLists);
        String htmlPath = TemplateUtils.html("/admin/picklist/picksLoading.html", getRequest());
        ok("", htmlPath);
    }

    /**
     * 快递单号保存
     */
    public void expressSave() {
        String[] tradeCodes = getParaValues("tradeCode");
        String[] expressIds = getParaValues("expressId");
        String[] expressNums = getParaValues("expressNum");

        for (int i = 0; i < tradeCodes.length; i++) {
            if (expressIds[i].equals("0")) {
                error("亲，请选择快递公司！");
                return;
            }
            /*
            if (StrKit.isBlank(expressNums[i])) {
                error("亲，请填写快递单号！");
                return;
            }
            */
        }

        for (int i = 0; i < tradeCodes.length; i++) {
            ExpressCompany express = expressCompanyService.getExpressCompanyById(Integer.parseInt(expressIds[i]));
            Trade trade = orderService.getTrade(Trade.code2id(tradeCodes[i]));
            trade.setExpressId(express.getId());
            trade.setExpressName(express.getName());
            trade.setExpressCode(expressNums[i]);
            orderService.updateTrade(trade);

            SendOrder sendOrder = orderService.getSendOrderByTradeId(trade.getId());
            sendOrder.setExpressId(express.getId());
            sendOrder.setExpressName(express.getName());
            sendOrder.setExpressCode(expressNums[i]);
            deliverService.updateSendOrder(sendOrder);

        }

        ok("快递单号保存成功！");
    }

    /**
     * 发货处理
     */
    public void deliverGoods() {
        String  pickCode = getPara("pickCode");
        PickOrder pickOrder = pickOrderService.getByCode(pickCode);
        if(pickOrder != null && pickOrder.getStatus().equals(PickOrderStatus.FINISH)){
            error("该拣货单已发货，请刷新列表页");
            return;
        }
        String tradeCodes = getPara("tradeCode");
        List<Integer> ids = new ArrayList<>();
        for (String code : tradeCodes.split(",")) {
            ids.add(Trade.code2id(code));
        }

        List<Trade> trades = orderService.getTradesByIds(ids);
        for (Trade trade : trades) {
            if (trade.getStatus().getValue() < OrderStatus.WAIT_RECEIVE.getValue()) {
                SendOrder sendOrder = orderService.getSendOrderByTradeId(trade.getId());
                sendOrder.setStatus(SendOrderStatus.FINISHED);
                sendOrder.setSendTime(new Date());
                deliverService.updateSendOrder(sendOrder);

                List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
                for (Iterator<Order> it = orders.iterator(); it.hasNext();) {
                    Order order = it.next();
                    if (RefundDetectorService.doInclude(order)) {
                        it.remove();
                    }
                }
                for (Order order : orders) {
                    order.setStatus(OrderStatus.WAIT_RECEIVE);
                }
                orderService.updateOrdersStatus(orders);

                trade.setConsignTime(new Date());
                trade.setStatus(OrderStatus.WAIT_RECEIVE);
                orderService.updateTrade(trade);
            }
        }
        // 配货单里面全部总订单都发货完才修改状态
        List<Order> orderList = orderService.findPickOrderCode(pickCode);
        int size = 0;
        for (Order order : orderList) {
            if(order.getStatus() == OrderStatus.WAIT_RECEIVE || RefundDetectorService.doInclude(order)){
                ++size;
            }
        }
        if (orderList.size() == size) {
            changeStatus(pickCode, PickOrderStatus.FINISH);
        }

        ok("发货完成！");
    }

    /**
     * 配货未完成处理
     */
    public void doAllocating() {
        String tradeCode = getPara("tradeCode");

        Trade trade = orderService.getTrade(Trade.code2id(tradeCode));
        trade.setAllocatingStatus(AllocatingStatus.unfinished);
        orderService.updateTrade(trade);

        ok("已下发到未完成订单！");
    }

    /**
     * 获取总订单信息列表
     * @param pickCode
     * @return
     */
    private List<Trade> getTrades(String pickCode) {
        List<Order> orderList = refundDetectorService.detect(pickCode);         // 拣货订单监视退款处理
        Multimap<Integer, Order> orderMultimap = Multimaps.index(orderList, Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());
        // 移除配货未完成订单
        for (Iterator it = trades.iterator(); it.hasNext();) {
            Trade trade = (Trade) it.next();
            if(trade.getAllocatingStatus() == AllocatingStatus.unfinished) {
                it.remove();
            }
        }
        for (Trade trade : trades) {
            trade.setSendOrder(orderService.getSendOrderByTradeId(trade.getId()));
            trade.setOrders(orderMultimap.get(trade.getId()));
            for (Order order : trade.getOrders()) {
                setOrderInfo(order);
            }
        }
        return trades;
    }

    /**
     * 补齐Order对象中的值
     */
    private void setOrderInfo(Order order) {
        Goods goods = goodsService.getByCode(order.getGoodsCode());
        if (goods != null) {
            order.setHeadImg(goods.getHeadImgUrl1());
            order.setBrand(goods.getBrand());
        }
        Customer seller = customerService.getById(order.getSellerId());
        if (seller != null) {
            order.setSellerName(seller.getName());
            order.setPickAddress(seller.getPickAddress());
        }
    }

    /**
     * 更改配货单状态
     * @param pickCode
     * @param status
     */
    private void changeStatus(String pickCode, PickOrderStatus status) {
        PickOrder pickOrder = pickOrderService.getByCode(pickCode);
        pickOrder.setStatus(status);
        pickOrder.setLastTime(new Date());
        pickOrderService.updatePickOrder(pickOrder);
    }

    /**
     * 获取快递公司信息
     */
    private List<ExpressCompany> getExpressCompany() {
        return expressCompanyService.getAllExpressCompanies();
    }

    // 拣货流程结束！

    /**
     * excel 导出处理
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Tx
    public void export() throws UnsupportedEncodingException, ParseException {
        synchronized (PickMyToday2Controller.class) {
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
                // 总订单 code 转成 Order 订单
                List<String> codeList = Lists.newArrayList(Splitter.on(',').split(codes));
                List<Integer> tradeIds = new ArrayList<>();
                for (String codeStr : codeList) {
                    tradeIds.add(Trade.code2id(codeStr));
                }
                List<Order> orderList = orderService.getOrdersByTradeIds(tradeIds);
                List<String> orderCodes = new ArrayList<>();
                for (Order order : orderList) {
                    orderCodes.add(order.getCode());
                }

                List<Order> list = orderService.getOrderByCodes(orderCodes);
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

    public void detail() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        SendOrder sendOrder = orderService.getSendOrderById(order.getSendOrderId());
        setAttr("order", order);
        Trade trade = orderService.getTrade(order.getTradeId());
        setAttr("trade", trade);
        setAttr("province", provinceDao.getById(trade.getProvinceId()));
        setAttr("sendOrder", sendOrder);
    }

    @Autowired
    private RefundDetectorService refundDetectorService;
}
