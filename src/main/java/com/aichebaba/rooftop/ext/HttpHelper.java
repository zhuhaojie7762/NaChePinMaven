package com.aichebaba.rooftop.ext;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * httphelper
 * Created by He Daoyuan on 2016/5/23.
 */
public class HttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static String getResultAsString(HttpClient client, HttpUriRequest method) {
        try {
            HttpResponse resp = client.execute(method);
            return EntityUtils.toString(resp.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getResultAsString(HttpClient client, HttpPost method, List<NameValuePair> paramList) {
        try {
            method.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
            HttpResponse resp = client.execute(method);
            return EntityUtils.toString(resp.getEntity());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    public static JSONObject getResultAsJSON(HttpClient client, HttpGet method) {
        try {
            String str = getResultAsString(client, method);
            return JSONObject.parseObject(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    public static JSONObject getResultAsJSON(HttpClient client, HttpPost method) {
        try {
            String str = getResultAsString(client, method);
            return JSONObject.parseObject(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            method.releaseConnection();
        }

        return null;
    }

}
