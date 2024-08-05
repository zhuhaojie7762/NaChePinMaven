package com.aichebaba.rooftop.model.enums;

/**
 * 结算方式
 */
public enum SettlementMethodType {
    monthly("月结"),
    weekly("周结"),
    daily("现结");

    private String name;

    private SettlementMethodType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SettlementMethodType valOf(String val) {
        SettlementMethodType e = null;
        for (SettlementMethodType type : SettlementMethodType.values()) {
            if (type.name().equals(val)) {
                e = type;
                break;
            }
        }
        return e;
    }
}
