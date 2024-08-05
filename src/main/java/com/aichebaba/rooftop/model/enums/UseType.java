package com.aichebaba.rooftop.model.enums;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
public enum UseType {
    POST_FEE("抵运费"),
    PAYMENT("抵货款");

    private UseType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
