package com.aichebaba.utils;

import com.aichebaba.rooftop.utils.HttpUtils;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtilsTest extends TestCase {

    public void testSentMsg() throws UnsupportedEncodingException {
        String msg = "古时明月 先生/小姐，有客户于2016年03月 10:48下单贵公司的四季坐垫，货号是LDJ-WMZD-DJ，颜色是红, 尺寸是300mm*600mm， 数量是4，请备货。&needstatus=true";

        Map<String, String> params = new HashMap<>();
        params.put("account", "006059");
        params.put("pswd", "Sd123456");
        params.put("mobile", "15869018124");
        params.put("msg", URLEncoder.encode(msg, "UTF-8"));
        HttpUtils.get("http://120.26.69.248/msg/HttpSendSM", params);
    }

    public void testCalc() {
        System.out.println(200 * 1024 * 1024);
        System.out.println(Integer.MAX_VALUE);

    }

}
