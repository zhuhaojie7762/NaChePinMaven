package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.SendOrderStatus;
import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class SendOrder implements Serializable {

    public static  final Function<SendOrder, Integer> tradeIdValue = new Function<SendOrder, Integer>() {
        @Override
        public Integer apply(SendOrder s) {
            return s.getTradeId();
        }
    };

    private int id;
    private String code;
    //客户ID
    private int customerId;
    private int provinceId;

    /**
     * 市
     */
    private int cityId;

    /**
     * 区县
     */
    private int countyId;

    //收货人信息
    private String buyerName;
    private String buyerAddress;
    private String buyerPostCode;
    private String buyerPhone;

    private int expressId;
    private String expressName;
    private String expressCode;
    private int sendUserId;
    private int orderCnt;
    private int weight;
    private long freight;
    private Date sendTime;
    private int tradeId;
    @EnumVal(EnumValType.Name)
    private SendOrderStatus status;
    private boolean isSend;
    private Date created;

    /**
     *实际重量
     */
    private int realityWeight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerPostCode() {
        return buyerPostCode;
    }

    public void setBuyerPostCode(String buyerPostCode) {
        this.buyerPostCode = buyerPostCode;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public int getExpressId() {
        return expressId;
    }

    public void setExpressId(int expressId) {
        this.expressId = expressId;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getExpressCode() {
        return expressCode;
    }

    public void setExpressCode(String expressCode) {
        this.expressCode = expressCode;
    }

    public int getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(int sendUserId) {
        this.sendUserId = sendUserId;
    }

    public int getOrderCnt() {
        return orderCnt;
    }

    public void setOrderCnt(int orderCnt) {
        this.orderCnt = orderCnt;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public long getFreight() {
        return freight;
    }

    public void setFreight(long freight) {
        this.freight = freight;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public boolean getIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public SendOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SendOrderStatus status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getRealityWeight() {
        return realityWeight;
    }

    public void setRealityWeight(int realityWeight) {
        this.realityWeight = realityWeight;
    }

    /**
     * 非持久属性
     */
    @Transient
    private String provinceName;
    @Transient
    private String cityName;
    @Transient
    private String countyName;
    @Transient
    private Date payTime;
    @Transient
    private long goodsNum;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    @Transient
    public String getFullBuyerAddress(){
        return provinceName + cityName + countyName + buyerAddress;
    }

    @Transient
    public String getTradeCode(){
        return Trade.mark + String.format("%05d", tradeId);
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(long goodsNum) {
        this.goodsNum = goodsNum;
    }
}
