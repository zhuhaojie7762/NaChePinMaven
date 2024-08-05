package com.aichebaba.rooftop.interceptor.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Menu;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.util.List;

public class MenusInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        if (invocation.getActionKey().startsWith("/admin")) {
            BaseController c = (BaseController) invocation.getController();
            c.initTopMenus();
            List<Menu> menus = c.getSessionAttr("leftMenus");
            if (menus != null) {
                String requesturi = c.getRequest().getRequestURI();
                for (Menu menu : menus) {
                    if (requesturi.startsWith(menu.getUrl())) {
                        c.setAttr("menuURI", menu.getUrl());
                        break;
                    }
                }
            }
            invocation.invoke();
        } else {
            invocation.invoke();
        }
    }
}
