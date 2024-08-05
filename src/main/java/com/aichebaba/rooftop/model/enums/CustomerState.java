package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum CustomerState implements EnumValue {
    normal("正常", 0),
    stop("停用", 1);

    private String name;
    private int value;

    private CustomerState(String name, int value){
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
        CustomerState e = normal;
        for (CustomerState state: CustomerState.values()) {
            if(state.value == value){
                e = state;
                break;
            }
        }
        return e;
    }
}
