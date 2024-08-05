package com.aichebaba.rooftop.beetl.functions;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberFunctions {

    public static final DecimalFormat W_FORMAT = new DecimalFormat("#0.###");
    public static final DecimalFormat YUAN_FORMAT = new DecimalFormat("#0.00");

    public String g2k(Integer g) {
        if (g == null) {
            return "";
        }
        BigDecimal m = BigDecimal.valueOf(g);
        m = m.divide(new BigDecimal("1000.00"));
        return W_FORMAT.format(m);
    }

    public String yuan(Long fen) {
        if (fen == null) {
            return "";
        }
        BigDecimal m = BigDecimal.valueOf(fen);
        m = m.divide(new BigDecimal("100.00"));
        return YUAN_FORMAT.format(m);
    }
}
