package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.service.DeliverService;
import com.aichebaba.rooftop.service.ExpressCompanyService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.ProvinceService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.ExpressHelper;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class DeliverHistoryController extends BaseController {

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

    public void index() {
        String tradeCode = getPara("tradeCode");
        Date fromTime = getParaToDate("fromTime", null);
        Date endTime = getParaToDate("endTime", null);
        String sendCode = getPara("sendCode");
        int expressId = getParaToInt("expressId", 0);
        String expressCode = getPara("expressCode");

        if(endTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tempTime = sdf.format(endTime);
            tempTime += " 23:59:59";
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                endTime = sdf2.parse(tempTime);
            } catch (ParseException ex) {}
        }
        List<ExpressCompany> expressCompanyList = expressCompanyService.getAllExpressCompanies(ExpressType.express);
        setAttr("expressList", expressCompanyList);

        PageList<SendOrder> pager = deliverService.findOldSendOrders(tradeCode, fromTime, endTime, sendCode, expressId, expressCode, getPageParam());
        setAttr("pager", pager);
        for (SendOrder so : pager.getData()) {
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
        render("history_list.html");
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        SendOrder so = deliverService.getSendOrderById(id);
        County county = provinceService.getCountyInfo(so.getCountyId());
        Province province = provinceService.getProvince(so.getProvinceId());
        if (province != null) {
            so.setProvinceName(province.getName());
        }
        if (county != null) {
            so.setCityName(county.getCityName());
            so.setCountyName(county.getName());
        }
        setAttr("sendOrder", so);
        setAttr("province", provinceDao.getById(so.getProvinceId()));
        Date dayStart = DateUtil.getDayStart(so.getCreated());
        List<Order> orders = orderService.getOrdersByTradeAndCreatedRange(so.getTradeId(), dayStart, DateUtils.addDays(dayStart, 1));
        setAttr("orders", orders);

        // 物流API对接 add by 2016-5-27
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(so.getExpressId());
        String expressMark = (expressCompany == null ? "tiantian" : expressCompany.getCode());
        JSONObject expressJson = ExpressHelper.getExpressResult(expressMark, so.getExpressCode());
        setAttr("expressJson", expressJson);
    }

    @Tx
    public void export() {
        synchronized (PickMyTodayController.class) {
            String tradeCode = getPara("tradeCode");
            Date fromTime = getParaToDate("fromTime", null);
            Date endTime = getParaToDate("endTime", null);
            String sendCode = getPara("sendCode");
            int expressId = getParaToInt("expressId", 0);
            String expressCode = getPara("expressCode");

            String codes = getPara("codes");

            PageParam pageParam = getPageParam();
            PageList<SendOrder> pager;
            List<SendOrder> orders = new ArrayList<>();
            if (StrKit.notBlank(codes)) {
                orders = deliverService.findSendOrderList(null, Lists.newArrayList(Splitter.on(',').split(codes)), null, null, -1, null, null, 0);
            } else {
                do {
                    pager = deliverService.findOldSendOrders(tradeCode, fromTime, endTime, sendCode, expressId, expressCode, pageParam);
                    orders.addAll(pager.getData());
                    pageParam.setCurNo(pageParam.getCurNo() + 1);
                } while (pager.getPage().getCurNo() <= pager.getPage().getTotalPage());
            }

            for (SendOrder so : orders) {
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

            if (orders.isEmpty()) {
                renderText("没有可导出的单据或单据已被其他用户导出");
            } else {
                setAttr("items", orders);
                excelRender("sendOrder.xls", "以往发货单.xls");
            }
        }
    }
}
