package com.aichebaba.rooftop.model.enums;

public enum CustomerAuditType {
    PURCHASE("进货认证"), SUPPLIER("供货认证");

    private CustomerAuditType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
