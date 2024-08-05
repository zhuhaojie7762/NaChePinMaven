package com.aichebaba.rooftop.interceptor.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.vo.Json;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class LoginInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        if (inv.getActionKey().startsWith("/admin")) {
            BaseController c = (BaseController) inv.getController();
            if (c.getCurUser() != null) {
                inv.invoke();
            } else {
                if (c.isAjax()) {
                    c.renderJson(new Json(Json.NO_LOGIN, "", "/admin/login.html"));
                } else {
                    c.redirect("/admin/login.html");
                }
            }
        } else {
            inv.invoke();
        }
    }
}
