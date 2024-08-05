package com.aichebaba.rooftop.vo;

public enum Operator {
    EQ(1), GT(2), LT(3), GE(4), LE(5), BETWEEN(6), LIKE(7), IN(8), NIN(9), SET(10), NEQ(11), LIKES(12);

    private int value;

    public String getOp() {
        String op;
        switch (value) {
            case 1:
                op = " = ";
                break;
            case 2:
                op = " > ";
                break;
            case 3:
                op = " < ";
                break;
            case 4:
                op = " >= ";
                break;
            case 5:
                op = " <= ";
                break;
            case 6:
                op = " ";
                break;
            case 7:
                op = " like ";
                break;
            case 8:
                op = " in ";
                break;
            case 9:
                op = " not in ";
                break;
            case 10:
                op = " find_in_set ";
                break;
            case 11:
                op = " != ";
                break;
            case 12:
                op = " like ";
                break;
            default:
                op = null;
        }
        return op;
    }

     Operator(int v) {
        this.value = v;
    }

    public static Operator valueOf(int v) {
        Operator o;
        switch (v) {
            case 1:
                o = EQ;
                break;
            case 2:
                o = GT;
                break;
            case 3:
                o = LT;
                break;
            case 4:
                o = GE;
                break;
            case 5:
                o = LE;
                break;
            case 6:
                o = BETWEEN;
                break;
            case 7:
                o = LIKE;
                break;
            case 8:
                o = IN;
                break;
            case 9:
                o = NIN;
                break;
            case 10:
                o = SET;
                break;
            case 11:
                o = NEQ;
                break;
            case 12:
                o = LIKES;
                break;
            default:
                o = null;
        }
        return o;
    }
}
