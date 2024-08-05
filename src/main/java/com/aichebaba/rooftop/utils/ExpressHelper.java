package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.ext.HttpHelper;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by HeDaoyuan on 2016/5/23.
 */
public class ExpressHelper {
    private static HttpClient client;

    static {
        client = HttpClients.custom().setMaxConnTotal(100).build();
    }

    public static JSONObject getExpressResult(String expressName, String expressId) {
        if (StrKit.isBlank(expressId)) {
            return null;
        }

        expressName = expressName.replace(" ", "");
        expressId = expressId.replace(" ", "");
        String url = "http://api.kuaidi100.com/api?id=e778c330ddaaaa98&com=" + expressName + "&nu=" + expressId + "&show=0&muti=1&order=desc";
        JSONObject json = null;
        try {
            HttpGet method = new HttpGet(url);
            json = HttpHelper.getResultAsJSON(client, method);
        }catch (Exception e){
            System.out.println("快递单号异常，请检查快递单号");
            return null;
        }
        return json;
    }
}
