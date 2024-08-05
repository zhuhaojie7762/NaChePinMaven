package com.aichebaba.rooftop.model.enums;

public enum UserState {
    normal("在职"),
    leave("离职");

    private UserState(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
