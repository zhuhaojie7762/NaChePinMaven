package com.aichebaba.rooftop.vo;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016-12-15.
 */
public class AttrSearch {
    private String value;
    private String pid;
    private String name;

    public AttrSearch(String value, String pid, String name){
        this.value = value;
        this.pid = pid;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
