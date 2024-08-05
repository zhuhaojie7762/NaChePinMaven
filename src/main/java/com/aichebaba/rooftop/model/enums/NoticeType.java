package com.aichebaba.rooftop.model.enums;

public enum NoticeType {
    notice("平台公告"),
    dynamics("行业动态"),
    news("新闻资讯"),
    offline("下架信息");

    private String value;

    private NoticeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
