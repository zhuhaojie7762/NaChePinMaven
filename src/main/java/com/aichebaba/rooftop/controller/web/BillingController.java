package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.SettlementDetail;
import com.aichebaba.rooftop.model.SettlementOrder;
import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.SettlementService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

/**
 * hdy
 * 供货商账单处理
 * Created by Andy on 2016/8/30.
 */
@Controller
@Scope("prototype")
public class BillingController extends BaseController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private OrderService orderService;

    @ActionKey("bill.html")
    public void index() {
        List<SettlementOrderStatus> statuses = new ArrayList<SettlementOrderStatus>();
        if (getParaToInt("status", 9) == 0) {
            statuses.add(SettlementOrderStatus.return_check_first);
            statuses.add(SettlementOrderStatus.wait_check_first);
            statuses.add(SettlementOrderStatus.return_check_second);
            statuses.add(SettlementOrderStatus.wait_check_second);
            statuses.add(SettlementOrderStatus.return_check_thirdly);
            statuses.add(SettlementOrderStatus.wait_check_thirdly);
        }
        if (getParaToInt("status", 9) == 1) {
            statuses.add(SettlementOrderStatus.wait_pay);
            statuses.add(SettlementOrderStatus.finish);
        }

        SettlementOrder settlement = new SettlementOrder();
        settlement.setSupplierId(getCurCustomer().getId());
        settlement.setCode(getPara("code"));
        settlement.setStartTime(getParaToDate("startTime"));
        settlement.setEndTime(getParaToDate("endTime"));
        PageList<SettlementOrder> pageList = settlementService.queryToBill(settlement, statuses, getPageParam());

        setAttr("pageList", pageList);
        render("bill.html");
    }

    public void detail() {
        String code = getPara("settlementCode");

        SettlementOrder settlementOrder = settlementService.getSettlementOrderByCode(code);
        Customer supplier = customerService.getById(settlementOrder.getSupplierId());
        setAttr("supplier", supplier);

        //交易订单
        List<SettlementDetail> saleDetails = settlementService.findSettlementDetail(code, SettlementDetailType.sale);
        Collection<String> orderCodes = transform(saleDetails, SettlementDetail.ORDER_CODE_VALUE);
        List<Order> orders = orderService.getOrderByCodes(orderCodes);
        setAttr("sales", orders);

        //退货订单
        List<SettlementDetail> backDetails = settlementService.findSettlementDetail(code, SettlementDetailType.back);
        orderCodes = transform(backDetails, SettlementDetail.ORDER_CODE_VALUE);
        List<Order> backs = orderService.getOrderByCodes(orderCodes);
        setAttr("backs", backs);

        setAttr("startTime", settlementOrder.getStartTime());
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(settlementOrder.getEndTime());
        calendar.add(Calendar.DATE, -1);
        setAttr("endTime", calendar.getTime());

        render("statementBill.html");
    }
}
