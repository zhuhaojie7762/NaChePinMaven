package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum SettlementDetailType implements EnumValue {
    sale("销售额", 1),
    back("退款额", 2);

    private String name;
    private int value;

    private SettlementDetailType(String name, int value){
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
        SettlementDetailType e = null;
        for (SettlementDetailType type: SettlementDetailType.values()) {
            if(type.value == value){
                e = type;
                break;
            }
        }
        return e;
    }
}
