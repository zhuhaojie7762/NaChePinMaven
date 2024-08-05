package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.beetl.functions.DateFunctions;
import com.aichebaba.rooftop.beetl.functions.NumberFunctions;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.ext.jfinal3.JFinal3BeetlRenderFactory;
import org.beetl.ext.web.ParameterWrapper;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.beetl.core.Configuration.defaultConfiguration;

public class TemplateUtils {
    public static String viewExtension = ".html";
    //public static GroupTemplate groupTemplate = new GroupTemplate(new Configuration(new Properties("beetl.properties")));

    private static GroupTemplate stringTemplate;

    static {
        try {
            Configuration conf = defaultConfiguration();
            conf.setEngine("org.beetl.core.engine.DefaultTemplateEngine");
            stringTemplate = new GroupTemplate(new StringTemplateResourceLoader(), conf);
            stringTemplate.registerFunctionPackage("nums", NumberFunctions.class);
            stringTemplate.registerFunctionPackage("dts", DateFunctions.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String html(String view, HttpServletRequest request) {
        if (!view.endsWith(viewExtension)) {
            view = view + viewExtension;
        }
        Template e = stringTemplate.getTemplate(view);

        Enumeration attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String webVariable = (String) attrs.nextElement();
            e.binding(webVariable, request.getAttribute(webVariable));
        }
        WebVariable webVariable = new WebVariable();
        webVariable.setRequest(request);
        e.binding("session", new SessionWrapper(request, request.getSession(false)));

        e.binding("servlet", webVariable);
        e.binding("request", request);
        e.binding("ctxPath", request.getContextPath());
        e.binding("$page", new HashMap());
        e.binding("parameter", new ParameterWrapper(request));
        return e.render();
    }

    public static String parseText(String template, Map<String, Object> data) {
        String text = template.replaceAll("\\{", "\\$\\{");
        Template t = stringTemplate.getTemplate(text);
        t.binding(data);
        return t.render();
    }

    public static void staticHtml(String view, String staticRoot, String htmlName,
                                  HttpServletRequest request) throws FileNotFoundException {
        if (!view.endsWith(viewExtension)) {
            view = view + viewExtension;
        }
        Template e = stringTemplate.getTemplate(view);

        Enumeration attrs = request.getAttributeNames();
        while (attrs.hasMoreElements()) {
            String webVariable = (String) attrs.nextElement();
            e.binding(webVariable, request.getAttribute(webVariable));
        }
        WebVariable webVariable = new WebVariable();
        webVariable.setRequest(request);
        e.binding("session", new SessionWrapper(request, request.getSession(false)));

        e.binding("servlet", webVariable);
        e.binding("request", request);
        e.binding("ctxPath", request.getContextPath());
        e.binding("$page", new HashMap());
        e.binding("parameter", new ParameterWrapper(request));
        File root = new File(staticRoot);
        if (!root.exists()) {
            root.mkdirs();
        }

        PrintWriter writer = new PrintWriter(new File(root, htmlName));
        try {
            e.renderTo(writer);
            writer.flush();
        } finally {
            writer.close();
        }
    }
}