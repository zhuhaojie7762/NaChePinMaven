package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

import org.springframework.beans.factory.annotation.Value;

public class AlipayConfig {

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    @Value("${alipay.partner}")
    private String partner;

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    @Value("${alipay.seller_id}")
    private String sellerId;

    // MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    @Value("${alipay.key}")
    private String key;

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    @Value("${alipay.notify_url}")
    private String notifyUrl;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问

    @Value("${alipay.return_url}")
    private String returnUrl;

    // 签名方式
    @Value("${alipay.sign_type}")
    private String signType;

    // 字符编码格式 目前支持 gbk 或 utf-8
    @Value("${alipay.input_charset}")
    private String inputCharset;

    // 支付类型 ，无需修改
    @Value("${alipay.payment_type}")
    private String paymentType;

    // 调用的接口名，无需修改
    @Value("${alipay.service}")
    private String service;

//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    //↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    @Value("${alipay.antiPhishingKey}")
    private String antiPhishingKey;

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    @Value("${alipay.exterInvokeIp}")
    private String exterInvokeIp;
//↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    //支付超时时间
    @Value("${alipay.it_b_pay}")
    private String itBPay;

    //卖家支付宝账号，格式一般是邮箱或手机号。
    @Value("${alipay.seller_email}")
    private String sellerEmail;

    //卖家支付宝账号名称
    @Value("${alipay.seller_name}")
    private String sellerName;

    public String getPartner() {
        return partner;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getKey() {
        return key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getSignType() {
        return signType;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getService() {
        return service;
    }

    public String getAntiPhishingKey() {
        return antiPhishingKey;
    }

    public String getExterInvokeIp() {
        return exterInvokeIp;
    }

    public String getItBPay() {
        return itBPay;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public String getSellerName() {
        return sellerName;
    }
}

