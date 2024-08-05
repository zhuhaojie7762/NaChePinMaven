package com.aichebaba.rooftop.vo;

import java.io.Serializable;

/**
 * @auther huwhy
 * @date 2016/5/10.
 */
public class CouponActivityCondition implements Serializable{

    private Boolean chkPreMonthMoney;
    private long preMonthMoney;

    private Boolean chkPreMonthNum;
    private int preMonthNum;

    private Boolean chkPreSeasonMoney;
    private long preSeasonMoney;

    private Boolean chkPreSeasonNum;
    private int preSeasonNum;

    public Boolean getChkPreMonthMoney() {
        return chkPreMonthMoney;
    }

    public void setChkPreMonthMoney(Boolean chkPreMonthMoney) {
        this.chkPreMonthMoney = chkPreMonthMoney;
    }

    public long getPreMonthMoney() {
        return preMonthMoney;
    }

    public void setPreMonthMoney(long preMonthMoney) {
        this.preMonthMoney = preMonthMoney;
    }

    public Boolean getChkPreMonthNum() {
        return chkPreMonthNum;
    }

    public void setChkPreMonthNum(Boolean chkPreMonthNum) {
        this.chkPreMonthNum = chkPreMonthNum;
    }

    public int getPreMonthNum() {
        return preMonthNum;
    }

    public void setPreMonthNum(int preMonthNum) {
        this.preMonthNum = preMonthNum;
    }

    public Boolean getChkPreSeasonMoney() {
        return chkPreSeasonMoney;
    }

    public void setChkPreSeasonMoney(Boolean chkPreSeasonMoney) {
        this.chkPreSeasonMoney = chkPreSeasonMoney;
    }

    public long getPreSeasonMoney() {
        return preSeasonMoney;
    }

    public void setPreSeasonMoney(long preSeasonMoney) {
        this.preSeasonMoney = preSeasonMoney;
    }

    public Boolean getChkPreSeasonNum() {
        return chkPreSeasonNum;
    }

    public void setChkPreSeasonNum(Boolean chkPreSeasonNum) {
        this.chkPreSeasonNum = chkPreSeasonNum;
    }

    public int getPreSeasonNum() {
        return preSeasonNum;
    }

    public void setPreSeasonNum(int preSeasonNum) {
        this.preSeasonNum = preSeasonNum;
    }
}
