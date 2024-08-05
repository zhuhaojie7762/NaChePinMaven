package com.aichebaba.rooftop.controller.web.dispute;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.BuyerDispute;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.refund.BuyerDisputeService;
import com.aichebaba.rooftop.utils.NaChePingHelper;
import com.jfinal.core.ActionKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

/**
 * Created by He daoyuan on 2016/11/8.
 */
@Controller
@Scope("prototype")
public class DisputeController extends BaseController {
    // 前往退货页面
    @ActionKey("refundGoods.html")
    public void goRefundGoods() {
        String code = getPara("code");
        refundLoad(code);
        render("refundGoods.html");
    }

    // 提交退货申请
    public void doRefundGoods() {
        String orderCode = getPara("orderCode");                    // order 编号
        String refundCause = getPara("refundCause");                // 退款退货原因
        Double refundAmount = getParaToDouble("refundAmount");      // 退款金额
        String refundExplain = getPara("refundExplain");            // 退款退货说明
        String[] imgs = getParaValues("headImgUrl");                // 退货退款纠纷5张图片url

        if ("请选择退款原因".equals(refundCause)) {
            error("请选择退款原因");
            return;
        }
        if (null == refundAmount || refundAmount < 0) {
            error("请输入退款金额");
            return;
        }
        if (refundExplain.length() > 200) {
            error("说明字数不能超过200个字");
            return;
        }

        Order order = orderService.getOrderByCode(orderCode);
        Trade trade = orderService.getTrade(order.getTradeId());
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        // 订单校验
        String checkOrder = RefundAssist.checkOrder(order);
        if (checkOrder != null) {
            error(checkOrder);
            return;
        }

        Long refundFee = (long) (refundAmount * 100);
        Long maxRefund = RefundAssist.getMaxRefund(trade, order, orders);
        if (refundFee > maxRefund) {
            error("不能超过最大退款金额");
            return;
        }

        order.setOrderStatus(order.getStatus());
        order.setCancelRefundFlag(0);
        order.setStatus(OrderStatus.APPLY_REFUND_GOODS);                // 已申请退货
        order.setApplyRefundFlag(OrderStatus.APPLY_REFUND_GOODS);
        order.setRefundDeal("");                                        // 退货退款处理方式
        order.setDisagreeRefundReason("");                              // 不同意理由
        order.setApplyRefundTime(new Date());                           // 退货退款申请时间
        order.setRefundFee(refundFee);                                  // 退款金额
        order.setRefundReason(refundCause + ":" + refundExplain);       // 退货退款理由
        RefundAssist.setImgGroup(order, imgs);                          // 退货退款纠纷图片，共存5张，以#号分隔
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(orderCode);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(OrderStatus.APPLY_REFUND_GOODS);
        buyerDispute.setGoodsStatus(1);
        buyerDispute.setReason(refundCause);
        buyerDispute.setMoney((long) (refundAmount * 100));
        buyerDispute.setRemark(refundExplain);
        buyerDisputeService.save(buyerDispute);

        ok("退货申请成功");
    }

    // 前往退款页面
    @ActionKey("refund.html")
    public void goRefund(){
        String code = getPara("code");
        refundLoad(code);
        render("refund.html");
    }

    // 提交退款申请
    public void doRefund() {
        String orderCode = getPara("orderCode");                    // order 编号
        Integer isCargo = getParaToInt("isCargo");                  // 是否已收到货
        String refundCause = getPara("refundCause");                // 退款退货原因
        Double refundAmount = getParaToDouble("refundAmount");      // 退款金额
        String refundExplain = getPara("refundExplain");            // 退款退货说明
        String[] imgs = getParaValues("headImgUrl");                // 退货退款纠纷5张图片url

        if ("请选择退款原因".equals(refundCause)) {
            error("请选择退款原因");
            return;
        }
        if (null == refundAmount || refundAmount < 0) {
            error("请输入退款金额");
            return;
        }
        if (refundExplain.length() > 200) {
            error("说明字数不能超过200个字");
            return;
        }

        Order order = orderService.getOrderByCode(orderCode);
        Trade trade = orderService.getTrade(order.getTradeId());
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        // 订单校验
        String checkOrder = RefundAssist.checkOrder(order);
        if (checkOrder != null) {
            error(checkOrder);
            return;
        }

        Long refundFee = (long) (refundAmount * 100);
        Long maxRefund = RefundAssist.getMaxRefund(trade, order, orders);
        if (refundFee > maxRefund) {
            error("不能超过最大退款金额");
            return;
        }

        order.setOrderStatus(order.getStatus());
        order.setCancelRefundFlag(0);
        order.setStatus(OrderStatus.APPLY_REFUND);                      // 已申请退款
        order.setApplyRefundFlag(OrderStatus.APPLY_REFUND);
        order.setIsCargo(isCargo);                                      // 是否已收到货
        order.setRefundDeal("");                                        // 退货退款处理方式
        order.setDisagreeRefundReason("");                              // 不同意理由
        order.setApplyRefundTime(new Date());                           // 退货退款申请时间
        order.setRefundFee(refundFee);                                  // 退款金额
        order.setRefundReason(refundCause + ":" + refundExplain);       // 退货退款理由
        RefundAssist.setImgGroup(order, imgs);                          // 退货退款纠纷图片，共存5张，以#号分隔
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(orderCode);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(OrderStatus.APPLY_REFUND);
        buyerDispute.setGoodsStatus(isCargo);
        buyerDispute.setReason(refundCause);
        buyerDispute.setMoney((long) (refundAmount * 100));
        buyerDispute.setRemark(refundExplain);
        buyerDisputeService.save(buyerDispute);

        ok("退款申请成功");
    }

    private void refundLoad(String code) {
        Order order = orderService.getOrderByCode(code);
        Trade trade = orderService.getTrade(order.getTradeId());
        Goods goods = goodsService.getByCode(order.getGoodsCode());

        // 退款金额明细
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        setAttr("detailPrice", RefundAssist.getDetailPrice(trade, order, orders));

        setAttr("trade", trade);
        setAttr("goods", goods);
        setAttr("order", order);
        setAttr("filePolicy", NaChePingHelper.policy("file"));
        setAttr("imagePolicy", NaChePingHelper.policy("image"));
    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BuyerDisputeService buyerDisputeService;
}
