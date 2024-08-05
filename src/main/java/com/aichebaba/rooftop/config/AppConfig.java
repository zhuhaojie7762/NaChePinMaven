package com.aichebaba.rooftop.config;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class AppConfig {
    private static Prop prop;

    static {
        init();
    }

    public static void init() {
        if (prop == null) {
            prop = PropKit.use("app.properties", "utf-8");
        }
    }

    public static String getImgPath() {
        return prop.get("img.url");
    }

//    public static String getConsoleUrl() {
//        return prop.get("console.url");
//    }
//
//    public static String getSyncImgPaths() {
//        return prop.get("sync.img.paths");
//    }
//
//    public static String getSyncImageTargetRoot() {
//        return prop.get("sync.img.targetRoot");
//    }
//
//    public static boolean getSynOpen() {
//        return prop.getBoolean("sync.open", false);
//    }

    public static int getSmsType() {
        return prop.getInt("sms.type");
    }

    public static String getValue(String key) {
        return prop.get(key);
    }

    public static String getItemBaseUrl() {
        return prop.get("item.base.url");
    }

    public static String getRemindNumber() {
        return prop.get("warning.remind.number");
    }
}
