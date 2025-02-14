package com.aichebaba.rooftop.ue;

import java.util.HashMap;
import java.util.Map;

public final class ActionMap {
    public static final Map<String, Integer> mapping = new HashMap<>();
    public static final int CONFIG = 0;
    public static final int UPLOAD_IMAGE = 1;
    public static final int UPLOAD_SCRAWL = 2;
    public static final int UPLOAD_VIDEO = 3;
    public static final int UPLOAD_FILE = 4;
    public static final int CATCH_IMAGE = 5;
    public static final int LIST_FILE = 6;
    public static final int LIST_IMAGE = 7;

    static {
        mapping.put("config", 0);
        mapping.put("uploadimage", 1);
        mapping.put("uploadfile", 4);
        mapping.put("listfile", 4);
    }

    public static int getType(String key) {
        return ((Integer) mapping.get(key)).intValue();
    }
}
