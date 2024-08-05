package com.aichebaba.rooftop.controller.web.buyerDispute;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.BuyerDispute;
import com.aichebaba.rooftop.model.search.QueryTimeParam;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.refund.BuyerDisputeService;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

/**
 * Created by He DaoYuan on 2016/11/29.
 */
@Controller
@Scope("prototype")
public class BuyerDisputeController extends BaseController {
    @ActionKey("buyerDisputeList.html")
    public void buyerDisputeList() {
        Order order = new Order();
        order.setBuyerId(getCurCustomer().getId());                                                     // 进货商登录ID
        order.setTradeId(Trade.parserTradeCode(getPara("tradeId")));                                    // trade ID
        order.setCode(getPara("code"));                                                                 // order ID
        order.setGoodsItemNo(getPara("goods"));                                                         // 商品货号
        String status = getPara("status");                                                              // 订单状态
        order.setStatus(getVariable(StrKit.isBlank(status)? null : Integer.parseInt(status)));

        QueryTimeParam queryTime = new QueryTimeParam();
        queryTime.setStartCreated(getParaToDate("startCreated"));                                       // 下单时间
        queryTime.setEndCreated(getParaToDate("endCreated"));
        queryTime.setStartRefund(getParaToDate("startRefund"));                                         // 退款申请时间
        queryTime.setEndRefund(getParaToDate("endRefund"));

        List<OrderStatus> refundFlag = Arrays.asList(APPLY_REFUND_GOODS, APPLY_REFUND);
        PageList<Order> orders = orderService.gatherBuyerDispute(order, queryTime, refundFlag, getPageParam());
        for (Order ord : orders.getData()) {
            ord.setGoods(goodsService.getByCode(ord.getGoodsCode()));                                   // 存入商品信息
        }
        // 查询货号处理
        for (Iterator<Order> it = orders.getData().iterator(); it.hasNext();) {
            if (StrKit.isBlank(order.getGoodsItemNo())) {
                break;
            }
            if (!order.getGoodsItemNo().equals(it.next().getGoodsItemNo())) {
                it.remove();
            }
        }

        setAttr("orders", orders);
        render("buyerDisputeList.html");
    }

    public void trackingSave() {
        String code = getPara("code");
        String trackingName = getPara("trackingName");
        String trackingNumber = getPara("trackingNumber");

        Order order = orderService.getOrderByCode(code);
        order.setRefundExpressCompany(trackingName);
        order.setRefundExpressCode(trackingNumber);
        order.setRefundConsignTime(new Date());
        order.setStatus(OrderStatus.REFUND_GOODS_DELIVERY_ING);
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(code);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(order.getStatus());
        buyerDispute.setGoodsStatus(order.getIsCargo());
        buyerDispute.setReason(BuyerDisputeAssist.getReasonOrder(order));
        buyerDispute.setMoney(order.getRefundFee());
        buyerDispute.setRemark(BuyerDisputeAssist.getRemarkOrder(order));
        buyerDispute.setTrackingName(trackingName);
        buyerDispute.setTrackingNumber(trackingNumber);
        buyerDisputeService.save(buyerDispute);

        ok("物流信息提交成功");
    }

    public void cancel() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        if (order.getOrderStatus().getValue() < 50) {
            error("待收流程之前都不可取消退款退货申请");
            return;
        }
        order.setCancelRefundStatus(BuyerDisputeAssist.getRefundType(order));
        order.setStatus(order.getOrderStatus());
        order.setCancelRefundFlag(1);
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(code);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(BuyerDisputeAssist.getRefundType(order));
        //buyerDispute.setGoodsStatus(order.getIsCargo());
        //buyerDispute.setReason(BuyerDisputeAssist.getReasonOrder(order));
        //buyerDispute.setMoney(order.getRefundFee());
        //buyerDispute.setRemark(BuyerDisputeAssist.getRemarkOrder(order));
        buyerDisputeService.save(buyerDispute);

        ok("取消退款退货申请成功");
    }

    public void goBuyerDisputeDetail() {
        String code = getPara("code");

        Order order = orderService.getOrderByCode(code);
        Trade trade = orderService.getTrade(order.getTradeId());
        Goods goods = goodsService.getByCode(order.getGoodsCode());

        setAttr("trade", trade);
        setAttr("goods", goods);
        setAttr("order", order);

        setAttr("reason", BuyerDisputeAssist.splitReason(order));

        String refundImg = order.getRefundDisputeImg();
        setAttr("imgList", BuyerDisputeAssist.getImgCroupList(refundImg));

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(order.getCode());
        List<BuyerDispute> refundLogs = buyerDisputeService.findBuyerDispute(buyerDispute);
        setAttr("refundLogs", refundLogs);

        render("buyerDisputeDetail.html");
    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BuyerDisputeService buyerDisputeService;
}
