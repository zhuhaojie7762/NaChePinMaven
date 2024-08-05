package com.aichebaba.rooftop.beetl.functions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFunctions {
    public String d2s(Date d){
        if(d == null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(d);
    }

    public String d2d(Date d){
        if(d == null){
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }
}
