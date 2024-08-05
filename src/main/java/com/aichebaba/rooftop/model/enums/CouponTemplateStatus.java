package com.aichebaba.rooftop.model.enums;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
public enum CouponTemplateStatus {
    NORMAL("正常"),
    DISABLE("停用");

    private CouponTemplateStatus(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
