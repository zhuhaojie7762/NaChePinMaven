package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.SettlementService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.vo.BatchPay;
import com.alipay.config.AlipayConfig;
import com.jfinal.aop.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

@Controller
@Scope("prototype")
public class SettlementPayController extends BaseController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AlipayConfig alipayConfig;

    public void index(){
        String code = getPara("code");
        Date startTime = getParaToDate("startTime", null);
        Date endTime = getParaToDate("endTime", null);
        if (endTime != null) {
            endTime = DateUtil.getDayEnd(endTime);
        }
        List<SettlementOrderStatus> statuses = new ArrayList<>();
        statuses.add(SettlementOrderStatus.wait_pay);
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(code, statuses, startTime, endTime);
//        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(SettlementOrderStatus.wait_pay);
        setAttr("data", settlementTotalOrders);
        render("pay_list.html");
    }

    public void toPay(){
        String code = getPara("code");
        String supplierName = getPara("supplierName");
        Double settlementMoney = getParaToDouble("settlementMoney");

        List<SettlementOrder> settlementOrders = settlementService.findSettlementOrder(code, supplierName, null, settlementMoney);
        setAttr("data", settlementOrders);
        render("toPay.html");
    }

    public void detail(){
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

    @Tx
    public void confirmPay(){
        String codes = getPara("codes");
        List<String> codeList = Arrays.asList(codes.split(","));

        for(String code : codeList){
            settlementService.changeStatus(code, SettlementOrderStatus.finish);
        }

        ok("确认成功");
    }

    public void batchPay(){
        String code = getPara("code");
        String batchCode = settlementService.getBatchCode();
        setAttr("code", batchCode);
        setAttr("day", DateUtil.getDateFormat(new Date(), "yyyyMMdd"));
        List<SettlementOrder> settlementOrders = settlementService.findSettlementOrder(code, null, null, null);
        setAttr("alipay", alipayConfig);
        Long totalMoney = 0l;
        List<BatchPay> batchPays = new ArrayList<>();
        for(SettlementOrder settlementOrder : settlementOrders){
            if(settlementOrder.getSaleMoney() - settlementOrder.getBackMoney() > 0) {
                BatchPay batchPay = new BatchPay();
                batchPay.setPayeeCode(settlementOrder.getAlipayCode());
                batchPay.setPayeeName(settlementOrder.getAlipayName());
                batchPay.setMoney(settlementOrder.getSaleMoney() - settlementOrder.getBackMoney());
                totalMoney += batchPay.getMoney();
                batchPays.add(batchPay);
            }
//            settlementService.changeStatus(settlementOrder.getCode(), SettlementOrderStatus.finish);
        }
        setAttr("size", batchPays.size());
        setAttr("totalMoney", totalMoney);
        setAttr("items", batchPays);
        excelRender("batchPay.xls", "批量结算" + DateUtil.getDateFormat(new Date(), "yyyyMMddHHmm") + ".xls");
    }
}
