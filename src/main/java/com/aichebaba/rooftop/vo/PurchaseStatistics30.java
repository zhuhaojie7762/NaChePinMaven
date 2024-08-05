package com.aichebaba.rooftop.vo;


import java.io.Serializable;

/**
 * 进货商统计近30天+近7天
 */
public class PurchaseStatistics30 implements Serializable {

    private int buyerId;                //进货商ID
    private String buyerUsername;       //进货商账号
    private String buyerName;           //进货商名称
    private long nullGoodsCnt30;        //空包商品数
    private long realGoodsCnt30;        //实物商品数
    private long nullOrderCnt30;        //空包订单数
    private long realOrderCnt30;        //实物订单数
    private long nullGoodsMoney30;      //空包商品总金额
    private long realGoodsMoney30;      //实物商品总金额
    private long postFee30;             //运费总金额
    private long couponMoney30;         //优惠券总金额
    private long packFee30;             //打包袋总金额
    private long packSubsidy30;         //打包袋优惠总金额
    private long refundMoney30;         //退款退货总金额
    private long nullGoodsCnt7;         //空包商品数
    private long realGoodsCnt7;         //实物商品数
    private long nullOrderCnt7;         //空包订单数
    private long realOrderCnt7;         //实物订单数
    private long nullGoodsMoney7;       //空包商品总金额
    private long realGoodsMoney7;       //实物商品总金额
    private long postFee7;              //运费总金额
    private long couponMoney7;          //优惠券总金额
    private long packFee7;              //打包袋总金额
    private long packSubsidy7;          //打包袋优惠总金额
    private long refundMoney7;          //退款退货总金额

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

    public long getNullGoodsCnt30() {
        return nullGoodsCnt30;
    }

    public void setNullGoodsCnt30(long nullGoodsCnt30) {
        this.nullGoodsCnt30 = nullGoodsCnt30;
    }

    public long getRealGoodsCnt30() {
        return realGoodsCnt30;
    }

    public void setRealGoodsCnt30(long realGoodsCnt30) {
        this.realGoodsCnt30 = realGoodsCnt30;
    }

    public long getTotalGoodsCnt30() {
        return nullGoodsCnt30 + realGoodsCnt30;
    }

    public long getNullOrderCnt30() {
        return nullOrderCnt30;
    }

    public void setNullOrderCnt30(long nullOrderCnt30) {
        this.nullOrderCnt30 = nullOrderCnt30;
    }

    public long getRealOrderCnt30() {
        return realOrderCnt30;
    }

    public void setRealOrderCnt30(long realOrderCnt30) {
        this.realOrderCnt30 = realOrderCnt30;
    }

    public long getTotalOrderCnt30() {
        return nullOrderCnt30 + realOrderCnt30;
    }

    public long getNullGoodsMoney30() {
        return nullGoodsMoney30;
    }

    public void setNullGoodsMoney30(long nullGoodsMoney30) {
        this.nullGoodsMoney30 = nullGoodsMoney30;
    }

    public long getRealGoodsMoney30() {
        return realGoodsMoney30;
    }

    public void setRealGoodsMoney30(long realGoodsMoney30) {
        this.realGoodsMoney30 = realGoodsMoney30;
    }

    public long getRealGoodsAvgPrice30() {
        if (realGoodsCnt30 == 0) {
            return 0;
        } else {
            return realGoodsMoney30 / realGoodsCnt30;
        }
    }

    public long getTotalGoodsMoney30() {
        return nullGoodsMoney30 + realGoodsMoney30;
    }

    public long getPostFee30() {
        return postFee30;
    }

    public void setPostFee30(long postFee30) {
        this.postFee30 = postFee30;
    }

    public long getCouponMoney30() {
        return couponMoney30;
    }

    public void setCouponMoney30(long couponMoney30) {
        this.couponMoney30 = couponMoney30;
    }

    public long getPackFee30() {
        return packFee30;
    }

    public void setPackFee30(long packFee30) {
        this.packFee30 = packFee30;
    }

    public long getPackSubsidy30() {
        return packSubsidy30;
    }

    public void setPackSubsidy30(long packSubsidy30) {
        this.packSubsidy30 = packSubsidy30;
    }

    public long getRefundMoney30() {
        return refundMoney30;
    }

    public void setRefundMoney30(long refundMoney30) {
        this.refundMoney30 = refundMoney30;
    }

    public long getNullGoodsCnt7() {
        return nullGoodsCnt7;
    }

    public void setNullGoodsCnt7(long nullGoodsCnt7) {
        this.nullGoodsCnt7 = nullGoodsCnt7;
    }

    public long getRealGoodsCnt7() {
        return realGoodsCnt7;
    }

    public void setRealGoodsCnt7(long realGoodsCnt7) {
        this.realGoodsCnt7 = realGoodsCnt7;
    }

    public long getTotalGoodsCnt7() {
        return nullGoodsCnt7 + realGoodsCnt7;
    }

    public long getNullOrderCnt7() {
        return nullOrderCnt7;
    }

    public void setNullOrderCnt7(long nullOrderCnt7) {
        this.nullOrderCnt7 = nullOrderCnt7;
    }

    public long getRealOrderCnt7() {
        return realOrderCnt7;
    }

    public void setRealOrderCnt7(long realOrderCnt7) {
        this.realOrderCnt7 = realOrderCnt7;
    }

    public long getTotalOrderCnt7() {
        return nullOrderCnt7 + realOrderCnt7;
    }

    public long getNullGoodsMoney7() {
        return nullGoodsMoney7;
    }

    public void setNullGoodsMoney7(long nullGoodsMoney7) {
        this.nullGoodsMoney7 = nullGoodsMoney7;
    }

    public long getRealGoodsMoney7() {
        return realGoodsMoney7;
    }

    public void setRealGoodsMoney7(long realGoodsMoney7) {
        this.realGoodsMoney7 = realGoodsMoney7;
    }

    public long getRealGoodsAvgPrice7() {
        if (realGoodsCnt7 == 0) {
            return 0;
        } else {
            return realGoodsMoney7 / realGoodsCnt7;
        }
    }

    public long getTotalGoodsMoney7() {
        return nullGoodsMoney7 + realGoodsMoney7;
    }

    public long getPostFee7() {
        return postFee7;
    }

    public void setPostFee7(long postFee7) {
        this.postFee7 = postFee7;
    }

    public long getCouponMoney7() {
        return couponMoney7;
    }

    public void setCouponMoney7(long couponMoney7) {
        this.couponMoney7 = couponMoney7;
    }

    public long getPackFee7() {
        return packFee7;
    }

    public void setPackFee7(long packFee7) {
        this.packFee7 = packFee7;
    }

    public long getPackSubsidy7() {
        return packSubsidy7;
    }

    public void setPackSubsidy7(long packSubsidy7) {
        this.packSubsidy7 = packSubsidy7;
    }

    public long getRefundMoney7() {
        return refundMoney7;
    }

    public void setRefundMoney7(long refundMoney7) {
        this.refundMoney7 = refundMoney7;
    }
}
