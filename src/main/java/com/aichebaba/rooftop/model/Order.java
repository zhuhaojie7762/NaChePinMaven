package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.PickOrderStatus;
import com.google.common.base.Function;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {

    public static final Function<Order, Integer> tradeIdValue = new Function<Order, Integer>() {
        @Override
        public Integer apply(Order order) {
            return order.getTradeId();
        }
    };

    public static final Function<Order, Integer> buyerIdValue = new Function<Order, Integer>() {
        @Override
        public Integer apply(Order order) {
            return order.getBuyerId();
        }
    };

    public static final Function<Order, String> codeValue = new Function<Order, String>() {
        @Override
        public String apply(Order order) {
            return order.getCode();
        }
    };

    public static final Function<Order, String> GOODS_CODE_VALUE = new Function<Order, String>() {
        @Override
        public String apply(Order g) {
            return g.getGoodsCode();
        }
    };
    public static final Function<Order, Integer> sellerIdValue = new Function<Order, Integer>() {
        @Override
        public Integer apply(Order o) {
            return o.getSellerId();
        }
    };

    private String code;
    private int tradeId;
    private Date payTime;
    private Date created;
    private String goodsCode;
    private int skuId;
    private String properties;
    private String specPropValue;
    private String goodsItemNo;
    private String goodsName;
    private int buyerId;
    private String buyerPhone;
    private int sellerId;
    private String sellerPhone;
    private int quantity;
    private long price;
    private long oldPrice;
    private long money;
    private long payment;
    private int weight;
    private String color;
    private String size;
    private String fitCar;
    private Boolean isSpecial;
    @EnumVal(EnumValType.Manual)
    private OrderStatus status;
    private int pickerId;
    private Date startPickTime;
    private Date pickTime;
    private Date waitDeliverTime;
    private int sendOrderId;
    private String pickOrderCode;
    private Date applyRefundTime;
    private int refundUserId;
    private String refundReason;
    private String refundDeal;
    private long refundFee;
    private String refundExpressCompany;
    private String refundExpressCode;
    private Date refundConsignTime;
    private Date refundConfirmTime;
    private String refundDisputeImg;
    private int isCargo;
    @EnumVal(EnumValType.Manual)
    private OrderStatus applyRefundFlag;
    @EnumVal(EnumValType.Manual)
    private OrderStatus orderStatus;
    @EnumVal(EnumValType.Manual)
    private OrderStatus cancelRefundStatus;
    private Integer cancelRefundFlag;
    private String disagreeRefundReason;
    private String negotiationResult;
    private long negotiationRefundMoney;
    private String buyerMessage;
    private String sellerMessage;
    private Date endTime;
    private boolean picking;
    private long platformMoney;
    private long supplierMoney;
    private long expressMoney;
    private long stockMoney;
    private String afterNote;
    private Date updatePriceTime;
    private int updatePriceUserId;

    public String getAfterNote() {
        return afterNote;
    }

    public void setAfterNote(String afterNote) {
        this.afterNote = afterNote;
    }

    public Integer getCancelRefundFlag() {
        return cancelRefundFlag;
    }

    public void setCancelRefundFlag(Integer cancelRefundFlag) {
        this.cancelRefundFlag = cancelRefundFlag;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getApplyRefundFlag() {
        return applyRefundFlag;
    }

    public void setApplyRefundFlag(OrderStatus applyRefundFlag) {
        this.applyRefundFlag = applyRefundFlag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getSpecPropValue() {
        return specPropValue;
    }

    public void setSpecPropValue(String specPropValue) {
        this.specPropValue = specPropValue;
    }

    public String getGoodsItemNo() {
        return goodsItemNo;
    }

    public void setGoodsItemNo(String goodsItemNo) {
        this.goodsItemNo = goodsItemNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getPayment() {
        return payment;
    }

    public void setPayment(long payment) {
        this.payment = payment;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFitCar() {
        return fitCar;
    }

    public void setFitCar(String fitCar) {
        this.fitCar = fitCar;
    }

    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getPickerId() {
        return pickerId;
    }

    public void setPickerId(int pickerId) {
        this.pickerId = pickerId;
    }

    public Date getPickTime() {
        return pickTime;
    }

    public void setPickTime(Date pickTime) {
        this.pickTime = pickTime;
    }

    public Date getStartPickTime() {
        return startPickTime;
    }

    public void setStartPickTime(Date startPickTime) {
        this.startPickTime = startPickTime;
    }

    public Date getWaitDeliverTime() {
        return waitDeliverTime;
    }

    public void setWaitDeliverTime(Date waitDeliverTime) {
        this.waitDeliverTime = waitDeliverTime;
    }

    public int getSendOrderId() {
        return sendOrderId;
    }

    public void setSendOrderId(int sendOrderId) {
        this.sendOrderId = sendOrderId;
    }

    public String getPickOrderCode() {
        return pickOrderCode;
    }

    public void setPickOrderCode(String pickOrderCode) {
        this.pickOrderCode = pickOrderCode;
    }

    public Date getApplyRefundTime() {
        return applyRefundTime;
    }

    public void setApplyRefundTime(Date applyRefundTime) {
        this.applyRefundTime = applyRefundTime;
    }

    public int getRefundUserId() {
        return refundUserId;
    }

    public void setRefundUserId(int refundUserId) {
        this.refundUserId = refundUserId;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRefundDeal() {
        return refundDeal;
    }

    public void setRefundDeal(String refundDeal) {
        this.refundDeal = refundDeal;
    }

    public long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(long refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundExpressCompany() {
        return refundExpressCompany;
    }

    public void setRefundExpressCompany(String refundExpressCompany) {
        this.refundExpressCompany = refundExpressCompany;
    }

    public String getRefundExpressCode() {
        return refundExpressCode;
    }

    public void setRefundExpressCode(String refundExpressCode) {
        this.refundExpressCode = refundExpressCode;
    }

    public Date getRefundConsignTime() {
        return refundConsignTime;
    }

    public void setRefundConsignTime(Date refundConsignTime) {
        this.refundConsignTime = refundConsignTime;
    }

    public Date getRefundConfirmTime() {
        return refundConfirmTime;
    }

    public void setRefundConfirmTime(Date refundConfirmTime) {
        this.refundConfirmTime = refundConfirmTime;
    }

    public String getDisagreeRefundReason() {
        return disagreeRefundReason;
    }

    public void setDisagreeRefundReason(String disagreeRefundReason) {
        this.disagreeRefundReason = disagreeRefundReason;
    }

    public String getNegotiationResult() {
        return negotiationResult;
    }

    public void setNegotiationResult(String negotiationResult) {
        this.negotiationResult = negotiationResult;
    }

    public long getNegotiationRefundMoney() {
        return negotiationRefundMoney;
    }

    public void setNegotiationRefundMoney(long negotiationRefundMoney) {
        this.negotiationRefundMoney = negotiationRefundMoney;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getSellerMessage() {
        return sellerMessage;
    }

    public void setSellerMessage(String sellerMessage) {
        this.sellerMessage = sellerMessage;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean getPicking() {
        return picking;
    }

    public void setPicking(boolean picking) {
        this.picking = picking;
    }

    public long getPlatformMoney() {
        return platformMoney;
    }

    public void setPlatformMoney(long platformMoney) {
        this.platformMoney = platformMoney;
    }

    public long getSupplierMoney() {
        return supplierMoney;
    }

    public void setSupplierMoney(long supplierMoney) {
        this.supplierMoney = supplierMoney;
    }

    public long getExpressMoney() {
        return expressMoney;
    }

    public void setExpressMoney(long expressMoney) {
        this.expressMoney = expressMoney;
    }

    public long getStockMoney() {
        return stockMoney;
    }

    public void setStockMoney(long stockMoney) {
        this.stockMoney = stockMoney;
    }

    public String getRefundDisputeImg() {
        return refundDisputeImg;
    }

    public void setRefundDisputeImg(String refundDisputeImg) {
        this.refundDisputeImg = refundDisputeImg;
    }

    public int getIsCargo() {
        return isCargo;
    }

    public void setIsCargo(int isCargo) {
        this.isCargo = isCargo;
    }

    public OrderStatus getCancelRefundStatus() {
        return cancelRefundStatus;
    }

    public void setCancelRefundStatus(OrderStatus cancelRefundStatus) {
        this.cancelRefundStatus = cancelRefundStatus;
    }

    public Date getUpdatePriceTime() {
        return updatePriceTime;
    }

    public void setUpdatePriceTime(Date updatePriceTime) {
        this.updatePriceTime = updatePriceTime;
    }

    public int getUpdatePriceUserId() {
        return updatePriceUserId;
    }

    public void setUpdatePriceUserId(int updatePriceUserId) {
        this.updatePriceUserId = updatePriceUserId;
    }

    public long getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(long oldPrice) {
        this.oldPrice = oldPrice;
    }

    /**
     * 非持久属性
     */
    @Transient
    private int stock;

    @Transient
    private boolean offline;

    @Transient
    private int shoppingCartId;

    private String brand;

    @Transient
    private String supplierCompany;

    private String headImg;

    @Transient
    private String alipayNo;

    @Transient
    private long alipayment;

    @Transient
    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(int shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean getOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    private String emergencyContact;

    private String emergencyPhone;

    private String sellerCode;
    private String sellerUsername;
    private String sellerName;
    private String sellerCompany;
    private String sellerAddress;
    private String buyerCode;
    private String buyerUsername;
    private String buyerName;

    private String address;

    private String fullAddress;

    private String pickAddress;

    private String alipayName;

    private String alipayCode;

    @Transient
    private long postFee;

    @Transient
    private SendOrder sendOrder;

    private String pickerName;

    @Transient
    private String alipayOrderCode;

    @Transient
    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    @Transient
    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    @Transient
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    @Transient
    public String getSellerCompany() {
        return sellerCompany;
    }

    public void setSellerCompany(String sellerCompany) {
        this.sellerCompany = sellerCompany;
    }

    @Transient
    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    @Transient
    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    @Transient
    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    @Transient
    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    @Transient
    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    @Transient
    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    @Transient
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Transient
    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    @Transient
    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    @Transient
    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    @Transient
    public String getAlipayCode() {
        return alipayCode;
    }

    public void setAlipayCode(String alipayCode) {
        this.alipayCode = alipayCode;
    }

    public long getPostFee() {
        return postFee;
    }

    public void setPostFee(long postFee) {
        this.postFee = postFee;
    }

    @Transient
    private long couponFee;

    public long getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(long couponFee) {
        this.couponFee = couponFee;
    }

    @Transient
    public String getSpec(){
        if(StrKit.isBlank(specPropValue)){
            return "";
        }
        int start = specPropValue.indexOf("规格:") + 3;
        int end = specPropValue.indexOf(";");
        return specPropValue.substring(start, end);
    }

    /**
     * 商品规格（去除规格:颜色:尺寸:）
     */
    @Transient
    public String getSpecPropValue2() {
        if (StrKit.isBlank(specPropValue)) {
            return "";
        }
        return specPropValue.replace("规格:", "").replace("颜色:", "").replace("尺寸:", "").replace(";", " ");
    }

    @Transient
    public String getTradeCode(){
        return Trade.mark + String.format("%05d", tradeId);
    }

    public SendOrder getSendOrder() {
        return sendOrder;
    }

    public void setSendOrder(SendOrder sendOrder) {
        this.sendOrder = sendOrder;
    }

    @Transient
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    @Transient
    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getAlipayNo() {
        return alipayNo;
    }

    public void setAlipayNo(String alipayNo) {
        this.alipayNo = alipayNo;
    }

    public long getAlipayment() {
        return alipayment;
    }

    public void setAlipayment(long alipayment) {
        this.alipayment = alipayment;
    }

    public String getAlipayOrderCode() {
        return alipayOrderCode;
    }

    public void setAlipayOrderCode(String alipayOrderCode) {
        this.alipayOrderCode = alipayOrderCode;
    }

    public String getPickerName() {
        return pickerName;
    }

    public void setPickerName(String pickerName) {
        this.pickerName = pickerName;
    }
}
