package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.controller.web.buyerDispute.BuyerDisputeAssist;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.model.BuyerDispute;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.refund.BuyerDisputeService;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Controller
@Scope("prototype")
public class RefundController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Value("${sms.refundDeliverMsg}")
    private String refundDeliverMsg;

    @Value("${sms.refundDealMsg}")
    private String refundDealMsg;

    @Value("${sms.refundRefuse}")
    private String refundRefuse;

    @Value("${sms.refundMoneyRefuse}")
    private String refundMoneyRefuse;

    @Value("${sms.refundAgree}")
    private String refundAgree;

    @Autowired
    private CustomerService customerService;

    public void index() {
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String code = "";
        String goodsCode = "";
        String goodsItemNo = "";
        String sellerPhone = "";
        String buyerPhone = "";
        if (StrKit.notBlank(value)) {
            if ("orderCode".equals(fieldName)) {
                code = value;
            }
            if ("goodsCode".equals(fieldName)) {
                goodsCode = value;
            }
            if ("goodsItemNo".equals(fieldName)) {
                goodsItemNo = value;
            }
            if ("sellerPhone".equals(fieldName)) {
                sellerPhone = value;
            }
            if ("buyerPhone".equals(fieldName)) {
                buyerPhone = value;
            }
        }

        List<OrderStatus> statusList = Arrays.asList(APPLY_REFUND_GOODS, REFUND_GOODS_REFUSE, REFUND_GOODS_DELIVERY,
                REFUND_GOODS_DELIVERY_ING, REFUND_GOODS_DELIVERY_ING, FULL_REFUND_GOODS, REFUND_GOODS_DISCUSS,
                CLOSED_BY_REFUND_GOODS, APPLY_REFUND, REFUND_REFUSE, REFUND_CONFIRM, CLOSED_BY_REFUND);

        PageList<Order> pager = orderService
                .findOrders(code, goodsCode, 0, goodsItemNo, sellerPhone, buyerPhone, statusList, getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    public void toDeal() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        SendOrder sendOrder = orderService.getSendOrderById(order.getSendOrderId());
        setAttr("order", order);
        setAttr("sendOrder", sendOrder);

        String refundImg = order.getRefundDisputeImg();
        setAttr("imgList", BuyerDisputeAssist.getImgCroupList(refundImg));

        Trade trade = orderService.getTrade(order.getTradeId());
        setAttr("trade", trade);

        setAttr("refundReason", BuyerDisputeAssist.getReasonOrder(order));
        setAttr("refundRemark", BuyerDisputeAssist.getRemarkOrder(order));

        render("deal.html");
    }

    /**
     * 特殊订单处理
     */
    @ActionKey("tishudingdanchuli.html")
    public void tishudingdanchuli(){render("tishudingdanchuli.html");}

    /**
     * 延迟退款退货申请页
     */
    @ActionKey("yanchitshenqing.html")
    public void yanchitshenqing(){render("yanchitshenqing.html");}

    @ActionKey(method = RequestMethod.POST)
    public void deal() {
        String code = getPara("code");
        String refundDeal = getPara("refundDeal");
        String disagreeReason = getPara("disagreeReason");
        String negotiationResult = getPara("negotiationResult");

        Long platformMoney = (long) (getParaToDouble("platformMoney", 0d) * 100);
        Long supplierMoney = (long) (getParaToDouble("supplierMoney", 0d) * 100);
        Long expressMoney = (long) (getParaToDouble("expressMoney", 0d) * 100);
        Long stockMoney = (long) (getParaToDouble("stockMoney", 0d) * 100);

        Long refundFee = (long) (getParaToDouble("refundFee", 0d) * 100);

        Order order = orderService.getOrderByCode(code);
        if (StrKit.isBlank(refundDeal) || refundDeal.equals(order.getRefundDeal())) {
            error("请选择退货处理意见");
            return;
        }
        OrderStatus status = null;
        if ("拒绝退货".equals(refundDeal)) {
            status = REFUND_GOODS_REFUSE;
            if (StrKit.isBlank(disagreeReason)) {
                error("请填写拒绝退货理由");
                return;
            }
        } else if ("拒绝退款".equals(refundDeal)) {
            status = REFUND_REFUSE;
            if (StrKit.isBlank(disagreeReason)) {
                error("请填写拒绝退款理由");
                return;
            }
        } else if ("退货发回".equals(refundDeal)) {
            status = REFUND_GOODS_DELIVERY;
        } else if ("全额退款".equals(refundDeal)) {
            order.setCancelRefundFlag(1);
            status = FULL_REFUND_GOODS;
        } else if ("退货协商".equals(refundDeal)) {
            status = REFUND_GOODS_DISCUSS;
            if (StrKit.isBlank(negotiationResult) || refundFee <= 0) {
                error("请填写协商退款处理结果与金额");
                return;
            }
        } else if ("同意退款".equals(refundDeal)) {
            order.setCancelRefundFlag(1);
            status = REFUND_CONFIRM;
        }
        if (status == null) {
            error("请选择退款/退货处理意见");
            return;
        }
        order.setRefundDeal(refundDeal);
        if (status.equals(REFUND_GOODS_REFUSE) || status.equals(REFUND_REFUSE)) {
            order.setDisagreeRefundReason(disagreeReason);
        }
        order.setStatus(status);

        Trade trade = orderService.getTrade(order.getTradeId());
        if (status.equals(REFUND_GOODS_DISCUSS)) {
            order.setRefundFee(refundFee);
            order.setNegotiationResult(negotiationResult);
        } else if (FULL_REFUND_GOODS.equals(status)) {
            //最大退款金额=总金额-订单中优惠的金额*（需退款商品原价/订单原价）
            if (trade.getCouponId() > 0 && trade.getUseType().equals(UseType.PAYMENT)) {
                order.setRefundFee(order.getPayment() - (trade.getCouponFee() * order.getMoney() / trade.getTotalPayment()));
            } else {
                order.setRefundFee(order.getPayment());
            }
        }
        order.setRefundUserId(getCurUserId());

        if (REFUND_GOODS_DISCUSS.equals(status) || FULL_REFUND_GOODS.equals(status) || REFUND_CONFIRM.equals(status)) {
            trade.setRefundNum(trade.getRefundNum() + 1);
            if (trade.getRefundNum() == trade.getOrderCnt()) {
                trade.setStatus(status);
            }

            // 退货退款责任方 金额分配 升级 2016-10-11
            if (platformMoney == 0 && supplierMoney == 0 && expressMoney == 0 && stockMoney == 0) {
                error("请分配退货退款责任方金额！");
                return;
            }
            long sum = platformMoney + supplierMoney + expressMoney + stockMoney;
            if (status.equals(FULL_REFUND_GOODS)) {     // 全额退款
                if (order.getRefundFee() != sum) {
                    error("您填写的退货退款责任方金额总合不对！");
                    return;
                }
            }
            if (status.equals(REFUND_GOODS_DISCUSS)) {   // 退货协商
                if (refundFee != sum) {
                    error("您填写的退货退款责任方金额总合不对！");
                    return;
                }
            }
            if (status.equals(REFUND_CONFIRM)) {    // 同意退款
                if (order.getRefundFee() != sum) {
                    error("您填写的退货退款责任方金额总合不对！");
                    return;
                }
            }
            order.setPlatformMoney(platformMoney);
            order.setSupplierMoney(supplierMoney);
            order.setExpressMoney(expressMoney);
            order.setStockMoney(stockMoney);

            order.setRefundConfirmTime(new Date());
            orderService.updateTrade(trade);
        }

        order.setAfterNote(getPara("afterNote"));
        orderService.dealRefundApply(order, getCurUserId());

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(code);
        buyerDispute.setAdminId(getCurUser().getId());
        buyerDispute.setAdminName(getCurUser().getName());
        buyerDispute.setRefundStatusCode(order.getStatus());
        buyerDispute.setReason(disagreeReason);
        buyerDisputeService.save(buyerDispute);

        Customer customer = customerService.getById(order.getBuyerId());
        String msg;
        if ("退货发回".equals(refundDeal)) {
            msg = TemplateUtils.parseText(refundDeliverMsg, ImmutableMap.<String, Object>of("code", order.getCode()));
        } else if ("拒绝退货".equals(refundDeal)) {
            msg = TemplateUtils.parseText(refundRefuse,
                    ImmutableMap.<String, Object>of("code", order.getCode(), "reason", disagreeReason));
        } else {
            msg = TemplateUtils.parseText(refundDealMsg,
                    ImmutableMap.<String, Object>of("code", order.getCode(), "result", refundDeal));
        }
        SMSUtil.sendSmsMsg2(customer.getPhone(), msg);
        ok("操作成功");
    }
    @Autowired
    private BuyerDisputeService buyerDisputeService;
}