package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Coupon;
import com.aichebaba.rooftop.model.CouponTemplate;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.MemberLevel;
import com.aichebaba.rooftop.model.enums.CustomerState;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.service.CouponService;
import com.aichebaba.rooftop.service.MemberService;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class adminMemberController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponService couponService;

    public void index(){
        String username = getPara("username");
        int level = getParaToInt("level", 0);
        String province = getPara("province", "");
        String stateName = getPara("state");
        CustomerState state = null;
        if(StrKit.notBlank(stateName)){
            state = CustomerState.valueOf(stateName);
        }
        String typeName = getPara("type");
        CustomerType type = null;
        if(StrKit.notBlank(typeName)){
            type = CustomerType.valueOf(typeName);
        }
        String signs = getPara("sign", "");

        String totalStart = getPara("totalStart");
        String totalEnd = getPara("totalEnd");

        Long totalSumStart = null;
        if(StrKit.notBlank(totalStart)){
            totalSumStart =  Math.round(Double.parseDouble(totalStart) * 100);
        }
        Long totalSumEnd = null;
        if(StrKit.notBlank(totalEnd)){
            totalSumEnd =  Math.round(Double.parseDouble(totalEnd) * 100);
        }
        Long countStart = getParaToLong("countStart");
        Long countEnd = getParaToLong("countEnd");

        Date lastStart = getParaToDate("lastStart");
        Date lastEnd = getParaToDate("lastEnd");

        PageList<Customer> pager = customerService.getMembers(username, level, province, state, type, signs, totalSumStart, totalSumEnd, countStart, countEnd,
                lastStart, lastEnd, getPageParam());
        setAttr("pager", pager);

        List<MemberLevel> memberLevels = memberService.findAllMemberLevel();
        setAttr("memberLevels", memberLevels);
        setAttr("useTypes", UseType.values());

        render("manager.html");
    }

    public void detail() {
        int id = getParaToInt("id");
        Customer customer = customerService.getById(id);
        setAttr("customer", customer);
        MemberLevel memberLevel = memberService.getMemberLevelByLevel(customer.getLevel());
        setAttr("memberLevels", memberLevel);
    }

    //会员导出
    @Tx
    public void exportToExcel() {
        String codes = getPara("codes");
        String[] code = codes.split(";");
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, code);
        List<Customer> list = customerService.getCustomerByCodes(codeList);
        if (list.isEmpty()) {
            renderText("没有选中数据");
        } else {
            for (Customer item : list) {
                MemberLevel memberLevel = memberService.getMemberLevelByLevel(item.getLevel());
                item.setLevelName(memberLevel.getName());
            }
            setAttr("items", list);
            excelRender("memberManager.xls", "会员管理报表.xls");
        }
    }

    /**
     * 发放优惠券
     */
    @Tx
    public void sendCoupon() {
        int templateId = getParaToInt("couponTemplateId", 0);
        if(templateId == 0){
            error("请选择优惠券");
            return;
        }
        String codes = getPara("codes");
        String[] code = codes.split(";");
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, code);
        CouponTemplate template = couponService.getTemplate(templateId);
        int leftNum = template.getNum() - template.getSendNum();
        List<Customer> list = customerService.getCustomerByCodes(codeList);
        if(list.size() > leftNum){
            error("优惠券不足！优惠券剩余"+ leftNum + "张，当前选中会员" + list.size() + "人");
            return;
        }

        List<Coupon> coupons = new ArrayList<>();
        if (list.isEmpty()) {
            error("没有选中数据");
            return;
        } else {
            for(Customer item : list){
                coupons.add(new Coupon(templateId, item.getId(), new Date(), 0));
            }
            couponService.saveCoupons(coupons);
        }
        template.setSendNum(template.getSendNum() + list.size());
        couponService.saveTemplate(template, null);

        ok("优惠券发放成功");
    }
}
