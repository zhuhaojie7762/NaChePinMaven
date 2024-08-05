package com.aichebaba.rooftop.interceptor.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.jfinal.aop.Invocation;
import com.jfinal.aop.Interceptor;
import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.List;

public class RedirectInterceptor implements Interceptor {

    private static final List<String> excludePre = new ArrayList<String>(){
        {
            add("/login.html");
            add("/register.html");
            add("/logout");
            add("/agreement.html");
            add("/forgetPwd.html");
            add("/regSuccess.html");
        }
    };

    private boolean inUrl(String url) {
        for (String s : excludePre) {
            if (url.startsWith(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void intercept(Invocation invocation) {
        if (!invocation.getActionKey().startsWith("/admin")) {
            BaseController c = (BaseController) invocation.getController();

            //初始化商品分类数据
            c.initGoodsClass();

            //初始化品牌
            c.initBrand();

            String actionKey = invocation.getActionKey();
            if (StrKit.isBlank(actionKey)) {
                actionKey = "/";
            }
            if(c.isAjax() || !inUrl(actionKey)) {
                actionKey = "/";
            }
            c.setAttr("actionKey", actionKey);

            Customer curCustomer = c.getCurCustomer();
            //未登录用户，跳转到登录页
            if (curCustomer == null && invocation.getActionKey().startsWith("/member")) {
                c.redirect("/login.html");
                return;
            } else if (invocation.getActionKey().startsWith("/member/purchase.html")) {
                //进货商或供货商不到申请进货页，跳转到用户中心
                assert curCustomer != null;
                if (curCustomer.getType().equals(CustomerType.buyer) || curCustomer.getType().equals(CustomerType.seller)) {
                    c.redirect("/member/index.html");
                }
            } else if (invocation.getActionKey().startsWith("/member/supplier.html")) {
                //供货商不到申请供货页，跳转到用户中心
                if (curCustomer.getType().equals(CustomerType.seller)) {
                    c.redirect("/member/index.html");
                }
            }

            invocation.invoke();
        }else {
            invocation.invoke();
        }
    }
}
