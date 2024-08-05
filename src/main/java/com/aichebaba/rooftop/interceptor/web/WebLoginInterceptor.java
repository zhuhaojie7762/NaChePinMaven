package com.aichebaba.rooftop.interceptor.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * 用于 @Before
 */
public class WebLoginInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        BaseController c = (BaseController) inv.getController();
        if (c.getCurCustomer() == null) {
            if (c.isAjax()) {
                c.error(100, "请登录");
            } else {
                c.redirect("/login.html");
            }
            return;
        }
        inv.invoke();
    }
}
