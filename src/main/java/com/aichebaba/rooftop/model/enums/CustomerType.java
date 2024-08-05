package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum CustomerType implements EnumValue {
    normal("普通", 0),
    buyer("进货商", 1),
    seller("供货商", 2);

    private String name;
    private int value;

    private CustomerType(String name, int value){
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
        CustomerType e = normal;
        for (CustomerType type: CustomerType.values()) {
            if(type.value == value){
                e = type;
                break;
            }
        }
        return e;
    }
}
