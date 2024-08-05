package com.aichebaba.rooftop.beetl.functions;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.utils.StringUtils;


public class ImageFunctions {
    public String fullPath(String path){
        if(StringUtils.isBlank(path)){
            return "";
        }
        String fullUrl = AppConfig.getImgPath();
        fullUrl += path;
        return fullUrl;
    }

    public String getS(String path){
        if(StringUtils.isBlank(path)){
            return "";
        }
        return AppConfig.getImgPath() + path + Constant.MARK_IMG_SIZE_S;
    }

    public String getM(String path){
        if(StringUtils.isBlank(path)){
            return "";
        }
        return AppConfig.getImgPath() + path + Constant.MARK_IMG_SIZE_M;
    }

    public String getL(String path){
        if(StringUtils.isBlank(path)){
            return "";
        }
        return AppConfig.getImgPath() + path + Constant.MARK_IMG_SIZE_L;
    }
}
