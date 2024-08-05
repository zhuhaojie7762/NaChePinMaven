package com.aichebaba.rooftop.interceptor;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class ResponseInterceptor implements Interceptor {
	@Override
	public void intercept(Invocation invocation) {
		invocation.invoke();
		HttpServletResponse response = invocation.getController().getResponse();
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragrma", "no-cache");
		response.setDateHeader("Expires", 0);
	}
}
