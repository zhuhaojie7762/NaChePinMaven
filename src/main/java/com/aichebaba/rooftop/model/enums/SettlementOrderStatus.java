package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

/**
 * Created by zhuligang on 2016-8-17.
 */
public enum SettlementOrderStatus implements EnumValue {
    return_check_first("运营处退回", 10),
    wait_check_first("运营主管审核", 20),
    return_check_second("财务处退回", 30),
    wait_check_second("财务经理审核", 40),
    return_check_thirdly("总经理处退回", 50),
    wait_check_thirdly("总经理审核", 60),

    wait_pay("待付款",90),
    finish("已结算", 100);

    private String name;
    private int value;

    private SettlementOrderStatus(String name, int value){
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
        SettlementOrderStatus e = null;
        for (SettlementOrderStatus status: SettlementOrderStatus.values()) {
            if(status.value == value){
                e = status;
                break;
            }
        }
        return e;
    }

    public static SettlementOrderStatus getVariable(Integer value) {
        if (value != null) {
            for (SettlementOrderStatus sos : SettlementOrderStatus.values()) {
                if (value == sos.value)
                    return sos;
            }
        }
        return null;
    }
}
