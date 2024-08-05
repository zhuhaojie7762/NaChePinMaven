package com.aichebaba.rooftop.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @auther huwhy
 * @date 2016/5/11.
 */
public class ActivityJobCustomerST implements Serializable {

    private int buyerId;

    private BigDecimal money = BigDecimal.ZERO;

    private long num;

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
