package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

/**
 * Created by Andy on 2016/8/16.
 */
public enum AccountPayeeStatus implements EnumValue {
    PENDING("待审核", 0),
    PASS("审核通过", 1),
    REJECT("审核不通过", 2);

    private AccountPayeeStatus(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private Integer value;

    public String getName() {
        return name;
    }

    @Override
    public Object getVal() {
        return value;
    }

    @Override
    public Enum valOf(Object obj) {
        int value = (int) obj;
        AccountPayeeStatus e = PENDING;
        for (AccountPayeeStatus status : AccountPayeeStatus.values()) {
            if (status.value == value) {
                e = status;
                break;
            }
        }
        return e;
    }
}
