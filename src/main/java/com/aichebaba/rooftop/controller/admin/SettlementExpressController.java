package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.SendOrderDao;
import com.aichebaba.rooftop.model.ExpressCompany;
import com.aichebaba.rooftop.model.SendOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.service.DeliverService;
import com.aichebaba.rooftop.service.ExpressCompanyService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.SettlementService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.vo.SettlementExpress;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class SettlementExpressController extends BaseController {

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private OrderService orderService;

    public void index(){
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        String expressName = getPara("expressName");
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
//        List<ExpressCompany> expressCompanies = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        List<SettlementExpress> settlementExpresses = settlementService.findSettlementExpress(startTime, endTime, expressName);
        setAttr("data", settlementExpresses);
        render("express_list.html");
    }

    public void detail(){
        int expressId = getParaToInt("id", 0);
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
        List<SendOrder> sendOrders = deliverService.findSendOrderList(null, null, startTime, endTime, -1, SendOrderStatus.FINISHED, null, expressId);
        Collection<Integer> tradeIds = Collections2.transform(sendOrders, SendOrder.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(tradeIds);
        Map<Integer, Trade> tradeMap = Maps.uniqueIndex(trades, Trade.ID_VALUE);

        for(SendOrder sendOrder : sendOrders){
            sendOrder.setPayTime(tradeMap.get(sendOrder.getTradeId()).getPayTime());
            Long quantity = orderService.getGoodsNumByTradeId(sendOrder.getTradeId());
            sendOrder.setGoodsNum(quantity);
        }

        setAttr("data", sendOrders);
        render("express_detail.html");
    }
}
