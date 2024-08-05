package com.aichebaba.rooftop.beetl.functions;

import com.jfinal.kit.StrKit;

public class FileFunctions {
    public String getFileName(String path){
        if(StrKit.isBlank(path)){
            return "";
        }
        int pos=path.lastIndexOf("/");
        return path.substring(pos+1);
    }
}
