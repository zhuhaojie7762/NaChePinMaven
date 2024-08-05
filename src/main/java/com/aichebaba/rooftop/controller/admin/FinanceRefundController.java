package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.ExcelUtils;
import com.aichebaba.rooftop.vo.BatchPay;
import com.alipay.config.AlipayConfig;
import com.google.common.collect.*;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.*;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Controller
@Scope("prototype")
public class FinanceRefundController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private AlipayConfig alipayConfig;

    public void index() {
        RefundParam refundParam = new RefundParam();
        refundParam.setNameParam(getPara("nameParam"));
        refundParam.setCodeParam(getPara("codeParam"));
        refundParam.setStartTimeParam(getParaToDate("startTimeParam"));
        refundParam.setEndTimeParam(getParaToDate("endTimeParam"));
        refundParam.setStatusParam(OrderStatus.getVariable(getParaToInt("statusParam")));
        refundParam.setPaidAlpayCodeParam(getPara("paidAlpayCodeParam"));
        Integer expressIdParam = getParaToInt("expressIdParam");
        refundParam.setExpressIdParam(expressIdParam);
        refundParam.setExpressCodeParam(getPara("expressCodeParam"));

        List listStr = Arrays.asList(WAIT_REFUND_BY_CANCEL, FULL_REFUND_GOODS, REFUND_GOODS_DISCUSS, REFUND_CONFIRM, CLOSED_BY_CANCEL, CLOSED_BY_REFUND_GOODS, CLOSED_BY_REFUND);
        PageList orderList = orderService.listByRefund(refundParam, listStr, getPageParam());

        ImmutableListMultimap orderMultimap = Multimaps.index(orderList.getData(), Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(orderMultimap.keySet());
        for (Trade trade : trades) {
            trade.setCustomer(customerService.getById(trade.getBuyerId()));
            trade.setOrders(orderMultimap.get(trade.getId()));
        }
        PageList<Trade> pager = new PageList<>(trades, orderList.getPage());

        // 返回查询者用的快递公司信息
        if (expressIdParam != null && expressIdParam != 0) {
            ExpressCompany expressInfo = expressCompanyService.getExpressCompanyById(expressIdParam);
            refundParam.setExpressNameParam(expressInfo.getName());
        }
        // 加载快递公司
        List<ExpressCompany> expressLists = expressCompanyService.getAllExpressCompanies();

        setAttr("expressLists", expressLists);
        setAttr("refund", refundParam);
        setAttr("pager", pager);
        render("refundList.html");
    }

    public void toDeal() {
        String code = getPara("code");
        Order order = orderService.getOrderByCode(code);
        setAttr("order", order);
        render("deal.html");
    }

    @Tx
    public void deal() {
        String codes[] = getPara("codes").split(",");
        for(String code : codes) {
            Order order = orderService.getOrderByCode(code);
            Trade trade = orderService.getTrade(order.getTradeId());
            switch (order.getStatus()) {
                case WAIT_REFUND_BY_CANCEL:
                    if (trade.getCouponId() > 0) {
                        Coupon coupon = couponService.getCoupon(trade.getCouponId());
                        coupon.setUsed(false);
                        coupon.setUsedTime(null);
                        couponService.saveCoupon(coupon);
                    }
                    order.setStatus(CLOSED_BY_CANCEL);
                    break;
                case FULL_REFUND_GOODS:
                case REFUND_GOODS_DISCUSS:
                    order.setStatus(CLOSED_BY_REFUND_GOODS);
                    break;
                case REFUND_CONFIRM:
                    order.setStatus(CLOSED_BY_REFUND);
                    break;
                default:
//                    error("退款已处理");
//                    return;
                    continue;
            }
            if (trade.getRefundNum() == trade.getOrderCnt()) {
                trade.setStatus(order.getStatus());
            }
            order.setEndTime(new Date());
            orderService.update(order);
            orderService.updateTrade(trade);
        }
        ok("操作成功");
    }

    public void outExcel() {
        String[] codes = getPara("codes").split(",");

        HSSFWorkbook excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet("退款单");
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = excel.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("退款处理一览表");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        row = sheet.createRow(1);
        row.createCell(0).setCellValue("退款人账号");
        row.createCell(1).setCellValue("子订单编号");
        row.createCell(2).setCellValue("支付时间");
        row.createCell(3).setCellValue("退货申请时间");
        row.createCell(4).setCellValue("退货原因");
        row.createCell(5).setCellValue("订单状态");
        row.createCell(6).setCellValue("退款金额(元)");
        row.createCell(7).setCellValue("完成日期");

        for(int i = 0; i < codes.length; i++) {
            Order order = orderService.getOrderByCode(codes[i]);
            Trade trade = orderService.getTrade(order.getTradeId());
            Customer customer = customerService.getById(order.getBuyerId());

            if (customer == null) {
                continue;
            }

            row = sheet.createRow(i + 2);
            if (StrKit.notBlank(customer.getUsername())) {
                row.createCell(0).setCellValue(customer.getUsername());
            } else {
                row.createCell(0).setCellValue("");
            }
            row.createCell(1).setCellValue(order.getCode());
            if (trade.getPayTime() != null) {
                row.createCell(2).setCellValue(DateUtil.getDateFormat(trade.getPayTime(), "yyyy-MM-dd HH:mm"));
            } else {
                row.createCell(2).setCellValue("");
            }
            if (order.getApplyRefundTime() != null) {
                row.createCell(3).setCellValue(DateUtil.getDateFormat(order.getApplyRefundTime(),"yyyy-MM-dd HH:mm"));
            } else {
                row.createCell(3).setCellValue("");
            }
            if (StrKit.notBlank(order.getRefundReason())) {
                row.createCell(4).setCellValue(order.getRefundReason());
            } else {
                row.createCell(4).setCellValue("");
            }
            row.createCell(5).setCellValue(order.getStatus().getName());
            row.createCell(6).setCellValue(new DecimalFormat("0.00").format(order.getRefundFee()/100d));
            if (order.getEndTime() != null) {
                row.createCell(7).setCellValue(DateUtil.getDateFormat(order.getEndTime(), "yyyy-MM-dd HH:mm"));
            } else {
                row.createCell(7).setCellValue("");
            }
        }

        String fileName = "/退款处理导出" + DateUtil.getDateNow("yyyyMMddHHmmss") + ".xls";
        ExcelUtils.excelFileOutputStream(excel, getCurUserId(), fileName);
        renderFile("/download/" + getCurUserId() + fileName);
    }

    public void batchPay(){
        List<String> codes = Arrays.asList(getPara("codes").split(","));
        List<Order> orders = orderService.getOrderByCodes(codes);
        Collection<Integer> tradeIds = Collections2.transform(orders, Order.tradeIdValue);
        List<Trade> trades = orderService.getTradesByIds(tradeIds);
        Map<Integer, Trade> tradeMap = Maps.uniqueIndex(trades, Trade.ID_VALUE);

        String batchCode = settlementService.getBatchCode();
        setAttr("code", batchCode);
        setAttr("day", DateUtil.getDateFormat(new Date(), "yyyyMMdd"));

        setAttr("alipay", alipayConfig);
        Long totalMoney = 0L;
        List<BatchPay> batchPays = new ArrayList<>();
        for(Order order : orders){
//            //退款额大于零

                BatchPay batchPay = new BatchPay();
                Trade trade = tradeMap.get(order.getTradeId());
                batchPay.setPayeeCode(trade.getPaidAlipayCode());
                batchPay.setMoney(order.getRefundFee());
                totalMoney += order.getRefundFee();
                batchPays.add(batchPay);

        }
        setAttr("size", batchPays.size());
        setAttr("totalMoney", totalMoney);
        setAttr("items", batchPays);
        excelRender("batchPay.xls", "批量结算" + DateUtil.getDateFormat(new Date(), "yyyyMMddHHmm") + ".xls");
    }
}
