package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.SettlementCheckLogService;
import com.aichebaba.rooftop.service.SettlementService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.aichebaba.rooftop.model.enums.SettlementOrderStatus.finish;
import static com.google.common.collect.Collections2.transform;

@Controller
@Scope("prototype")
public class SettlementOrderController extends BaseController {
    public static final String CLEAN_ACCOUNT_HTML = "cleanAccount.html";
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementCheckLogService checkLogService;

    @Autowired
    private OrderService orderService;

    /**
     * 审核中的订单
     */
    public void checking() {
        String code = getPara("code");
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
        List<SettlementOrderStatus> statuses = new ArrayList<>();
        statuses.add(SettlementOrderStatus.wait_check_first);
        statuses.add(SettlementOrderStatus.wait_check_second);
        statuses.add(SettlementOrderStatus.wait_check_thirdly);
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(code, statuses, startTime, endTime);
        setAttr("data", settlementTotalOrders);
        render("order_list.html");
    }

    /**
     * 订单详情
     */
    public void detail() {
        String code = getPara("code");
        String supplierName = getPara("supplierName");
        Double settlementMoney = getParaToDouble("settlementMoney");

        SettlementTotalOrder totalOrder = settlementService.getSettlementTotalOrderByCode(code);
        setAttr("totalOrder", totalOrder);

        List<SettlementOrder> settlementOrders = settlementService.findSettlementOrder(code, supplierName, null, settlementMoney);
        setAttr("data", settlementOrders);

        List<SettlementCheckLog> checkLogs = checkLogService.findByTotalCode(code);
        setAttr("checkLog", checkLogs);
        render("order.html");
    }

    /**
     * 被退回的账单
     */
    public void back() {
        String code = getPara("code");
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
        List<SettlementOrderStatus> statuses = new ArrayList<>();
        statuses.add(SettlementOrderStatus.return_check_first);
        statuses.add(SettlementOrderStatus.return_check_second);
        statuses.add(SettlementOrderStatus.return_check_thirdly);
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(code, statuses, startTime, endTime);
        for (SettlementTotalOrder settlementTotalOrder : settlementTotalOrders) {
            settlementTotalOrder.setBackTime(checkLogService.getLastTime(settlementTotalOrder.getCode()));
        }
        setAttr("data", settlementTotalOrders);
        render("back_order.html");
    }

    /**
     * 已审批的账单
     */
    public void finished() {
        String code = getPara("code");
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
        List<SettlementOrderStatus> statuses = new ArrayList<>();
        statuses.add(SettlementOrderStatus.wait_pay);
        statuses.add(finish);
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(code, statuses, startTime, endTime);
        for (SettlementTotalOrder settlementTotalOrder : settlementTotalOrders) {
            settlementTotalOrder.setCheckFinishTime(checkLogService.getCheckFinishTime(settlementTotalOrder.getCode()));
        }
        setAttr("data", settlementTotalOrders);
        render("finish_order.html");
    }


    @Tx
    public void restart(){
        String code = getPara("code");
        SettlementTotalOrder totalOrder = settlementService.getSettlementTotalOrderByCode(code);
        totalOrder.setStatus(SettlementOrderStatus.wait_check_first);
        settlementService.update(totalOrder);

        List<SettlementOrder> settlementOrders = settlementService.findSettlementOrder(code, null, null, null);
        for(SettlementOrder settlementOrder : settlementOrders){
            settlementOrder.setStatus(SettlementOrderStatus.wait_check_first);
            settlementService.saveSettlementOrder(settlementOrder);
        }
        SettlementCheckLog settlementCheckLog = new SettlementCheckLog();
        settlementCheckLog.setTotalCode(code);
        settlementCheckLog.setRemark("");
        settlementCheckLog.setStatus(SettlementOrderStatus.wait_check_first);
        settlementCheckLog.setCreatedTime(new Date());
        checkLogService.save(settlementCheckLog);

        ok("成功");
    }

    /**
     *  供货商已结算汇总 && 对账单记录
     *  add hdy 2016-9-12
     */
    public void goCleanAccount() {
        List<SettlementOrderStatus> statuses = Arrays.asList(finish);
        SettlementOrder settlementOrder = new SettlementOrder();
        settlementOrder.setSupplierCompany(getPara("supplierCompany"));
        settlementOrder.setAlipayCode(getPara("alipayCode"));
        settlementOrder.setAlipayName(getPara("alipayName"));
        String methodParam = getPara("methodParam");
        if (StrKit.notBlank(methodParam)) {
            settlementOrder.setSettlementMethod(SettlementMethodType.valOf(methodParam));
        }
        settlementOrder.setStartTime(getParaToDate("startTime1", null));
        settlementOrder.setEndTime(getParaToDate("endTime1", null));

        // 供货商结算部汇
        PageList<SettlementOrder> pages1 = settlementService.getSettlementCleanAccount(settlementOrder, statuses, getPageParam());
        for (SettlementOrder settlementOrder1 : pages1.getData()) {
            SettlementOrder lastOrder = settlementService.getLastSettlementOrder(settlementOrder.getSupplierId());
            if (lastOrder != null) {
                settlementOrder1.setLastTime(lastOrder.getStartTime());
            }
        }

        // 对账单记录
        String code = getPara("code");
        Date startTime = getParaToDate("startTime2", null);
        Date endTime = getParaToDate("endTime2", null);
        List<SettlementTotalOrder> pages2 = settlementService.findSettlementTotalOrder(code, statuses, startTime, endTime);

        setAttr("pages1", pages1);
        setAttr("pages2", pages2);
        setAttr("cleanAccount", settlementOrder);
        setAttr("tabParam", getPara("tabParam"));
        render("cleanAccount.html");
    }

    public void detailCleanAccount(){
        String code = getPara("code");
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

        render("create_detail.html");
    }
}
