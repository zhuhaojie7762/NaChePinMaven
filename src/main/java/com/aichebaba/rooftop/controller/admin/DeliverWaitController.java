package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.service.DeliverService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class DeliverWaitController extends BaseController {

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private OrderService orderService;

    public void index() {
        PageList<SendOrder> pager = deliverService
                .findSendOrders(getPara("code"), 0, SendOrderStatus.WAIT_SEND, false, getPageParam());
        setAttr("pager", pager);
        render("wait_list.html");
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        SendOrder sendOrder = deliverService.getSendOrderById(id);
        setAttr("sendOrder", sendOrder);
        Date dayStart = DateUtil.getDayStart(sendOrder.getCreated());
        List<Order> orders = orderService.getOrdersByTradeAndCreatedRange(sendOrder.getTradeId(), dayStart, DateUtils.addDays(dayStart, 1));
        setAttr("orders", orders);
    }

    public void takeOrder() {
        int id = getParaToInt("id", 0);
        deliverService.takeOrder(id, getCurUser());
        ok("接单成功");
    }
}
