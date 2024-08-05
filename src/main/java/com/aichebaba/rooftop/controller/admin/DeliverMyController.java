package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.aichebaba.rooftop.service.DeliverService;
import com.aichebaba.rooftop.service.ExpressCompanyService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.ProvinceService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.ExpressHelper;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class DeliverMyController extends BaseController {

    @Autowired
    private DeliverService deliverService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private ProvinceService provinceService;

    public void index() {
        PageList<SendOrder> pager = deliverService
                .findSendOrders(getPara("code"), -1, SendOrderStatus.WAIT_SEND, null, getPageParam());
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
        setAttr("pager", pager);
        render("my_list.html");
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        SendOrder sendOrder = deliverService.getSendOrderById(id);
        setAttr("sendOrder", sendOrder);
        County county = provinceService.getCountyInfo(sendOrder.getCountyId());
        Province province = provinceService.getProvince(sendOrder.getProvinceId());
        if (province != null) {
            sendOrder.setProvinceName(province.getName());
        }
        if (county != null) {
            sendOrder.setCityName(county.getCityName());
            sendOrder.setCountyName(county.getName());
        }
        setAttr("province", provinceDao.getById(sendOrder.getProvinceId()));
        Date dayStart = DateUtil.getDayStart(sendOrder.getCreated());
        List<Order> orders = orderService
                .getOrdersByTradeAndCreatedRange(sendOrder.getTradeId(), dayStart, DateUtils.addDays(dayStart, 1));
        setAttr("orders", orders);

        // 物流API对接 add by 2016-5-25
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(sendOrder.getExpressId());
        String expressMark = (expressCompany == null ? "tiantian" : expressCompany.getCode());
        JSONObject expressJson = ExpressHelper.getExpressResult(expressMark, sendOrder.getExpressCode());
        setAttr("expressJson", expressJson);
    }

    public void deliverItems() {
        int expressId = getParaToInt("expressId");
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(expressId);
        setAttr("expressCompany", expressCompany);
        setAttr("expresses", expressCompanyService.getAllExpressCompanies(ExpressType.express));
        String html = TemplateUtils.html("/admin/deliver/deliverItem.html", getRequest());
        ok("", html);
    }

    @Tx
    public void sendOrder() {
        int id = getParaToInt("id", 0);
        int expressId = getParaToInt("expressId", 0);
        String code = getPara("code");
        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(expressId);
        if (id == 0 || expressCompany == null || StrKit.isBlank(code)) {
            error("数据错误");
            return;
        }
        SendOrder order = deliverService.getSendOrderById(id);
        if (order.getStatus().equals(SendOrderStatus.WAIT_SEND)) {
            deliverService.sendOrder(id, expressCompany.getId(), expressCompany.getName(), code, getCurUser());
            ok("操作成功");
        } else {
            error("发货单已发货");
        }
    }
}
