package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.CustomerCheck;
import com.aichebaba.rooftop.model.enums.CustomerAuditState;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.vo.CustomerApplyAudit;
import com.google.common.base.Joiner;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;

@Controller
@Scope("prototype")
public class SupplierAuditController extends BaseController {

    @Autowired
    private CustomerService customerService;

    public void index() {
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String phone = null;
        String code = null;
        String name = null;
        if (StrKit.notBlank(value, fieldName)) {
            if ("phone".equals(fieldName)) {
                phone = value;
            } else if ("code".equals(fieldName)) {
                code = value;
            } else if ("name".equals(fieldName)) {
                name = value;
            }
        }

        String statusVal = getPara("status", "");
        CustomerAuditState status = null;
        if (StrKit.notBlank(statusVal)) {
            if (StrKit.notBlank(statusVal)) {
                status = (CustomerAuditState) CustomerAuditState.NO_AUDIT.valOf(Integer.parseInt(statusVal));
            }
        }
        PageList<CustomerApplyAudit> pager = customerService.findWaitAuditSupplier(phone, code, name, status, getPageParam());
        setAttr("statusList", CustomerAuditState.values());
        setAttr("pager", pager);
        render("list.html");
    }

    public void toAudit() {
        int id = getParaToInt("id", 0);
        CustomerCheck apply = customerService.getCustomerApplyCheck(id);
        setAttr("apply", apply);
        Customer customer = customerService.getById(apply.getCustomerId());
        setAttr("customer", customer);
        render("audit.html");
    }

    public void audit() {
        int id = getParaToInt("id", 0);
        Boolean auditResult = getParaToBoolean("result");
        if (auditResult == null) {
            error("请选择审核结果");
            return;
        }
        String reason = getPara("reason");
        if (!auditResult && StrKit.isBlank(reason)) {
            error("请填写反馈");
            return;
        }
        String[] sign = getParaValues("sign");
        String remarks = getPara("remarks", "");

        if (sign == null || sign.length == 0) {
            customerService.auditApplySupplierAudit(id, auditResult, reason, getCurUser(), null, remarks);
        }else {
            customerService.auditApplySupplierAudit(id, auditResult, reason, getCurUser(), Joiner.on(";").join(sign), remarks);
        }
        ok("操作成功");
    }

    public void edit() {
        int id = getParaToInt("id", 0);
        CustomerCheck apply = customerService.getCustomerApplyCheck(id);
        setAttr("apply", apply);
        Customer customer = customerService.getById(apply.getCustomerId());
        setAttr("customer", customer);
        render("audit.html");
    }
}
