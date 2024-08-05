package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

/**
 * Created by Andy on 2016/7/18.
 */
public enum PickOrderStatus implements EnumValue {
    WAIT_PICKING("待拣货", 1),
    PICK_ORDER("订单导出Excel", 10),
    FILL_EXPRESS("填写快递面单号", 20),
    PRINT_PICKING("打印拣货单", 30),
    DISTRIBUTION("发货确认", 40),
    FINISH("完成", 50);

    private PickOrderStatus(String name, int val) {
        this.name = name;
        this.val = val;
    }

    private String name;
    private int val;

    public String getName() {
        return name;
    }

    @Override
    public Object getVal() {
        return val;
    }

    @Override
    public Enum valOf(Object obj) {
        int i = (int) obj;
        PickOrderStatus e = PICK_ORDER;
        for (PickOrderStatus status : PickOrderStatus.values()) {
            if (status.val == i) {
                e = status;
                break;
            }
        }
        return e;
    }
}
