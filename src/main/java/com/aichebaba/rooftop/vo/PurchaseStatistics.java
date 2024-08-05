package com.aichebaba.rooftop.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 进货商统计项目
 */
public class PurchaseStatistics implements Serializable {
    private int buyerId;                //进货商ID
    private String buyerUsername;       //进货商账号
    private String buyerName;           //进货商名称
    private long nullGoodsCnt;          //空包商品数
    private long realGoodsCnt;          //实物商品数
    private long nullOrderCnt;          //空包订单数
    private long realOrderCnt;          //实物订单数
    private long nullGoodsMoney;        //空包商品总金额
    private long realGoodsMoney;        //实物商品总金额
    private long postFee;               //运费总金额
    private long couponMoney;           //优惠券总金额
    private long packFee;               //打包袋总金额
    private long packSubsidy;           //打包袋优惠总金额
    private long historyTotalMoney;     //历史交易总额
    private long historyTotalCnt;       //历史交易单数
    private long refundMoney;           //退款退货总金额
    private Date firstPurchaseTime;     //第一次采购时间
    private Date lastPurchaseTime;      //最后一次采购时间

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public long getNullGoodsCnt() {
        return nullGoodsCnt;
    }

    public void setNullGoodsCnt(long nullGoodsCnt) {
        this.nullGoodsCnt = nullGoodsCnt;
    }

    public long getRealGoodsCnt() {
        return realGoodsCnt;
    }

    public void setRealGoodsCnt(long realGoodsCnt) {
        this.realGoodsCnt = realGoodsCnt;
    }

    public long getTotalGoodsCnt() {
        return nullGoodsCnt + realGoodsCnt;
    }

    public long getNullOrderCnt() {
        return nullOrderCnt;
    }

    public void setNullOrderCnt(long nullOrderCnt) {
        this.nullOrderCnt = nullOrderCnt;
    }

    public long getRealOrderCnt() {
        return realOrderCnt;
    }

    public void setRealOrderCnt(long realOrderCnt) {
        this.realOrderCnt = realOrderCnt;
    }

    public long getTotalOrderCnt() {
        return nullOrderCnt + realOrderCnt;
    }

    public long getNullGoodsMoney() {
        return nullGoodsMoney;
    }

    public void setNullGoodsMoney(long nullGoodsMoney) {
        this.nullGoodsMoney = nullGoodsMoney;
    }

    public long getRealGoodsMoney() {
        return realGoodsMoney;
    }

    public void setRealGoodsMoney(long realGoodsMoney) {
        this.realGoodsMoney = realGoodsMoney;
    }

    public long getRealGoodsAvgPrice() {
        if (realGoodsCnt == 0) {
            return 0;
        } else {
            return realGoodsMoney / realGoodsCnt;
        }
    }

    public long getTotalGoodsMoney() {
        return nullGoodsMoney + realGoodsMoney;
    }

    public long getPostFee() {
        return postFee;
    }

    public void setPostFee(long postFee) {
        this.postFee = postFee;
    }

    public long getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(long couponMoney) {
        this.couponMoney = couponMoney;
    }

    public long getPackFee() {
        return packFee;
    }

    public void setPackFee(long packFee) {
        this.packFee = packFee;
    }

    public long getPackSubsidy() {
        return packSubsidy;
    }

    public void setPackSubsidy(long packSubsidy) {
        this.packSubsidy = packSubsidy;
    }

    public long getHistoryTotalMoney() {
        return historyTotalMoney;
    }

    public void setHistoryTotalMoney(long historyTotalMoney) {
        this.historyTotalMoney = historyTotalMoney;
    }

    public long getHistoryTotalCnt() {
        return historyTotalCnt;
    }

    public void setHistoryTotalCnt(long historyTotalCnt) {
        this.historyTotalCnt = historyTotalCnt;
    }

    public long getHistoryAvgPrice() {
        if (historyTotalCnt == 0) {
            return 0;
        } else {
            return historyTotalMoney / historyTotalCnt;
        }
    }

    public long getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(long refundMoney) {
        this.refundMoney = refundMoney;
    }

    public Date getFirstPurchaseTime() {
        return firstPurchaseTime;
    }

    public void setFirstPurchaseTime(Date firstPurchaseTime) {
        this.firstPurchaseTime = firstPurchaseTime;
    }

    public Date getLastPurchaseTime() {
        return lastPurchaseTime;
    }

    public void setLastPurchaseTime(Date lastPurchaseTime) {
        this.lastPurchaseTime = lastPurchaseTime;
    }
}
