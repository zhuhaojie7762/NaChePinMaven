package com.aichebaba.rooftop.model.enums;

public enum BannerType {
    main("首页Banner"),
    cushion("坐垫/座套"),
    floorMat("脚垫/后备箱垫"),
    wheelCover("方向盘套"),
    carTrim("功能小件");

    private String value;

    private BannerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
