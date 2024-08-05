package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum AllocatingStatus implements EnumValue {
    normal("普通", 0),
    unfinished("配货未完成", 1);

    private String name;
    private int value;

    private AllocatingStatus(String name, int value){
        this.name = name;
        this.value = value;
    }

    @Override
    public Object getVal() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public Enum valOf(Object val) {
        int value = (int) val;
        AllocatingStatus e = normal;
        for (AllocatingStatus state: AllocatingStatus.values()) {
            if(state.value == value){
                e = state;
                break;
            }
        }
        return e;
    }
}
