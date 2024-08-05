package com.aichebaba.rooftop.interceptor;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.service.CustomerService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CommonInterceptor implements Interceptor {

    private CustomerService customerService;
    private static Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

    @Override
    public void intercept(Invocation invocation) {
        BaseController c = (BaseController) invocation.getController();
//        logger.debug("instance hash : {}", c);

        Enumeration<String> names = c.getParaNames();
        Map<String, Object> params = new HashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, c.getPara(name));
        }
        c.setAttr("params", params);
        if(!c.isAjax()) {
            c.setAttr("referer", c.getReferer());
        }

        String cookieUserId = c.getCookie(Constant.COOKIE_CUSTOMER);
        if (c.getCurCustomer() == null && StrKit.notBlank(cookieUserId)) {
            int id = Integer.valueOf(cookieUserId, 2);
            c.setCurCustomer(getCustomer(id));
        }
        c.setAttr("imgPath", AppConfig.getImgPath());

        invocation.invoke();
    }

    /**
     * 根据ID获取客户信息
     * @param id 客户ID
     * @return
     */
    private Customer getCustomer(int id) {
        if (customerService == null) {
            customerService = SpringUtils.getBean(CustomerService.class);
        }
        return customerService.getNormalCustomerById(id);
    }

}
