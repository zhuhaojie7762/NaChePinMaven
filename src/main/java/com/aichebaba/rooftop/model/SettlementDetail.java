package com.aichebaba.rooftop.model;

import java.io.Serializable;

import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

/**
 * 结算单明细
 */
public class SettlementDetail implements Serializable {
    public static final Function<SettlementDetail, String> ORDER_CODE_VALUE = new Function<SettlementDetail, String>() {
        @Override
        public String apply(SettlementDetail s) {
            return s.getOrderCode();
        }
    };
    private String settlementCode;
    private String orderCode;
    private int tradeId;
    private long money;
    @EnumVal(EnumValType.Manual)
    private SettlementDetailType type;

    public String getSettlementCode() {
        return settlementCode;
    }

    public void setSettlementCode(String settlementCode) {
        this.settlementCode = settlementCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public SettlementDetailType getType() {
        return type;
    }

    public void setType(SettlementDetailType type) {
        this.type = type;
    }
}
