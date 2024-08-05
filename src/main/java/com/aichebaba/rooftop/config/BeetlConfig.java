package com.aichebaba.rooftop.config;

import com.aichebaba.rooftop.beetl.functions.*;
import com.aichebaba.rooftop.beetl.tags.*;
import com.aichebaba.rooftop.ext.ItemUrlFunction;
import com.jfinal.config.Constants;
import com.jfinal.kit.PathKit;
import com.jfinal.render.IErrorRenderFactory;
import com.jfinal.render.Render;
import org.beetl.ext.jfinal.BeetlRenderFactory;

import static org.beetl.ext.jfinal.BeetlRenderFactory.groupTemplate;

public class BeetlConfig {
    public static void config(Constants me) {
        String templateRoot = PathKit.getWebRootPath() + "/WEB-INF/template";
        final BeetlRenderFactory mainRenderFactory = new BeetlRenderFactory(templateRoot);
        me.setMainRenderFactory(mainRenderFactory);
        me.setErrorRenderFactory((i, s) -> mainRenderFactory.getRender(s));
        groupTemplate.registerTag("page", PageTag.class);
        groupTemplate.registerTag("pagePlus", PageTagPlus.class);
        groupTemplate.registerTag("webPage", WebPageTag.class);
        groupTemplate.registerTag("webPagePlus", WebPageTagPlus.class);
        groupTemplate.registerTag("webPage2", WebPageTag2.class);
        groupTemplate.registerTag("auth", AuthTag.class);
        groupTemplate.registerFunctionPackage("nums", NumberFunctions.class);
        groupTemplate.registerFunctionPackage("dts", DateFunctions.class);
        groupTemplate.registerFunctionPackage("urls", UrlFunctions.class);
        groupTemplate.registerTag("item_url", ItemUrlTag.class);
        groupTemplate.registerFunction("item_url", new ItemUrlFunction());
        groupTemplate.registerTag("username", UsernameTag.class);
        groupTemplate.registerFunction("json", new JsonFunction());
        groupTemplate.registerFunctionPackage("img_url", ImageFunctions.class);
        groupTemplate.registerFunctionPackage("files", FileFunctions.class);
    }
}
