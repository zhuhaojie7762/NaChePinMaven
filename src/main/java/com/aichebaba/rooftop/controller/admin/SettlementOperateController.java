package com.aichebaba.rooftop.controller.admin;


import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.SettlementCheckLogService;
import com.aichebaba.rooftop.service.SettlementService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.aop.Tx;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 供货商结算账单（运营专员）
 */

@Controller
@Scope("prototype")
public class SettlementOperateController extends BaseController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettlementCheckLogService settlementCheckLogService;

    public void index() {

        String settlementMethod = getPara("settlementMethod", "weekly");    //默认为周结
        String supplierName = getPara("supplierName");
        Double settlementMoney = getParaToDouble("settlementMoney");

        SettlementMethodType method = SettlementMethodType.valOf(settlementMethod);

        Date startTime;
        Date endTime;
        if (method.equals(SettlementMethodType.monthly)) {
            endTime = DateUtil.getMonthStart(new Date());
            startTime = DateUtil.monthAdd(endTime, -1);
        } else if (method.equals(SettlementMethodType.weekly)) {
            endTime = DateUtil.getWeekStart(new Date());
            startTime = DateUtil.dateAdd(endTime, -7);
        } else {
            endTime = DateUtil.getDayStart(new Date());
            startTime = DateUtil.dateAdd(endTime, -1);
        }
        setAttr("startTime", startTime);
        setAttr("endTime", endTime);

        List<SettlementOrder> settlementOrders = settlementService.getTempSettlementByOrder(supplierName, method, settlementMoney);
        for(SettlementOrder settlementOrder : settlementOrders){
            SettlementOrder lastOrder = settlementService.getLastSettlementOrder(settlementOrder.getSupplierId());
            if (lastOrder != null) {
                settlementOrder.setLastTime(lastOrder.getStartTime());
            }
        }
        setAttr("data", settlementOrders);
        render("create_list.html");
    }

    public void detail() {
        int supplierId = getParaToInt("supplierId");
        Customer supplier = customerService.getById(supplierId);
        setAttr("supplier", supplier);
        Date startTime = supplier.getLastSettlementTime();
        Date endTime;
        if (supplier.getSettlementMethod().equals(SettlementMethodType.monthly)) {
            endTime = DateUtil.getMonthStart(new Date());
        } else if (supplier.getSettlementMethod().equals(SettlementMethodType.weekly)) {
            endTime = DateUtil.getWeekStart(new Date());
        } else {
            endTime = DateUtil.getDayStart(new Date());
        }
        //交易订单
        List<Order> orders = settlementService.findSaleSettlementDetail(supplierId, startTime, endTime);
        setAttr("sales", orders);

        //退货订单
        List<Order> backs = settlementService.findBackSettlementDetail(supplierId, startTime, endTime);
        setAttr("backs", backs);

        setAttr("startTime", startTime);
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE, -1);
        setAttr("endTime", calendar.getTime());

        render("create_detail.html");
    }

    @Tx
    public void create() throws ParseException {
//        String[] items = getParaValues("items");
        String[] startTimes = getParaValues("startTime");
        String[] endTimes = getParaValues("endTime");
        String[] supplierIds = getParaValues("supplierId");
        String[] supplierNames = getParaValues("supplierName");
        String[] supplierCompanys = getParaValues("supplierCompany");
        String[] saleMoneys = getParaValues("saleMoney");
        String[] backMoneys = getParaValues("backMoney");
        String[] alipayCodes = getParaValues("alipayCode");
        String[] alipayNames = getParaValues("alipayName");
        String[] settlementMethods = getParaValues("settlementMethod");

        for (int i = 0; i < alipayCodes.length; i++) {
            if (Long.parseLong(saleMoneys[i]) > 0 && StrKit.isBlank(alipayCodes[i])) {
                error("供应商[" + supplierCompanys[i] + "]无收款账号或收款账号未审核通过！");
                return;
            }
        }

        //结算总订单
        Date totalStartTime = getParaToDate("totalStartTime");
        Date totalEndTime = getParaToDate("totalEndTime");
        SettlementTotalOrder settlementTotalOrder = new SettlementTotalOrder();
        settlementTotalOrder.setStartTime(totalStartTime);
        settlementTotalOrder.setEndTime(totalEndTime);
        settlementTotalOrder.setSettlementMethod(SettlementMethodType.valOf(settlementMethods[0]));
        settlementService.addSettlementTotalOrder(settlementTotalOrder);

        for (int i = 0; i < supplierIds.length; i++) {
            //结算订单
            SettlementOrder settlementOrder = new SettlementOrder();
            settlementOrder.setStartTime(DateUtil.parse(startTimes[i],"yyyy-MM-dd"));
            settlementOrder.setEndTime(DateUtil.parse(endTimes[i],"yyyy-MM-dd"));
            settlementOrder.setSupplierId(Integer.parseInt(supplierIds[i]));
            settlementOrder.setSupplierName(supplierNames[i]);
            settlementOrder.setSupplierCompany(supplierCompanys[i]);
            settlementOrder.setSaleMoney(Long.parseLong(saleMoneys[i]));
            settlementOrder.setBackMoney(Long.parseLong(backMoneys[i]));
            settlementOrder.setAlipayCode(alipayCodes[i]);
            settlementOrder.setAlipayName(alipayNames[i]);
            settlementOrder.setSettlementMethod(SettlementMethodType.valOf(settlementMethods[i]));
            settlementOrder.setStatus(SettlementOrderStatus.wait_check_first);
            settlementOrder.setTotalCode(settlementTotalOrder.getCode());
            settlementOrder = settlementService.saveSettlementOrder(settlementOrder);

            //交易订单
            List<Order> sales = settlementService.findSaleSettlementDetail(settlementOrder.getSupplierId(), settlementOrder.getStartTime(), settlementOrder.getEndTime());
            for(Order order : sales){
                SettlementDetail settlementDetail = new SettlementDetail();
                settlementDetail.setSettlementCode(settlementOrder.getCode());
                settlementDetail.setOrderCode(order.getCode());
                settlementDetail.setTradeId(order.getTradeId());
                settlementDetail.setMoney(order.getMoney());
                settlementDetail.setType(SettlementDetailType.sale);
                settlementService.addSettlementDetail(settlementDetail);
            }

            //退货订单
            List<Order> backs = settlementService.findBackSettlementDetail(settlementOrder.getSupplierId(), settlementOrder.getStartTime(), settlementOrder.getEndTime());
            for (Order order : backs) {
                SettlementDetail settlementDetail = new SettlementDetail();
                settlementDetail.setSettlementCode(settlementOrder.getCode());
                settlementDetail.setOrderCode(order.getCode());
                settlementDetail.setTradeId(order.getTradeId());
                settlementDetail.setMoney(order.getMoney());
                settlementDetail.setType(SettlementDetailType.back);
                settlementService.addSettlementDetail(settlementDetail);
            }

            Customer supplier = customerService.getById(settlementOrder.getSupplierId());
            supplier.setLastSettlementTime(settlementOrder.getEndTime());
            customerService.save(supplier);
        }

        SettlementCheckLog scl = new SettlementCheckLog();
        scl.setTotalCode(settlementTotalOrder.getCode());
        scl.setRemark("");
        scl.setStatus(SettlementOrderStatus.wait_check_first);
        scl.setCreatedTime(new Date());
        settlementCheckLogService.save(scl);

        ok("提交成功");
    }

    public void exportDetail() throws IOException {
        String startTime = getPara("totalStartTime");
        String endTime = getPara("totalEndTime");
        String ids = getPara("ids");
        String startTimes = getPara("startTimes");
        String endTimes = getPara("endTimes");

        int rowNum = 0;
        long sumSales = 0l;
        long sumBacks = 0l;

        // 创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("供货商账单明细");
        sheet.addMergedRegion(new Region((short)0, (short)0, (short)0, (short)9));
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//左右居中
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);//居右

        /**
         * 供货商销售订单明细
         */
        // 在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(rowNum);
        // 创建单元格，并设置值表头

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("供货商销售订单明细");
        cell.setCellStyle(style);
        rowNum++;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("结算开始时间");
        cell = row.createCell(1);
        cell.setCellValue("结算结束时间");
        cell = row.createCell(2);
        cell.setCellValue("供货商名称");
        cell = row.createCell(3);
        cell.setCellValue("总订单编号");
        cell = row.createCell(4);
        cell.setCellValue("订单编号");
        cell = row.createCell(5);
        cell.setCellValue("下单时间");
        cell = row.createCell(6);
        cell.setCellValue("商品货号");
        cell = row.createCell(7);
        cell.setCellValue("商品单价");
        cell = row.createCell(8);
        cell.setCellValue("商品数量");
        cell = row.createCell(9);
        cell.setCellValue("商品总金额");

        List<String> idList = Lists.newArrayList(Splitter.on(',').split(ids));
        List<String> startList = Lists.newArrayList(Splitter.on(',').split(startTimes));
        List<String> endList = Lists.newArrayList(Splitter.on(',').split(endTimes));
        List<Integer> intIds = new ArrayList<>();
        for (String id : idList) {
            intIds.add(Integer.parseInt(id));
        }
        Map<Integer, Customer> supplierMap = customerService.getCustomersByIds(intIds);
        for (int i = 0; i < idList.size(); i++) {
            int supplierId = Integer.parseInt(idList.get(i));
            Customer supplier = supplierMap.get(supplierId);
            List<Order> sales = settlementService.findSaleSettlementDetail(supplierId, DateUtil.parse(startList.get(i), "yyyy-MM-dd"), DateUtil.parse(endList.get(i), "yyyy-MM-dd"));
            for (Order order : sales) {
                rowNum++;
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(startList.get(i));
                row.createCell(1).setCellValue(endList.get(i));
                row.createCell(2).setCellValue(supplier.getSupplierCompany());
                row.createCell(3).setCellValue(order.getTradeCode());
                row.createCell(4).setCellValue(order.getCode());
                row.createCell(5).setCellValue(DateUtil.getDateFormat(order.getPayTime()));
                row.createCell(6).setCellValue(order.getGoodsItemNo());
                row.createCell(7).setCellValue(order.getPrice() / 100d);
                row.createCell(8).setCellValue(order.getQuantity());
                row.createCell(9).setCellValue(order.getMoney() / 100d);
                sumSales += order.getMoney();
            }
        }
        rowNum++;
        //合计
        sheet.addMergedRegion(new Region((short) rowNum, (short) 0, (short) rowNum, (short) 9));
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("合计：" + sumSales / 100d);
        cell.setCellStyle(style2);

        //中间空行
        rowNum += 3;

        /**
         * 供货商销售订单明细
         */
        // 在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        sheet.addMergedRegion(new Region((short) rowNum, (short) 0, (short) rowNum, (short) 9));
        row = sheet.createRow(rowNum);
        // 创建单元格，并设置值表头

        cell = row.createCell(0);
        cell.setCellValue("供货商退款订单明细");
        cell.setCellStyle(style);
        rowNum++;
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("结算开始时间");
        cell = row.createCell(1);
        cell.setCellValue("结算结束时间");
        cell = row.createCell(2);
        cell.setCellValue("供货商名称");
        cell = row.createCell(3);
        cell.setCellValue("订单编号");
        cell = row.createCell(4);
        cell.setCellValue("支付时间");
        cell = row.createCell(5);
        cell.setCellValue("退款申请时间");
        cell = row.createCell(6);
        cell.setCellValue("同意申请时间");
        cell = row.createCell(7);
        cell.setCellValue("退款原因");
        cell = row.createCell(8);
        cell.setCellValue("订单状态");
        cell = row.createCell(9);
        cell.setCellValue("退款金额");

        for (int i = 0; i < idList.size(); i++) {
            int supplierId = Integer.parseInt(idList.get(i));
            Customer supplier = supplierMap.get(supplierId);
            List<Order> sales = settlementService.findBackSettlementDetail(supplierId, DateUtil.parse(startList.get(i), "yyyy-MM-dd"), DateUtil.parse(endList.get(i), "yyyy-MM-dd"));
            for (Order order : sales) {
                rowNum++;
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(startList.get(i));
                row.createCell(1).setCellValue(endList.get(i));
                row.createCell(2).setCellValue(supplier.getSupplierCompany());
                row.createCell(3).setCellValue(order.getCode());
                row.createCell(4).setCellValue(DateUtil.getDateFormat(order.getPayTime()));
                row.createCell(5).setCellValue(DateUtil.getDateFormat(order.getApplyRefundTime()));
                row.createCell(6).setCellValue(DateUtil.getDateFormat(order.getRefundConfirmTime()));
                row.createCell(7).setCellValue(order.getRefundReason());
                row.createCell(8).setCellValue(order.getStatus().getName());
                row.createCell(9).setCellValue(order.getMoney() / 100d);
                sumBacks += order.getMoney();
            }
        }
        rowNum++;
        //合计
        sheet.addMergedRegion(new Region((short) rowNum, (short) 0, (short) rowNum, (short) 9));
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("合计：" + sumBacks / 100d);
        cell.setCellStyle(style2);

        //中间空行
        rowNum += 3;

        /**
         * 本期结算金额
         */
        // 在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        sheet.addMergedRegion(new Region((short) rowNum, (short) 0, (short) rowNum, (short) 2));
        row = sheet.createRow(rowNum);
        // 创建单元格，并设置值表头

        cell = row.createCell(0);
        cell.setCellValue("本期结算金额");
        cell.setCellStyle(style);
        rowNum++;
        row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue("本期需结算金额");
        row.createCell(1).setCellValue("本期退款金额");
        row.createCell(2).setCellValue("实际应结算金额");

        rowNum++;
        row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(sumSales / 100d);
        row.createCell(1).setCellValue(sumBacks / 100d);
        row.createCell(2).setCellValue((sumSales - sumBacks) / 100d);

        // 将文件存到指定位置
        String fileName = "/供应商账单明细" + startTime.replace("-", "") + "-" + endTime.replace("-", "") + ".xls";
        try {
            String rootPath = PathKit.getWebRootPath();
            String path = rootPath + "/download/" + getCurUserId();
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fout = new FileOutputStream(path + fileName);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderFile("/download/" + getCurUserId() + fileName);
    }

    public void exportTotal() throws IOException{
        String startTime = getPara("totalStartTime");
        String endTime = getPara("totalEndTime");
        String ids = getPara("ids");
        String startTimes = getPara("startTimes");
        String endTimes = getPara("endTimes");

        List<String> idList = Lists.newArrayList(Splitter.on(',').split(ids));
        List<String> startList = Lists.newArrayList(Splitter.on(',').split(startTimes));
        List<String> endList = Lists.newArrayList(Splitter.on(',').split(endTimes));
        List<SettlementOrder> settlementOrderList = new ArrayList<>();

        for (int i = 0; i < idList.size(); i++) {
            SettlementOrder settlementOrder = settlementService.getTempSettlement(Integer.parseInt(idList.get(i)), DateUtil.parse(startList.get(i), "yyyy-MM-dd"), DateUtil.parse(endList.get(i), "yyyy-MM-dd"));
            settlementOrderList.add(settlementOrder);
        }
        setAttr("items", settlementOrderList);
        setAttr("startTime", startTime);
        setAttr("endTime", endTime);

        excelRender("settlement.xls", "供货商结算汇总" + startTime.replace("-", "") + "-" + endTime.replace("-", "") + ".xls");
    }

}
