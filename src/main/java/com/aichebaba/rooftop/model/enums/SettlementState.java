package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum SettlementState implements EnumValue {
    noPay("未付", 0),
    payed("已付", 1);

    private String name;
    private int value;

    private SettlementState(String name, int value){
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
        SettlementState e = noPay;
        for (SettlementState state: SettlementState.values()) {
            if(state.value == value){
                e = state;
                break;
            }
        }
        return e;
    }
}
