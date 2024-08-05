package com.aichebaba.rooftop.model.enums;

import com.jfinal.plugin.activerecord.annotation.EnumValue;

public enum GoodsSubmitType implements EnumValue {
	seller("供货商", 0), admin("后台", 1), unknow("未知", 2);

    private String name;
    private int value;

    private GoodsSubmitType(String name, int value) {
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
	public Enum<GoodsSubmitType> valOf(Object val) {
        int value = (int) val;
		GoodsSubmitType e = unknow;
        for (GoodsSubmitType status : GoodsSubmitType.values()) {
            if (status.value == value) {
                e = status;
                break;
            }
        }
        return e;
    }

	public static GoodsSubmitType valOf(Integer val) {
		GoodsSubmitType e = unknow;
		for (GoodsSubmitType status : GoodsSubmitType.values()) {
			if (status.getVal().equals(val)) {
				e = status;
				break;
			}
		}
		return e;
	}
}
