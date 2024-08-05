package com.aichebaba.dao;

import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;

public class MathTest extends TestCase{
    public void testFloor() {
        System.out.println(Math.ceil(2.3));
        System.out.println(Math.floor(2.8));
    }

    public void testString() {
        String s = "{name}";
        System.out.println(s.replaceAll("\\{", "\\$\\{"));
    }

    public void testTemp() {
        String s = "【纳车品】 {user.name!}先生/小姐，您已经通过纳车品平台的{user.phone}资格申请，请登陆平台查看你的个人中心。";
        User user = new User();
        user.setName("sss");
        user.setPhone("111111");
        System.out.println(TemplateUtils.parseText(s, ImmutableMap.<String, Object>of("user", user)));
    }
}
