package com.aichebaba.rooftop.ext;

import com.aichebaba.rooftop.config.AppConfig;
import org.beetl.core.Context;

public class ItemUrlFunction implements org.beetl.core.Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras == null || paras.length == 0) {
            throw new RuntimeException("没有该商品");
        }
        int id = (int) paras[0];
        String url = AppConfig.getItemBaseUrl() + id + ".html";
        return url;
    }
}
