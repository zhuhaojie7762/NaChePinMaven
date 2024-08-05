package com.aichebaba.rooftop.interceptor;

import com.aichebaba.rooftop.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(ErrorInterceptor.class);

    @Override
    public void intercept(Invocation invocation) {
        try{
            invocation.invoke();
        } catch (Exception e) {
            logger.error("error: ", e);
            BaseController c = (BaseController) invocation.getController();
            c.error(e.getMessage());
        }
    }
}
