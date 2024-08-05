package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.CouponActivity;
import com.aichebaba.rooftop.model.CouponTemplate;
import com.aichebaba.rooftop.model.enums.CouponSendType;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.service.CouponService;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.base.Joiner;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class CouponManageController extends BaseController {

    @Autowired
    private CouponService couponService;

    public void index(){
        String couponName = getPara("couponName");
        String activityName = getPara("activityName");
        Date fromTime = getParaToDate("fromTime");
        Date endTime = getParaToDate("endTime");
        Double dCondition = getParaToDouble("condition", 0d);
        int condition = (int) (dCondition * 100);
        int num = getParaToInt("num", 0);
        Double dDenomination = getParaToDouble("denomination", 0d);
        int denomination = (int) (dDenomination * 100);
        String status = getPara("status");
        CouponTemplateStatus status1 = null;
        if(StrKit.notBlank(status)) {
            status1 = CouponTemplateStatus.valueOf(status);
        }

        List<Long> denominations = couponService.getCouponMoney();
        setAttr("denominations", denominations);
        PageList<CouponTemplate> pager = couponService.findCouponTemplates(couponName, activityName, fromTime, endTime, condition, num, denomination, status1, getPageParam());
        for(CouponTemplate item : pager.getData()){
            List<CouponActivity> activities = couponService.findActivitysByTemplateId(item.getId());
            StringBuilder activityInfo = new StringBuilder();
            for(CouponActivity activity : activities){
                activityInfo.append(activity.getName()).append(",");
            }
            if(activityInfo.length() > 0){
                activityInfo.deleteCharAt(activityInfo.length() - 1);
            }
            item.setActivityName(activityInfo.toString());
        }

        setAttr("pager", pager);
        render("list.html");
    }

    public void enableTemplates() {
        List<CouponTemplate> templates = couponService.findEnableCouponTemplates(UseType.valueOf(getPara("useType")),
                new Date(), CouponTemplateStatus.NORMAL);
        setAttr("templates", templates);
        String html = TemplateUtils.html("admin/coupon/items", getRequest());
        success(html);
    }

    /**
     * 停用/启用优惠券
     */
    public void change(){
        int id = getParaToInt("id", 0);
        String status = getPara("status");

        if("stop".equals(status)){
            couponService.disableTemplate(id, getCurUser());
        }else{
            couponService.enableTemplate(id, getCurUser());
        }
        ok("");
    }

    /**
     * 创建优惠券
     */
    public void add() {
        render("add.html");
    }

    /**
     * 编辑优惠券
     */
    public void edit() {
        int id = getParaToInt("id", 0);
        CouponTemplate couponTemplate = couponService.getTemplate(id);
        setAttr("template", couponTemplate);
        render("edit.html");
    }

    /**
     * 保存优惠券
     */
    public void save(){
        int id = getParaToInt("id", 0);
        String couponName = getPara("couponName");
        Double money = getParaToDouble("money", 0d);
        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");
        int num = getParaToInt("num", 0);
        Double condition = getParaToDouble("condition", 0d);
        String useType = getPara("useType");
        String status = getPara("status");
        String sendType = getPara("sendType");
        String display = getPara("display");
        String donation = getPara("donation");
        if(startTime.getTime() > endTime.getTime()){
            error("有效期不正确，开始时间大于截止时间");
            return;
        }
        if (couponService.templateRepeatCheck(couponName, money, id)){
            error("已存在同面值的同名优惠券");
            return;
        }

        CouponTemplate couponTemplate;
        if (id == 0) {
            couponTemplate = new CouponTemplate();
        } else {
            couponTemplate = couponService.getTemplate(id);
        }
        couponTemplate.setName(couponName);
        couponTemplate.setMoney((int) (money * 100));
        couponTemplate.setStartTime(startTime);
        couponTemplate.setEndTime(endTime);
        couponTemplate.setNum(num);
        couponTemplate.setCondition((int) (condition * 100));
        couponTemplate.setUseType(UseType.valueOf(useType));
        couponTemplate.setStatus(CouponTemplateStatus.valueOf(status));
        couponTemplate.setSendType(CouponSendType.valueOf(sendType));
        couponTemplate.setDisplay("1".equals(display));
        couponTemplate.setDonation("1".equals(donation));
        couponService.saveTemplate(couponTemplate, getCurUser());

        ok("保存成功");
    }
}
