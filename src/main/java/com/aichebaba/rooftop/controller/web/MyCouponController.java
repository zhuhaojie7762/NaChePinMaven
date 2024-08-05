package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Coupon;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.service.CouponService;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
@Scope("prototype")
public class MyCouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 我的优惠券
     */
    @ActionKey("list.html")
    public void list() {
        PageList<Coupon> pager = couponService.findCoupon(curCustomerId(), new Date(), false, CouponTemplateStatus.NORMAL, new PageParam(1000, 1));
        setAttr("coupons", pager);

        PageList<Coupon> oldPager = couponService.findOldCoupon(curCustomerId(), new Date(), CouponTemplateStatus.NORMAL, new PageParam(1000, 1));
        setAttr("oldCoupons", oldPager);
        render("list.html");
    }
}
