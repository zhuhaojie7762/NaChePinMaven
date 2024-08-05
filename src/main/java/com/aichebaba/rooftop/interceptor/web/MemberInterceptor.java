package com.aichebaba.rooftop.interceptor.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.service.CustomerService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.spring.SpringUtils;

public class MemberInterceptor implements Interceptor {

    private CustomerService customerService;

    @Override
    public void intercept(Invocation invocation) {
        if (invocation.getActionKey().startsWith("/member")) {
            BaseController c = (BaseController) invocation.getController();
            if(c.curCustomerId() > 0) {
                c.setCurCustomer(getCustomer(c.curCustomerId()));
            }
            invocation.invoke();
        }else{
            invocation.invoke();
        }
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
