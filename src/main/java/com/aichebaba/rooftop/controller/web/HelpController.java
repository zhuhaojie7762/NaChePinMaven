package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.jfinal.core.ActionKey;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class HelpController extends BaseController {
    /**
     * 公司简介
     */
    @ActionKey("companyProfile.html")
    public void companyProfile() {
        render("companyProfile.html");
    }

    /**
     * 联系我们
     */
    @ActionKey("contactUs.html")
    public void contactUs() {
        render("contactUs.html");
    }

    /**
     * 无理由放心退
     */
    @ActionKey("returnGoods.html")
    public void returnGoods() {
        render("returnGoods.html");
    }

    /**
     * 账户注册
     */
    @ActionKey("registeredAccount.html")
    public void registeredAccount() {
        render("registeredAccount.html");
    }

    /**
     * 购物流程
     */
    @ActionKey("gouwuliucheng.html")
    public void gouwuliucheng() {
        render("gouwuliucheng.html");
    }

    /**
     * 配送范围及时间
     */
    @ActionKey("peisongfanwei.html")
    public void peisongfanwei() {
        render("peisongfanwei.html");
    }

    /**
     * 退换货政策
     */
    @ActionKey("zhengce.html")
    public void zhengce() {
        render("zhengce.html");
    }

    /**
     * 退换货办理
     */
    @ActionKey("banliliucheng.html")
    public void banliliucheng() {
        render("banliliucheng.html");
    }

    /**
     * 退款说明
     */
    @ActionKey("tuikuanshuoM.html")
    public void tuikuanshuoM() {
        render("tuikuanshuoM.html");
    }

    /**
     * 法律申明
     */
    @ActionKey("falvshenming.html")
    public void falvshenming() {
        render("falvshenming.html");
    }

    /**
     * 在线客服
     */
    @ActionKey("zaixiankefu.html")
    public void zaixiankefu() {
        render("zaixiankefu.html");
    }
}
