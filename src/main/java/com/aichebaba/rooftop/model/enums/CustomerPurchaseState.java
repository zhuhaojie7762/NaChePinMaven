package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum CustomerPurchaseState implements EnumValue {
    NO_APPLICATION("无申请", -1),
    NO_AUDIT("未审核", 0),
    PASS("审核通过", 1),
    NO_PASS("审核未通过", 2);

    private String name;
    private int value;

    private CustomerPurchaseState(String name, int value){
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
        CustomerPurchaseState e = NO_APPLICATION;
        for (CustomerPurchaseState state: CustomerPurchaseState.values()) {
            if(state.value == value){
                e = state;
                break;
            }
        }
        return e;
    }
}
