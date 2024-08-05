package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.config.AppConfig;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;


/**
 * 短信发送类
 *
 * @author zhangy
 */
public class SMSUtil {
    private static final Logger logger = LoggerFactory.getLogger(SMSUtil.class);

    public static void sendSMSCode(String phone, String code) {
        int smsType = AppConfig.getSmsType();
        switch (smsType) {
            case 0:
                break;
            case 1:     //示远
                SMSUtil.sendShiyuanSmscode(phone, code);
                break;
        }
    }

    public static void sendSmsMsg(String phone, String msg) {
        int smsType = AppConfig.getSmsType();
        switch (smsType) {
            case 0:
                break;
            case 1:     //示远
                sendShiyuanSmsMsg(phone, msg);
                break;
        }
    }

    public static void sendSmsMsg2(String phone, String msg){
        int smsType = AppConfig.getSmsType();
        switch (smsType) {
            case 0:
                break;
            case 1:     //示远
                sendShiYuanSmsMsg2(phone, msg);
                break;
        }
    }

    /**
     * 16位md5加密
     */
    public final static String digest(String plainText) {

        String md5Str = null;
        try {

            StringBuffer buf = new StringBuffer();

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plainText.getBytes());

            byte b[] = md.digest();
            int i;

            for (int offset = 0; offset < b.length; offset++) {

                i = b[offset];

                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));

            }

            md5Str = buf.toString().substring(8, 24);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    /**
     * JSON+HTTP POST
     *
     * @param url
     * @return String
     */
    public static String httpPost(String url, String mobile, NameValuePair[] data) throws Exception {
        HttpClient httpClient = new HttpClient();
        // HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        // 设置编码,httppost同时会用编码进行url.encode
        httpClient.getParams().setContentCharset("UTF-8");
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        // 将表单的值放入postMethod中
        postMethod.setRequestBody(data);
        // 执行postMethod
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
        // 301或者302
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            // 从头中取出转向的地址
            Header locationHeader = postMethod.getResponseHeader("location");
            String location = null;
            if (locationHeader != null) {
                location = locationHeader.getValue();
                logger.error("The page was redirected to:" + location);
            } else {
                logger.error("Location field value is null.");
            }
            return null;
        } else {
            String str = "";
            try {
                str = HttpTextUtil.getRsString(postMethod.getResponseBodyAsStream());
                logger.debug("SMS-result:手机号(" + mobile + ");返回结果：" + str);
                return str;
            } catch (IOException e) {
                logger.error(e.toString(), e);
            } finally {
                postMethod.releaseConnection();
            }
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, MalformedURLException {
//        SMSUtil.sendShiyuanSmscode("13858084377", "1234");
        SMSUtil.sendShiyuanSmsMsg("13858084377","【纳车品】有客户（电话号码12345678901）需要物流，请及时与他联系。");
    }

    /**
     * 服务商 示远
     *
     * @param mobile 用户手机号
     * @param code   验证码
     */
    private static void sendShiyuanSmscode(String mobile,
            String code) {
        String account = "006059";
        String pswd = "Sd123456";
//        String smsUrl = "http://120.26.69.248/msg/HttpSendSM";
        String smsUrl = "http://send.18sms.com/msg/HttpBatchSendSM";
        String msg = "您的验证码是" + code + "。如非本人操作，请忽略本短信。";

        String params = "account=" + account + "&pswd=" + pswd + "&mobile=" + mobile + "&msg=" + msg + "&needstatus=true";

        logger.debug("示远:SMS-result:手机号(" + mobile + ");返回结果：" + code);
        try {
            HttpKit.sendGet(smsUrl, params);
            logger.debug("短信验证码发送成功!");
        } catch (Exception e) {
            logger.debug("短信验证码发送失败!");
        }
    }

    /**
     * 服务商 示远
     *
     * @param mobile 用户手机号
     * @param msg    验证码
     */
    private static void sendShiyuanSmsMsg(String mobile, String msg) {
        String account = "006059";
        String pswd = "Sd123456";
//        String smsUrl = "http://120.26.69.248/msg/HttpSendSM";
        String smsUrl = "http://send.18sms.com/msg/HttpBatchSendSM";

        String params = "account=" + account + "&pswd=" + pswd + "&mobile=" + mobile + "&msg=" + msg + "&needstatus=true";

        logger.debug("示远:SMS-result:手机号(" + mobile + ");内容:" + msg);
        try {
            HttpKit.sendGet(smsUrl, params);
            logger.debug("短信验证码发送成功!");
        } catch (Exception e) {
            logger.error("短信验证码发送失败!", e);
        }
    }

    /**
     * 服务商 示远
     *
     * @param mobile 用户手机号
     * @param msg    验证码
     */
    private static void sendShiYuanSmsMsg2(String mobile, String msg) {
        String account = "006059";
        String pswd = "Sd123456";
//        String smsUrl = "http://120.26.69.248/msg/HttpSendSM";
        String smsUrl = "http://send.18sms.com/msg/HttpBatchSendSM";
        try {
            Map<String, String> params = new HashMap<>();
            params.put("account", account);
            params.put("pswd", pswd);
            params.put("mobile", mobile);
            params.put("msg", URLEncoder.encode(msg, "UTF-8"));
            HttpUtils.get(smsUrl, params);
        } catch (UnsupportedEncodingException e) {
            logger.error("shi yuan sent msg error", e);
        }
    }
}
