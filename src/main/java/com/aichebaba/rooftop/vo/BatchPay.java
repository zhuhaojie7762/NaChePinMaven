package com.aichebaba.rooftop.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-8-28.
 */
public class BatchPay implements Serializable {
    private String payeeCode;       //收款人账号
    private String payeeName;       //收款人姓名
    private Long money;             //金额(分)
    private String remark;          //理由

    public String getPayeeCode() {
        return payeeCode;
    }

    public void setPayeeCode(String payeeCode) {
        this.payeeCode = payeeCode;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
