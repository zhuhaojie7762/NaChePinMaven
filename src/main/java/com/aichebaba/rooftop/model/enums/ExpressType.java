package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

/**
 * Created by Administrator on 2016-5-26.
 */
public enum ExpressType implements EnumValue {
    logistics("物流", 1),
    express("快递", 2);

    private String name;
    private int value;

    private ExpressType(String name, int value){
        this.name = name;
        this.value = value;
    }

    @Override
    public Object getVal() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public Enum valOf(Object val) {
        int value = (int) val;
        ExpressType e = null;
        for (ExpressType type: ExpressType.values()) {
            if(type.value == value){
                e = type;
                break;
            }
        }
        return e;
    }
}
