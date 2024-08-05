package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum SkuPropValueType implements EnumValue {
    spec("规格", 1),
    color("颜色", 2),
    size("尺寸", 3);

    private String name;
    private int value;

    private SkuPropValueType(String name, int value){
        this.name = name;
        this.value = value;
    }

    @Override
    public Object getVal() {
        return value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public Enum valOf(Object val) {
        int value = (int) val;
        SkuPropValueType e = spec;
        for (SkuPropValueType type: SkuPropValueType.values()) {
            if(type.spec.getValue() == value){
                e = type;
                break;
            }
        }
        return e;
    }
}
