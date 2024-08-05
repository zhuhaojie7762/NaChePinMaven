package com.aichebaba.rooftop.beetl.functions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.beetl.core.Context;
import org.beetl.core.Function;

public class JsonFunction implements Function {

    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length == 1) {
            String s = JSON.toJSONString(paras[0], SerializerFeature.DisableCircularReferenceDetect);
            return s.replaceAll("\"", "&quot;");
        } else if (paras.length == 2) {
            String s = JSON.toJSONString(paras[0], SerializerFeature.DisableCircularReferenceDetect);
            return s.replaceAll("\"", String.valueOf(paras[1]));
        }
        return "";
    }

}
