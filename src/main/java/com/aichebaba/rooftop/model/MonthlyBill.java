package com.aichebaba.rooftop.model;


import com.jfinal.plugin.activerecord.annotation.Sql;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Sql(insertSQL = "INSERT INTO monthly_bill (sellerId, month, startDay, endDay, totalNum, totalPayment, finishNum, finishPayment," +
        " cancelNum, cancelPayment, refundNum, refundPayment, refundGoodsNum, refundGoodsPayment, paid, paidDay)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        " on duplicate key update totalNum=VALUES(totalNum),  totalPayment=VALUES(totalPayment),  finishNum=VALUES(finishNum),  finishPayment=VALUES(finishPayment)," +
        " cancelNum=VALUES(cancelNum), cancelPayment=VALUES(cancelPayment), refundNum=VALUES(refundNum), refundPayment=VALUES(refundPayment)," +
        " refundGoodsNum=VALUES(refundGoodsNum), refundGoodsPayment=VALUES(refundGoodsPayment), paid=VALUES(paid), paidDay=VALUES(paidDay)")
public class MonthlyBill implements Serializable{

    private int sellerId;

    private Date month;

    private Date startDay;

    private Date endDay;

    private int totalNum;

    private long totalPayment;

    private int finishNum;

    private long finishPayment;

    private int cancelNum;

    private long cancelPayment;

    private int refundNum;

    private long refundPayment;

    private int refundGoodsNum;

    private long refundGoodsPayment;

    private boolean paid;

    private Date paidDay;

    private long paidPayment;

    private String remark;

    public MonthlyBill() {
    }

    public MonthlyBill(int sellerId, Date month) {
        this.sellerId = sellerId;
        this.month = month;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public int getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(int finishNum) {
        this.finishNum = finishNum;
    }

    public long getFinishPayment() {
        return finishPayment;
    }

    public void setFinishPayment(long finishPayment) {
        this.finishPayment = finishPayment;
    }

    public int getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(int cancelNum) {
        this.cancelNum = cancelNum;
    }

    public long getCancelPayment() {
        return cancelPayment;
    }

    public void setCancelPayment(long cancelPayment) {
        this.cancelPayment = cancelPayment;
    }

    public int getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(int refundNum) {
        this.refundNum = refundNum;
    }

    public long getRefundPayment() {
        return refundPayment;
    }

    public void setRefundPayment(long refundPayment) {
        this.refundPayment = refundPayment;
    }

    public int getRefundGoodsNum() {
        return refundGoodsNum;
    }

    public void setRefundGoodsNum(int refundGoodsNum) {
        this.refundGoodsNum = refundGoodsNum;
    }

    public long getRefundGoodsPayment() {
        return refundGoodsPayment;
    }

    public void setRefundGoodsPayment(long refundGoodsPayment) {
        this.refundGoodsPayment = refundGoodsPayment;
    }

    public Date getPaidDay() {
        return paidDay;
    }

    public void setPaidDay(Date paidDay) {
        this.paidDay = paidDay;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    private String sellerCode;

    private String supplierCompany;

    @Transient
    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    @Transient
    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public long getPaidPayment() {
        return paidPayment;
    }

    public void setPaidPayment(long paidPayment) {
        this.paidPayment = paidPayment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
