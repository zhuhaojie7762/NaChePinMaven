package com.aichebaba.rooftop.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @auther huwhy
 * @date 2016/4/27
 * @name 供货商交易统计项
 */
public class ProviderStatistics implements Serializable {

    private int sellerId;
    private String sellerCode;
    private String sellerUsername;
    private String sellerName;

    private long totalPayment;
    private int totalOrderNum;
    private long totalMoney;
    private long totalPostFee;

    private long finishedPayment;
    private int finishedNum;
    private long finishedMoney;
    private long finishedPostFee;

    private long dealingPayment;
    private int dealingNum;
    private long dealingMoney;
    private long dealingPostFee;

    private long refundFee;
    private int refundNum;
    private long refundMoney;
    private long refundPostFee;

    private long closedPayment;
    private int closedNum;
    private long closedMoney;

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public int getTotalOrderNum() {
        return totalOrderNum;
    }

    public void setTotalOrderNum(int totalOrderNum) {
        this.totalOrderNum = totalOrderNum;
    }

    public long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public long getTotalPostFee() {
        return totalPostFee;
    }

    public void setTotalPostFee(long totalPostFee) {
        this.totalPostFee = totalPostFee;
    }

    public long getFinishedPayment() {
        return finishedPayment;
    }

    public void setFinishedPayment(long finishedPayment) {
        this.finishedPayment = finishedPayment;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }

    public long getFinishedMoney() {
        return finishedMoney;
    }

    public void setFinishedMoney(long finishedMoney) {
        this.finishedMoney = finishedMoney;
    }

    public long getFinishedPostFee() {
        return finishedPostFee;
    }

    public void setFinishedPostFee(long finishedPostFee) {
        this.finishedPostFee = finishedPostFee;
    }

    public long getDealingPayment() {
        return dealingPayment;
    }

    public void setDealingPayment(long dealingPayment) {
        this.dealingPayment = dealingPayment;
    }

    public int getDealingNum() {
        return dealingNum;
    }

    public void setDealingNum(int dealingNum) {
        this.dealingNum = dealingNum;
    }

    public long getDealingMoney() {
        return dealingMoney;
    }

    public void setDealingMoney(long dealingMoney) {
        this.dealingMoney = dealingMoney;
    }

    public long getDealingPostFee() {
        return dealingPostFee;
    }

    public void setDealingPostFee(long dealingPostFee) {
        this.dealingPostFee = dealingPostFee;
    }

    public long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(long refundFee) {
        this.refundFee = refundFee;
    }

    public int getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(int refundNum) {
        this.refundNum = refundNum;
    }

    public long getRefundMoney() {
        return refundMoney;
    }

    public void setRefundMoney(long refundMoney) {
        this.refundMoney = refundMoney;
    }

    public long getRefundPostFee() {
        return refundPostFee;
    }

    public void setRefundPostFee(long refundPostFee) {
        this.refundPostFee = refundPostFee;
    }

    public long getClosedPayment() {
        return closedPayment;
    }

    public void setClosedPayment(long closedPayment) {
        this.closedPayment = closedPayment;
    }

    public int getClosedNum() {
        return closedNum;
    }

    public void setClosedNum(int closedNum) {
        this.closedNum = closedNum;
    }

    public long getClosedMoney() {
        return closedMoney;
    }

    public void setClosedMoney(long closedMoney) {
        this.closedMoney = closedMoney;
    }
}
