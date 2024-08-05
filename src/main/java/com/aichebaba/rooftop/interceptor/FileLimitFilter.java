package com.aichebaba.rooftop.interceptor;

import com.aichebaba.rooftop.vo.Json;
import com.alibaba.fastjson.JSON;
import com.jfinal.core.JFinal;
import com.jfinal.render.RenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLimitFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(FileLimitFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        int length = request.getContentLength();
        int maxPostSize = JFinal.me().getConstants().getMaxPostSize();
        if (length > maxPostSize) {
            logger.error("Posted content length of " + length +
                    " exceeds limit of " + maxPostSize);
            Json json = Json.error("提交数据超过限制(" + (maxPostSize / 1024 / 1024) + "M)");

            PrintWriter writer = null;
            try {
                response.setHeader("Pragma",
                        "no-cache");    // HTTP/1.0 caches might not implement Cache-Control and might only implement Pragma: no-cache
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);

                response.setContentType("application/json; charset=utf-8");
                writer = response.getWriter();
                writer.write(JSON.toJSONString(json));
                writer.flush();
            } catch (IOException e) {
                throw new RenderException(e);
            } finally {
                if (writer != null)
                    writer.close();
            }

            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
