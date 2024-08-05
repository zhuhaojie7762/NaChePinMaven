package com.aichebaba.rooftop.model.enums;

public enum CouponSendType {
    AUTO("自动"),
    MANU("手动");

    private CouponSendType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
