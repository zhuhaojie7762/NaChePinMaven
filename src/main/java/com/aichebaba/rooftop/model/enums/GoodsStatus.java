package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum GoodsStatus implements EnumValue {
    wait_audit("待审核", 0),
    back_audit("退回", 5),
    online("上架", 10),
    offline("下架", 20),
	deleted("已删除", 30);

    private String name;
    private int value;

    private GoodsStatus(String name, int value) {
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
	public Enum<GoodsStatus> valOf(Object val) {
        int value = (int) val;
        GoodsStatus e = wait_audit;
        for (GoodsStatus status : GoodsStatus.values()) {
            if (status.value == value) {
                e = status;
                break;
            }
        }
        return e;
    }

	public static GoodsStatus valOf(Integer val) {
		GoodsStatus e = wait_audit;
		for (GoodsStatus status : GoodsStatus.values()) {
			if (status.getVal().equals(val)) {
				e = status;
				break;
			}
		}
		return e;
	}
}
