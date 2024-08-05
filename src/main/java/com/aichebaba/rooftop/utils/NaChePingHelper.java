package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.config.Constant;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class NaChePingHelper {
    private static Logger logger = LoggerFactory.getLogger(NaChePingHelper.class);

    // 获取Up云服务器的Key
    public static Map<String, String> policy(String type) {
        Map<String, String> param = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("bucket", "nachepin-storage");
        info.put("expiration", String.valueOf(System.currentTimeMillis() + 60 * 60 - 8 * 60 * 60));
        if ("file".equals(type)) {
            info.put("save-key", "/file/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        } else {
            info.put("save-key", "/images/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        }
        String policy = JSON.toJSONString(info);
        policy = Base64.encode(policy, "UTF-8");
        String signature = policy + "&" + Constant.UPYUN_SIGNATURE;
        try {
            signature = MD5.md5(signature, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("生成Up云数字签名失败");
        }
        param.put("policy", policy);
        param.put("signature", signature);
        return param;
    }
}
