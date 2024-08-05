package com.aichebaba.rooftop.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
/**
 * 自定义Handler
 * @author zhuhaojie
 *
 */
public class MyHandler extends Handler{

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
       //设置项目根路径
        request.setAttribute("ctx",request.getContextPath());
        //调用下一个拦截器
        nextHandler.handle(target, request, response, isHandled);
    }

}
