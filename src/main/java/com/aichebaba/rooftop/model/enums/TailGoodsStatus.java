package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum TailGoodsStatus implements EnumValue {
    wait("待审核", 0),
    online("上架", 1),
    offline("下架", 2);

    private String name;
    private int value;

    private TailGoodsStatus(String name, int value){
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
        TailGoodsStatus e = wait;
        for (TailGoodsStatus status: TailGoodsStatus.values()) {
            if(status.value == value){
                e = status;
                break;
            }
        }
        return e;
    }
}
