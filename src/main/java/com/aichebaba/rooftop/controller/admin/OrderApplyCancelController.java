package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Tx;
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
public class OrderApplyCancelController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Value("${sms.refundMoneyRefuse}")
    private String refundMoneyRefuse;

    @Value("${sms.refundAgree}")
    private String refundAgree;

    @Autowired
    private CustomerService customerService;

    public void index() {
        String buyerName = getPara("buyerName");
        PageList<Trade> pager = orderService.findTrades(buyerName, Arrays.asList(WAIT_AGREE_CANCEL, WAIT_REFUND_BY_CANCEL,
                CLOSED_BY_CANCEL), getPageParam());
        for (Trade trade : pager.getData()) {
            trade.setOrders(orderService.getOrdersByTradeId(trade.getId()));
        }
        setAttr("pager", pager);
        render("list.html");
    }

    /**
     * @创建时间： 2016年4月3日
     * @相关参数：
     * @功能描述： 退款详情
     */
    public void toDeal() {
        int tid = getParaToInt("tid");
        Trade trade = orderService.getTrade(tid);
        List<Order> orders = orderService.getOrdersByTradeId(tid);
        SendOrder sendOrder = orderService.getSendOrderByTradeId(tid);
        setAttr("trade", trade);
        setAttr("orders", orders);
        setAttr("sendOrder", sendOrder);
        render("deal.html");
    }

    /**
     * @创建时间： 2016年4月3日
     * @相关参数：
     * @功能描述： 退款确认
     */
    @ActionKey(method = RequestMethod.POST)
    @Tx
    public void deal() {
        int tradeId = getParaToInt("tradeId", 0);
        String refundDeal = getPara("refundDeal");
        String disagreeRefundReason = getPara("disagreeRefundReason");
        if (StrKit.isBlank(refundDeal)) {
            error("请选择处理结果");
            return;
        }
        OrderStatus status;
        Trade trade = orderService.getTrade(tradeId);
        List<Order> orders = orderService.getOrdersByTradeId(tradeId);
        if ("同意".equals(refundDeal)) {
            status = WAIT_REFUND_BY_CANCEL;
        } else {
            status = WAIT_PICKING;
            if (StringUtils.isBlank(disagreeRefundReason)) {
                error("请填写拒绝理由");
                return;
            }
        }
        trade.setStatus(status);
        StringBuilder orderCodes = new StringBuilder();
        long refundMoney = 0;
        for (Order order : orders) {
            order.setStatus(status);
            if (status == WAIT_PICKING) {
                order.setDisagreeRefundReason(disagreeRefundReason);
            } else {
                order.setRefundConfirmTime(new Date());
            }
            order.setRefundDeal(refundDeal);
            orderCodes.append(order.getCode()).append(" ");
            refundMoney += order.getRefundFee();
        }
        orderService.updateOrdersStatus(orders);
        orderService.updateTrade(trade);

        Customer customer = customerService.getById(trade.getBuyerId());
        String msg;
        if (status == WAIT_REFUND_BY_CANCEL) {
            msg = TemplateUtils.parseText(refundAgree, ImmutableMap.of("codes", orderCodes.toString(), "money", refundMoney / 100d));
        } else {
            msg = TemplateUtils.parseText(refundMoneyRefuse, ImmutableMap.of("codes", orderCodes.toString(), "reason", disagreeRefundReason));
        }
        SMSUtil.sendSmsMsg2(customer.getPhone(), msg);
        ok("操作成功");
    }
}
