package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.service.DeliverService;
import com.aichebaba.rooftop.service.ExpressCompanyService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.ProvinceService;
import com.aichebaba.rooftop.service.refund.RefundDetectorService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.aop.Tx;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class DeliverPrintController extends BaseController {

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    public void index() throws Exception {
        String code = getPara("code");
        String strStartTime = getPara("startTime");
        String strEndTime = getPara("endTime");
        int expressId = getParaToInt("expressId", 0);
        Date dStartTime = null;
        Date dEndTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (StrKit.notBlank(strStartTime)) {
            dStartTime = sdf.parse(strStartTime);
        }
        if (StrKit.notBlank(strEndTime)) {
            dEndTime = sdf.parse(strEndTime);
        }

        List<ExpressCompany> expressCompanyList = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("expressList", expressCompanyList);

        List<SendOrder> list = deliverService
                .findSendOrderList(code, null, dStartTime, dEndTime, 0, SendOrderStatus.PICK_FINISHED, null, expressId);
        for (SendOrder so : list) {
            County county = provinceService.getCountyInfo(so.getCountyId());
            Province province = provinceService.getProvince(so.getProvinceId());
            if (province != null) {
                so.setProvinceName(province.getName());
            }
            if (county != null) {
                so.setCityName(county.getCityName());
                so.setCountyName(county.getName());
            }
        }
        setAttr("sendOrders", list);
        render("print_list.html");
    }

    @Tx
    public void setExpressCode() {
        int id = getParaToInt("id");
        int expressId = getParaToInt("expressId", 0);
        String expressCode = getPara("code");
        SendOrder sendOrder = deliverService.getSendOrderById(id);
        if (!sendOrder.getStatus().equals(SendOrderStatus.PICK_FINISHED)) {
            error("拣货中的订单才能完成拣货");
            return;
        }
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(expressId);
        if(expressCompany == null){
            error("未找到该物流公司");
            return;
        }
        deliverService.updateExpressCode(id, expressCompany.getId(), expressCompany.getName(), expressCode);
        ok("");
    }

    public void exportToExcel() throws IOException {

        String codes = getPara("codes");
        if (StrKit.isBlank(codes)) {
            error("请选择打印的发货单");
            return;
        }

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("发货单");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("收件人姓名");
        cell = row.createCell(1);
        cell.setCellValue("收件人地址");
        cell = row.createCell(2);
        cell.setCellValue("收件人联系方式");
        cell = row.createCell(3);
        cell.setCellValue("收件人邮编");
        cell = row.createCell(4);
        cell.setCellValue("寄件日期");
        cell = row.createCell(5);
        cell.setCellValue("商品描述");
        cell = row.createCell(6);
        cell.setCellValue("商品数量");
        cell = row.createCell(7);
        cell.setCellValue("总订单号");
        cell = row.createCell(8);
        cell.setCellValue("订单号");
        cell = row.createCell(9);
        cell.setCellValue("买家备注");
        cell = row.createCell(10);
        cell.setCellValue("省");
        cell = row.createCell(11);
        cell.setCellValue("市");
        cell = row.createCell(12);
        cell.setCellValue("区");
        cell = row.createCell(13);
        cell.setCellValue("快递");

        List<String> codeList = Lists.newArrayList(Splitter.on(',').split(codes));
        List<String> sendOrderCodes = new ArrayList<>();
        for (String codeStr : codeList) {
            SendOrder sendOrder = orderService.getSendOrderByTradeId(Trade.code2id(codeStr));
            sendOrderCodes.add(sendOrder.getCode());
        }
        String[] code = new String[sendOrderCodes.size()];
        sendOrderCodes.toArray(code);
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        //String[] code = codes.split(";");
        List<SendOrder> list = deliverService
                .findSendOrderList(null, Arrays.asList(code), null, null, -1, null, null, 0);

        for (SendOrder ignored : list) {
            for (SendOrder so : list) {
                County county = provinceService.getCountyInfo(so.getCountyId());
                Province province = provinceService.getProvince(so.getProvinceId());
                if (province != null) {
                    so.setProvinceName(province.getName());
                }
                if (county != null) {
                    so.setCityName(county.getCityName());
                    so.setCountyName(county.getName());
                }
            }
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                SendOrder send = list.get(i);

                StringBuilder goodsInfo = new StringBuilder();
                int goodsCount = 0;
                StringBuilder orderCodes = new StringBuilder();
                String buyerMessage = "";
                List<Order> orders = orderService.getOrdersBySendOrderId(send.getId());
                for (Order item : orders) {
                    goodsInfo.append(item.getGoodsName()).append(item.getGoodsItemNo()).append(" x ").append(item.getQuantity()).append(" ").append(item.getSpecPropValue().replace("规格:", "").replace("颜色:", "").replace("尺寸:", "").replace(";", " ")).append(", ");
                    goodsCount += item.getQuantity();
                    // S 退款退货中的订单过滤 16-11-26.改
                    if (!RefundDetectorService.doInclude(item)) {
                        orderCodes.append(item.getCode()).append(", ");
                    }
                    // E 退款退货中的订单过滤
                    buyerMessage = item.getBuyerMessage();
                }
                if (goodsInfo.toString().endsWith(", ")) {
                    goodsInfo = new StringBuilder(goodsInfo.substring(0, goodsInfo.length() - 2));
                }
                if (orderCodes.toString().endsWith(", ")) {
                    orderCodes = new StringBuilder(orderCodes.substring(0, orderCodes.length() - 2));
                }
                // 第四步，创建单元格，并设置值
                row.createCell(0).setCellValue(send.getBuyerName());
                row.createCell(1).setCellValue(getValue(send.getProvinceName()) + getValue(send.getCityName())
                        + getValue(send.getCountyName()) + send.getBuyerAddress());
                row.createCell(2).setCellValue(send.getBuyerPhone());
                row.createCell(3).setCellValue(send.getBuyerPostCode());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                row.createCell(4).setCellValue(sdf.format(new Date()));
                row.createCell(5).setCellValue(goodsInfo.toString());
                row.createCell(6).setCellValue(goodsCount);
                row.createCell(7).setCellValue(send.getTradeCode());
                row.createCell(8).setCellValue(orderCodes.toString());
                row.createCell(9).setCellValue(buyerMessage);
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue("");
                row.createCell(12).setCellValue("");
                row.createCell(13).setCellValue(send.getExpressName());
            }
        }





        // 第六步，将文件存到指定位置
        String fileName = "/快递面单导出" + DateUtil.getDateNow("yyyyMMddHHmmss") + ".xls";
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

    private String getValue(String val) {
        return StrKit.notBlank(val) ? val : "";
    }
}
