package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.ExcelUtils;
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
import java.text.ParseException;
import java.util.*;

import static com.aichebaba.rooftop.model.enums.SettlementOrderStatus.wait_check_first;
import static com.aichebaba.rooftop.model.enums.SettlementOrderStatus.wait_check_second;
import static com.aichebaba.rooftop.model.enums.SettlementOrderStatus.wait_check_thirdly;

/**
 * Created by Andy on 2016/8/23.
 */
@Controller
@Scope("prototype")
public class SettlementCheckController extends BaseController {
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private AccountPayeeService accountPayeeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettlementCheckLogService settlementCheckLogService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SettlementCheckLogService checkLogService;

    public void goAuditManage() {
        List<SettlementOrderStatus> statuses = Arrays.asList(wait_check_first, wait_check_second, wait_check_thirdly);
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(getPara("code"), statuses, getParaToDate("startTime"), getParaToDate("endTime"));

        // 权限处理
        List<UserRole> listRole = roleService.getUserRoles(getCurUser().getId());
        List<Integer> sign = new ArrayList();
        for (UserRole userRole : listRole) {
            if (userRole.getRoleId() == 8) {
                sign.add(8);
            }
            if (userRole.getRoleId() == 9) {
                sign.add(9);
            }
            if (userRole.getRoleId() == 10) {
                sign.add(10);
            }
        }

        setAttr("lists", settlementTotalOrders);
        setAttr("signs", sign);
        render("auditManage.html");
    }

    public void goManage() {
        Integer val = 20;
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(SettlementOrderStatus.getVariable(val));
        setAttr("checkId", val);
        setAttr("pageTitle", "结算账单运营上级审核");
        setAttr("data", settlementTotalOrders);
        render("check_list.html");
    }

    public void goAccounting() {
        Integer val = 40;
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(SettlementOrderStatus.getVariable(val));
        setAttr("checkId", val);
        setAttr("pageTitle", "结算账单财务审核");
        setAttr("data", settlementTotalOrders);
        render("check_list.html");
    }

    public void goGeneral() {
        Integer val = 60;
        List<SettlementTotalOrder> settlementTotalOrders = settlementService.findSettlementTotalOrder(SettlementOrderStatus.getVariable(val));
        setAttr("checkId", val);
        setAttr("pageTitle", "结算账单部门经理审核");
        setAttr("data", settlementTotalOrders);
        render("check_list.html");
    }

    public void goChecking() {
        String code = getPara("code");
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

        SettlementOrder so = new SettlementOrder();
        so.setTotalCode(code);
        List<SettlementOrder> settlementOrders = settlementService.findSettlementOrder(code, supplierName, method, settlementMoney);

        List<SettlementCheckLog> checkLogs = checkLogService.findByTotalCode(code);
        setAttr("checkLog", checkLogs);

        setAttr("data", settlementOrders);
        setAttr("checkId", getPara("checkId"));
        setAttr("code", code);
        render("checking.html");
    }

    public void checkPass() {
        String code = getPara("code");
        Integer val = getParaToInt("checkId");

        SettlementOrderStatus sos = null;
        if (val == 20)
            sos = wait_check_second;
        if (val == 40)
            sos = wait_check_thirdly;
        if (val == 60)
            sos = SettlementOrderStatus.wait_pay;

        SettlementTotalOrder settlementOrder = settlementService.getSettlementTotalOrderByCode(code);
        settlementOrder.setStatus(sos);
        settlementService.update(settlementOrder);

        SettlementCheckLog scl = new SettlementCheckLog();
        scl.setTotalCode(code);
        scl.setRemark("通过");
        scl.setStatus(sos);
        scl.setCreatedTime(new Date());
        settlementCheckLogService.save(scl);

        ok("审核成功！");
    }

    public void checkNotPass() {
        String code = getPara("code");
        Integer val = getParaToInt("checkId");
        String remark = getPara("remark");

        if (remark.length() > 50) {
            error("退回理由不能超过50个字符！");
            return;
        }

        SettlementOrderStatus sos = null;
        if (val == 20)
            sos = SettlementOrderStatus.return_check_first;
        if (val == 40)
            sos = SettlementOrderStatus.return_check_second;
        if (val == 60)
            sos = SettlementOrderStatus.return_check_thirdly;

        SettlementTotalOrder settlementOrder = settlementService.getSettlementTotalOrderByCode(code);
        settlementOrder.setStatus(sos);
        settlementService.update(settlementOrder);

        SettlementCheckLog scl = new SettlementCheckLog();
        scl.setTotalCode(code);
        scl.setRemark(remark);
        scl.setStatus(SettlementOrderStatus.getVariable(val - 10));
        scl.setCreatedTime(new Date());
        settlementCheckLogService.save(scl);

        ok("退回成功！");
    }

    public void goSettlementCheck() throws ParseException {
        SettlementOrder setOrder = new SettlementOrder();
        setOrder.setStartTime(getParaToDate("startTime"));
        setOrder.setEndTime(getParaToDate("endTime"));
        setOrder.setSupplierCompany(getPara("supplierCompany"));
        setOrder.setAlipayCode(getPara("alipayCode"));
        setOrder.setAlipayName(getPara("alipayName"));
        setOrder.setSettlementMethod(SettlementMethodType.valOf(getPara("settlementMethod")));

        List<SettlementOrder> list = new ArrayList<>();
        if (setOrder.getStartTime() != null && setOrder.getEndTime() != null) {
            List<Customer> customers = customerService.getSuppliersList(setOrder);
            for (Customer customer : customers) {
                setOrder.setSupplierId(customer.getId());
                Map<String, Object> map = settlementService.getSettlementOrder(setOrder);

                SettlementOrder setOrderNew = new SettlementOrder();
                setOrderNew.setSupplierId(customer.getId());
                setOrderNew.setCreated(customer.getLastSettlementTime());
                setOrderNew.setSupplierCompany(customer.getSupplierCompany());
                if (map.get("saleMoney") != null)
                    setOrderNew.setSaleMoney(Long.valueOf(map.get("saleMoney").toString()));
                if (map.get("backMoney") != null)
                    setOrderNew.setBackMoney(Long.valueOf(map.get("backMoney").toString()));
                setOrderNew.setSettlementMethod(customer.getSettlementMethod());

                AccountPayee accountPayee = accountPayeeService.getDefaultAccountPayee(customer.getId());
                if (accountPayee != null) {
                    setOrderNew.setAlipayCode(accountPayee.getPayeeAccount());
                    setOrderNew.setAlipayName(accountPayee.getPayeeName());
                }
                list.add(setOrderNew);
            }
        }

        setAttr("params", setOrder);
        setAttr("lists", list);
        render("settlementCheck.html");
    }

    public void checkDetail() {
        int supplierId = getParaToInt("supplierId");
        Customer supplier = customerService.getById(supplierId);
        setAttr("supplier", supplier);

        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");

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

        render("check_detail.html");
    }

    public void exportDetail() {
        String[] sid = getPara("sid").split(",");
        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");

        /**********************************************华丽分割线******************************************************/

        HSSFWorkbook excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet("供货商结算及明细");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        HSSFCellStyle alignCenter = excel.createCellStyle();
        alignCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFCellStyle alignRight = excel.createCellStyle();
        alignRight.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

        int poniter = 0;
        HSSFRow row = sheet.createRow(poniter);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("供货商（查询）订单明细");
        cell.setCellStyle(alignCenter);

        row = sheet.createRow(++poniter);
        row.createCell(0).setCellValue("上次结算期间");
        row.createCell(1).setCellValue("供货商名称");
        row.createCell(2).setCellValue("总订单编号");
        row.createCell(3).setCellValue("订单编号");
        row.createCell(4).setCellValue("下单时间");
        row.createCell(5).setCellValue("商品货号");
        row.createCell(6).setCellValue("商品单价");
        row.createCell(7).setCellValue("商品数量");
        row.createCell(8).setCellValue("商品总金额");

        long sumSales = 0l;
        for (String id : sid) {
            Customer customer = customerService.getById(Integer.parseInt(id));
            List<Order> orders = settlementService.findSaleSettlementDetail(Integer.parseInt(id), startTime, endTime);
            for (Order order : orders) {
                row = sheet.createRow(++poniter);
                if (customer.getSettlementMethod().getName() == "现结") {
                    row.createCell(0).setCellValue("--");
                } else {
                    row.createCell(0).setCellValue(DateUtil.getDateFormat(customer.getLastSettlementTime()));
                }
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(order.getTradeCode());
                row.createCell(3).setCellValue(order.getCode());
                row.createCell(4).setCellValue(DateUtil.getDateFormat(order.getPayTime()));
                row.createCell(5).setCellValue(order.getGoodsItemNo());
                row.createCell(6).setCellValue(order.getPrice() / 100d);
                row.createCell(7).setCellValue(order.getQuantity());
                row.createCell(8).setCellValue(order.getMoney() / 100d);
                sumSales += order.getMoney();
            }
        }
        ++poniter;

        sheet.addMergedRegion(new CellRangeAddress(poniter, poniter, 0, 8));
        row = sheet.createRow(poniter);
        cell = row.createCell(0);
        cell.setCellValue("合计：" + sumSales / 100d);
        cell.setCellStyle(alignRight);

        /**********************************************华丽分割线******************************************************/

        poniter += 3;
        sheet.addMergedRegion(new CellRangeAddress(poniter, poniter, 0, 7));
        row = sheet.createRow(poniter);
        cell = row.createCell(0);
        cell.setCellValue("供货商退款（查询）结算表");
        cell.setCellStyle(alignCenter);

        row = sheet.createRow(++poniter);
        row.createCell(0).setCellValue("供货商名称");
        row.createCell(1).setCellValue("订单编号");
        row.createCell(2).setCellValue("支付时间");
        row.createCell(3).setCellValue("退货申请时间");
        row.createCell(4).setCellValue("同意申请时间");
        row.createCell(5).setCellValue("退款原因");
        row.createCell(6).setCellValue("订单状态");
        row.createCell(7).setCellValue("退款金额");

        long sumBacks = 0l;
        for (String id : sid) {
            Customer customer = customerService.getById(Integer.parseInt(id));
            List<Order> backs = settlementService.findBackSettlementDetail(Integer.parseInt(id), startTime, endTime);
            for (Order back : backs) {
                row = sheet.createRow(++poniter);
                row.createCell(0).setCellValue(customer.getName());
                row.createCell(1).setCellValue(customer.getCode());
                row.createCell(2).setCellValue(DateUtil.getDateFormat(back.getPayTime()));
                row.createCell(3).setCellValue(DateUtil.getDateFormat(back.getApplyRefundTime()));
                row.createCell(4).setCellValue(DateUtil.getDateFormat(back.getRefundConfirmTime()));
                row.createCell(5).setCellValue(back.getRefundReason());
                row.createCell(6).setCellValue(back.getStatus().getName());
                row.createCell(7).setCellValue(back.getSupplierMoney() / 100d);
                sumBacks += back.getSupplierMoney();
            }
        }
        ++poniter;

        sheet.addMergedRegion(new CellRangeAddress(poniter, poniter, 0, 7));
        row = sheet.createRow(poniter);
        cell = row.createCell(0);
        cell.setCellValue("合计：" + sumBacks / 100d);
        cell.setCellStyle(alignRight);

        /**********************************************华丽分割线******************************************************/

        poniter += 3;
        sheet.addMergedRegion(new CellRangeAddress(poniter, poniter, 0, 2));
        row = sheet.createRow(poniter);
        cell = row.createCell(0);
        cell.setCellValue("本期结算金额");
        cell.setCellStyle(alignCenter);

        row = sheet.createRow(++poniter);
        row.createCell(0).setCellValue("本期需结算金额");
        row.createCell(1).setCellValue("本期退款金额");
        row.createCell(2).setCellValue("实际应结算金额");

        row = sheet.createRow(++poniter);
        row.createCell(0).setCellValue(sumSales / 100d);
        row.createCell(1).setCellValue(sumBacks / 100d);
        row.createCell(2).setCellValue((sumSales - sumBacks) / 100d);

        /**********************************************华丽分割线******************************************************/

        String fileName = "/供货商（查询）结算及明细" + DateUtil.getDateNow("yyyyMMddHHmmss") + ".xls";
        ExcelUtils.excelFileOutputStream(excel, getCurUserId(), fileName);
        renderFile("/download/" + getCurUserId() + fileName);
    }

    public void exportTotal() {
        String[] sid = getPara("sid").split(",");
        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");

        List<SettlementOrder> settlementOrderList = new ArrayList<>();
        for (String id : sid) {
            SettlementOrder settlementOrder = settlementService.getTempSettlement(Integer.parseInt(id), startTime, endTime);
            Customer customer = customerService.getById(Integer.parseInt(id));
            if (customer.getSettlementMethod().getName() == "现结") {
                settlementOrder.setLastTime(null);
            } else {
                settlementOrder.setLastTime(customer.getLastSettlementTime());
            }
            settlementOrderList.add(settlementOrder);
        }

        setAttr("items", settlementOrderList);
        setAttr("startTime", startTime);
        setAttr("endTime", endTime);

        excelRender("settlementCheck.xls", "供货商（查询）结算汇总" + DateUtil.getDateFormat(startTime, "yyyyMMdd") + "-" + DateUtil.getDateFormat(endTime, "yyyyMMdd") + ".xls");
    }
}
