package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.AllocatingStatus;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.google.common.base.Function;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public class Trade implements Serializable {

    public static final String mark = "TR";

    public static final Function<Trade, Integer> ID_VALUE = t -> {
        assert t != null;
        return t.getId();
    };

    public static Integer code2id(String code){
        String temp = code.toUpperCase().replace(mark, "");
        int tradeId;
        try{
            tradeId = Integer.parseInt(temp);
        }catch (Exception ex){
            tradeId = -1;
        }
        return tradeId;
    }
    // 16-12-11 He 增
    public static Integer parserTradeCode(String code) {
        return StrKit.isBlank(code)? 0 : Integer.parseInt(code.trim().toUpperCase().replace(mark, ""));
    }

    private int id;

    /**
     * 买家ID
     */
    private int buyerId;

    /**
     * 买家支付宝帐号
     */
    private String paidAlipayCode;

    /**
     * 买家支付宝帐号
     */
    private String alipayCode;

    /**
     * 收货人姓名
     */
    private String receiveName;

    /**
     * 收货地址所在省份
     */
    private int provinceId;

    /**
     * 市
     */
    private int cityId;

    /**
     * 区县
     */
    private int countyId;

    /**
     * 收货地址
     */
    private String receiveAddress;

    /**
     * 收货电话
     */
    private String receivePhone;

    /**
     * 座机号码
     */
    private String receiveTel;

    /**
     * 邮编
     */
    private String zip;

    /**
     * 订单数
     */
    private int orderCnt;

    /**
     * 交易货物总重量(g)
     */
    private int weight;

    /**
     * 交易总金额(分）
     */
    private long totalPayment;

    /**
     * 修改前交易总金额(分）
     */
    private long oldTotalPayment;

    /**
     * 邮费(分）
     */
    private long postFee;

    /**
     * 修改前邮费(分)
     */
    private long oldPostFee;

    /**
     * 邮费补贴(分)
     */
    private long postSubsidy;

    /**
     * 需付款金额(分）
     */
    private long payment;

    /**
     * 修改前的支付金额(分)
     */
    private long oldPayment;

    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 支付宝交易号
     */
    private String alipayNo;

    /**
     * 订单状态
     */
    @EnumVal(EnumValType.Name)
    private OrderStatus status;

    /**
     * 发货时间
     */
    private Date consignTime;

    /**
     * 快递公司ID
     */
    private int expressId;

    /**
     * 快递公司名
     */
    private String expressName;

    /**
     * 快递单号
     */
    private String expressCode;

    private int refundNum;

    /**
     * 使用的优惠券ID
     */
    private int couponId;

    @EnumVal(EnumValType.Name)
    private UseType useType;

    /**
     * 使用的优惠券金额 单位分
     */
    private long couponFee;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 拣货单编号
     */
    private String pickOrderCode;

    /**
     * 备注
     * @return
     */
    private String remark;

    /**
     * 实际重量
     */
    private int realityWeight;

    /**
     * 配货未完成状态
     */
    @EnumVal(EnumValType.Manual)
    private AllocatingStatus allocatingStatus;

    /**
     * 发货单对象
     */
    private SendOrder sendOrder;

    /**
     * 进货商备注
     */
    private String purchaseComment;

    /**
     * 供货商备注
     */
    private String supplierComment;

    /**
     * 管理员备注
     */
    private String managerComment;

    /**
     * 修改价格理由
     */
    private String updatePriceReason;

    /**
     * 修改价格时间
     */
    private Date updatePriceTime;

    /**
     * 修改价格工作员ID
     */
    private int updatePriceUserId;

    /**
     * 打包费
     */
    private int packFee;

    /**
     * 打包费优惠额
     */
    private int packSubsidy;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getAlipayCode() {
        return alipayCode;
    }

    public void setAlipayCode(String alipayCode) {
        this.alipayCode = alipayCode;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
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

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public long getPostFee() {
        return postFee;
    }

    public void setPostFee(long postFee) {
        this.postFee = postFee;
    }

    public long getPostSubsidy() {
        return postSubsidy;
    }

    public void setPostSubsidy(long postSubsidy) {
        this.postSubsidy = postSubsidy;
    }

    public long getPayment() {
        return payment;
    }

    public void setPayment(long payment) {
        this.payment = payment;
    }

    public long getOldPayment() {
        return oldPayment;
    }

    public void setOldPayment(long oldPayment) {
        this.oldPayment = oldPayment;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getAlipayNo() {
        return alipayNo;
    }

    public void setAlipayNo(String alipayNo) {
        this.alipayNo = alipayNo;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
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

    public int getRefundNum() {
        return refundNum;
    }

    public void setRefundNum(int refundNum) {
        this.refundNum = refundNum;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public UseType getUseType() {
        return useType;
    }

    public void setUseType(UseType useType) {
        this.useType = useType;
    }

    public long getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(long couponFee) {
        this.couponFee = couponFee;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getPickOrderCode() {
        return pickOrderCode;
    }

    public void setPickOrderCode(String pickOrderCode) {
        this.pickOrderCode = pickOrderCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRealityWeight() {
        return realityWeight;
    }

    public void setRealityWeight(int realityWeight) {
        this.realityWeight = realityWeight;
    }

    public AllocatingStatus getAllocatingStatus() {
        return allocatingStatus;
    }

    public void setAllocatingStatus(AllocatingStatus allocatingStatus) {
        this.allocatingStatus = allocatingStatus;
    }

    public String getPurchaseComment() {
        return purchaseComment;
    }

    public void setPurchaseComment(String purchaseComment) {
        this.purchaseComment = purchaseComment;
    }

    public String getSupplierComment() {
        return supplierComment;
    }

    public void setSupplierComment(String supplierComment) {
        this.supplierComment = supplierComment;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public long getOldTotalPayment() {
        return oldTotalPayment;
    }

    public void setOldTotalPayment(long oldTotalPayment) {
        this.oldTotalPayment = oldTotalPayment;
    }

    public long getOldPostFee() {
        return oldPostFee;
    }

    public void setOldPostFee(long oldPostFee) {
        this.oldPostFee = oldPostFee;
    }

    public String getUpdatePriceReason() {
        return updatePriceReason;
    }

    public void setUpdatePriceReason(String updatePriceReason) {
        this.updatePriceReason = updatePriceReason;
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

    public int getPackFee() {
        return packFee;
    }

    public void setPackFee(int packFee) {
        this.packFee = packFee;
    }

    public int getPackSubsidy() {
        return packSubsidy;
    }

    public void setPackSubsidy(int packSubsidy) {
        this.packSubsidy = packSubsidy;
    }

    @Transient
    private Collection<Order> orders;

    @Transient
    private Coupon coupon;

    public Collection<Order> getOrders() {
        return orders;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public String getPaidAlipayCode() {
        return paidAlipayCode;
    }

    public void setPaidAlipayCode(String paidAlipayCode) {
        this.paidAlipayCode = paidAlipayCode;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    public SendOrder getSendOrder() {
        return sendOrder;
    }

    public void setSendOrder(SendOrder sendOrder) {
        this.sendOrder = sendOrder;
    }

    @Transient
    public String getCode(){
        return mark + String.format("%05d", id);
    }

    @Transient
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * 商品数量
     */
    private int quantity;

    @Transient
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Transient
    private Province province;

    @Transient
    private City city;

    @Transient
    private County county;

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }
}
