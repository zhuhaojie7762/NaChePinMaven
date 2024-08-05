package com.aichebaba.rooftop.beetl.functions;

import org.beetl.ext.fn.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class StringFunctions extends StringUtil{
    public String decoding(String val) {
        try {
            return URLDecoder.decode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
