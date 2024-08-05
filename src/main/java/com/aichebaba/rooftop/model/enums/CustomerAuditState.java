package com.aichebaba.rooftop.model.enums;

public enum CustomerAuditState {
    NO_AUDIT("未审核", 10),
    PASS("审核通过", 20),
    NO_PASS("审核未通过", 30);

    private String value;

    private int val;

    private CustomerAuditState(String value, int val) {
        this.value = value;
        this.val = val;
    }

    public String getValue() {
        return value;
    }

    public int getVal() {
        return val;
    }

    public Enum valOf(Object o) {
        int i = (int) o;
        CustomerAuditState e = null;
        switch (i) {
            case 10:
                e = NO_AUDIT;
                break;
            case 20:
                e = PASS;
                break;
            case 30:
                e = NO_PASS;
                break;
            default:
                e = null;
                break;
        }
        return e;
    }
}
