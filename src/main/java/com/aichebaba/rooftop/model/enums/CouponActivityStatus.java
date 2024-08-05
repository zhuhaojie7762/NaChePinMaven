package com.aichebaba.rooftop.model.enums;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
public enum CouponActivityStatus {
    CREATED("新建"),
    IN("进行中"),
    FINISHED("结束");

    private String value;

    private CouponActivityStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
