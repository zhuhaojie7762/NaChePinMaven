package com.aichebaba.rooftop.model.enums;

public enum TruncType {
    normal("不取整"), ceil("向上取整"), round("四舍五入");

    private TruncType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
