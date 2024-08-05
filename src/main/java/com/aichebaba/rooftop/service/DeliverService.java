package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.CustomerDao;
import com.aichebaba.rooftop.dao.OrderDao;
import com.aichebaba.rooftop.dao.SendOrderDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 发货相关服务
 */
@Service
public class DeliverService {

    private static Logger logger = LoggerFactory.getLogger(DeliverService.class);

    @Value("${sms.tradeDeliverMsg}")
    private String tradeDeliverMsg;

    @Autowired
    private SendOrderDao sendOrderDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    public PageList<SendOrder> findSendOrders(String code, int sendUserId, SendOrderStatus status, Boolean isSend,
                                              PageParam pageParam) {
        return sendOrderDao.findSendOrders(code, sendUserId, status, isSend, pageParam);
    }
    public PageList<SendOrder> findOldSendOrders(String tradeCode, Date fromTime, Date endTime, String sendCode, int expressId, String expressCode,
                                              PageParam pageParam) {
        int tradeId = 0;
        if (StrKit.notBlank(tradeCode)) {
            try {
                tradeId = Integer.parseInt(tradeCode.toUpperCase().replace(Trade.mark, ""));
            } catch (Exception e) {
                tradeId = -1;
            }
        }
        return sendOrderDao.findSendOrders2(tradeId, fromTime, endTime, sendCode, expressId, expressCode, true, pageParam);
    }
    public List<SendOrder> findSendOrderList(String code, List<String> codes, Date startTime, Date endTime, int sendUserId, SendOrderStatus status, Boolean isSend, int expressId) {
        return sendOrderDao.findSendOrderList(code, codes, startTime, endTime, sendUserId, status, isSend, expressId);
    }

    public SendOrder getSendOrderById(int id) {
        return sendOrderDao.getByPK(id);
    }

    public void updateExpressCode(int id, int expressId, String expressName, String expressCode) {
        SendOrder sendOrder = sendOrderDao.getByPK(id);
        if (sendOrder != null) {
            Trade trade = orderService.getTrade(sendOrder.getTradeId());
            trade.setStatus(OrderStatus.WAIT_DELIVERY);
            trade.setExpressId(expressId);
            trade.setExpressName(expressName);
            trade.setExpressCode(expressCode);
            List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
            for (Order o : orders) {
                o.setStatus(OrderStatus.WAIT_DELIVERY);
                o.setWaitDeliverTime(new Date());
            }
            orderService.updateOrdersStatus(orders);
            orderService.updateTrade(trade);
            sendOrder.setExpressId(expressId);
            sendOrder.setExpressCode(expressCode);
            sendOrder.setExpressName(expressName);
            sendOrder.setStatus(SendOrderStatus.WAIT_SEND);
            sendOrderDao.update(sendOrder);
        }
    }

    public void takeOrder(int id, User curUser) {
        SendOrder order = getSendOrderById(id);
        if (order != null) {
            order.setSendUserId(curUser.getId());
            sendOrderDao.update(order);
            logger.info("Send Order: user {} take order  {}", curUser.getId(), id);
        }
    }

    public void updateSendOrderPickStatus(int sendOrderId) {
        SendOrder sendOrder = getSendOrderById(sendOrderId);
        List<Order> orders = orderDao.getOrdersBySendOrderId(sendOrderId);
        boolean waitDeliver = true;
        for (Order order : orders) {
            if (!order.getStatus().equals(OrderStatus.PICK_FINISHED)) {
                waitDeliver = false;
                break;
            }
        }
        Trade trade = orderService.getTrade(sendOrder.getTradeId());
        if (waitDeliver) {
            sendOrder.setStatus(SendOrderStatus.PICK_FINISHED);
            sendOrderDao.update(sendOrder);
            trade.setStatus(OrderStatus.PICK_FINISHED);
            orderService.updateTrade(trade);
        }
    }

    public void sendOrder(int id, int expressId, String expressName, String code, User curUser) {
        SendOrder order = getSendOrderById(id);
        if (order != null) {
            order.setExpressId(expressId);
            order.setExpressName(expressName);
            order.setExpressCode(code);
            order.setSendUserId(curUser.getId());
            order.setIsSend(true);
            order.setStatus(SendOrderStatus.FINISHED);
            order.setSendTime(new Date());
            sendOrderDao.update(order);
            List<Order> orders = orderService.getOrdersByTradeId(order.getTradeId());
            Customer customer = customerDao.getById(order.getCustomerId());
            Trade trade = orderService.getTrade(order.getTradeId());
            trade.setExpressCode(code);
            trade.setExpressName(order.getExpressName());
            trade.setStatus(OrderStatus.WAIT_RECEIVE);
            trade.setConsignTime(new Date());
            orderService.updateTrade(trade);
            for (Order o : orders) {
                o.setStatus(OrderStatus.WAIT_RECEIVE);
                String msg = TemplateUtils.parseText(tradeDeliverMsg, ImmutableMap
                        .<String, Object>of("code", o.getCode(), "goodsName", o.getGoodsName(), "expressName",
                                expressName, "expressCode", code));
                SMSUtil.sendSmsMsg2(customer.getPhone(), msg);
            }
            orderService.updateOrdersStatus(orders);
            logger.info("Send Order: user {} deliver {}", curUser.getId(), id);
        }
    }

    public void updateSendOrder(SendOrder sendOrder) {
        sendOrderDao.update(sendOrder);
    }
}
