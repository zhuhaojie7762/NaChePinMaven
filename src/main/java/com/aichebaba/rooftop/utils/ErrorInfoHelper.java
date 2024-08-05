package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.config.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andy on 2016/6/28.
 */
public class ErrorInfoHelper {
    public void remind(String code) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒");
        code = "系统平台在『" + sdf.format(new Date()) + "』发生异常，异常代码：" + code + "，请查阅相关文档，并处理问题！";
        String[] numbers = AppConfig.getRemindNumber().split("#");
        for (String number : numbers) {
            SMSUtil.sendSmsMsg(number, code);
        }
    }
}
