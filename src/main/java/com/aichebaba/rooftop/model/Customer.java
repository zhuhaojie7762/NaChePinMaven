package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.model.enums.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Customer implements Serializable {

    public static Map<String, String> GOODS_TYPES = ImmutableMap
            .of("cushion", "汽车坐垫", "floorMat", "汽车脚垫", "wheelCover", "方向盘套", "carTrim", "汽车内饰", "other", "其他");
    public static Function<Customer, Integer> idValue = new Function<Customer, Integer>() {
        @Override
        public Integer apply(Customer c) {
            return c.getId();
        }
    };

    private int id;
    private String code;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    @EnumVal(EnumValType.Manual)
    private CustomerType type;
    @EnumVal(EnumValType.Manual)
    private CustomerState state;
    private String shopUrl;
    private String wangwang;
    private String qq;
    private String inGoodsType;
    private String address;
    private String fullAddress;
    private String postCode;
    private String alipayCode;
    private String tenpayCode;
    private String supplierCompany;
    private String artificialPerson;
    private String businessLicence;
    private Boolean isProducer;
    private String brand;
    private String emergencyContact;
    private String emergencyPhone;
    private String outGoodsType;
    private String pickAddress;
    private String pickAreaCode;
    private Date created;
    private String alipayName;
    private String weiXin;
    private String sign;
    private int weighting;
    private String province;

    private int provinceId;

    /**
     * 市
     */
    private int cityId;

    /**
     * 区县
     */
    private int countyId;
    private String city;
    private String area;
    private Date purchaseTime;
    private Date supplierTime;
    private Date passPurchaseTime;
    private Date passSupplierTime;
    private String remarks;
    private int level;
    private long totalPurchaseMoney;
    private long totalPurchaseCount;
    private Date lastPurchaseTime;
    @EnumVal(EnumValType.Name)
    private SettlementMethodType settlementMethod;
    private Date lastSettlementTime;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public CustomerState getState() {
        return state;
    }

    public void setState(CustomerState state) {
        this.state = state;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public String getWangwang() {
        return wangwang;
    }

    public void setWangwang(String wangwang) {
        this.wangwang = wangwang;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAlipayName() {
        return alipayName;
    }

    public void setAlipayName(String alipayName) {
        this.alipayName = alipayName;
    }

    public String getWeiXin() {
        return weiXin;
    }

    public void setWeiXin(String weiXin) {
        this.weiXin = weiXin;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    @Transient
    public String getInGoodsTypes() {
        if (StrKit.isBlank(inGoodsType))
            return "";
        String[] types = inGoodsType.split(";");
        StringBuilder sb = new StringBuilder();
        for (String type : types) {
            sb.append(GOODS_TYPES.get(type)).append(";");
        }
        return sb.toString();
    }

    public String getInGoodsType() {
        return inGoodsType;
    }

    public void setInGoodsType(String inGoodsType) {
        this.inGoodsType = inGoodsType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getAlipayCode() {
        return alipayCode;
    }

    public void setAlipayCode(String alipayCode) {
        this.alipayCode = alipayCode;
    }

    public String getTenpayCode() {
        return tenpayCode;
    }

    public void setTenpayCode(String tenpayCode) {
        this.tenpayCode = tenpayCode;
    }

    public String getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public String getArtificialPerson() {
        return artificialPerson;
    }

    public void setArtificialPerson(String artificialPerson) {
        this.artificialPerson = artificialPerson;
    }

    public String getBusinessLicence() {
        return businessLicence;
    }

    public void setBusinessLicence(String businessLicence) {
        this.businessLicence = businessLicence;
    }

    public Boolean getIsProducer() {
        return isProducer;
    }

    public void setIsProducer(Boolean isProducer) {
        this.isProducer = isProducer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public Date getLastSettlementTime() {
        return lastSettlementTime;
    }

    public void setLastSettlementTime(Date lastSettlementTime) {
        this.lastSettlementTime = lastSettlementTime;
    }

    @Transient
    public String getOutGoodsTypes() {
        if (StrKit.isBlank(outGoodsType))
            return "";
        String[] types = outGoodsType.split(";");
        StringBuilder sb = new StringBuilder();
        for (String type : types) {
            sb.append(GOODS_TYPES.get(type)).append(";");
        }
        return sb.toString();
    }


    @Transient
    public String getInGoodsTypes2() {
        if (StrKit.isBlank(inGoodsType))
            return "";
        String[] types = inGoodsType.split(Constant.SPLIT_MARK);
        StringBuilder sb = new StringBuilder();
        for (String type : types) {
            sb.append(Customer.GOODS_TYPES.get(type)).append(Constant.SPLIT_MARK);
        }
        return sb.toString();
    }

    public String getOutGoodsType() {
        return outGoodsType;
    }

    public void setOutGoodsType(String outGoodsType) {
        this.outGoodsType = outGoodsType;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public String getPickAreaCode() {
        return pickAreaCode;
    }

    public void setPickAreaCode(String pickAreaCode) {
        this.pickAreaCode = pickAreaCode;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Date getSupplierTime() {
        return supplierTime;
    }

    public void setSupplierTime(Date supplierTime) {
        this.supplierTime = supplierTime;
    }

    public Date getPassPurchaseTime() {
        return passPurchaseTime;
    }

    public void setPassPurchaseTime(Date passPurchaseTime) {
        this.passPurchaseTime = passPurchaseTime;
    }

    public Date getPassSupplierTime() {
        return passSupplierTime;
    }

    public void setPassSupplierTime(Date passSupplierTime) {
        this.passSupplierTime = passSupplierTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTotalPurchaseMoney() {
        return totalPurchaseMoney;
    }

    public void setTotalPurchaseMoney(long totalPurchaseMoney) {
        this.totalPurchaseMoney = totalPurchaseMoney;
    }

    public long getTotalPurchaseCount() {
        return totalPurchaseCount;
    }

    public void setTotalPurchaseCount(long totalPurchaseCount) {
        this.totalPurchaseCount = totalPurchaseCount;
    }

    public Date getLastPurchaseTime() {
        return lastPurchaseTime;
    }

    public void setLastPurchaseTime(Date lastPurchaseTime) {
        this.lastPurchaseTime = lastPurchaseTime;
    }

    public SettlementMethodType getSettlementMethod() {
        return settlementMethod;
    }

    public void setSettlementMethod(SettlementMethodType settlementMethod) {
        this.settlementMethod = settlementMethod;
    }

    /**
     * 非持久属性
     */
    @EnumVal(EnumValType.Manual)
    private CustomerPurchaseState purchaseState;

    @EnumVal(EnumValType.Manual)
    private CustomerSupplierState supplierState;

    @Transient
    private long shoppingCartSize;

    @Transient
    private AccountPayee defaultAccountPayee;

    @Transient
    public CustomerPurchaseState getPurchaseState() {
        if (purchaseState == null) {
            return CustomerPurchaseState.NO_APPLICATION;
        } else {
            return purchaseState;
        }
    }

    public void setPurchaseState(CustomerPurchaseState purchaseState) {
        this.purchaseState = purchaseState;
    }

    @Transient
    public CustomerSupplierState getSupplierState() {
        if (supplierState == null) {
            return CustomerSupplierState.NO_APPLICATION;
        } else {
            return supplierState;
        }
    }

    public void setSupplierState(CustomerSupplierState supplierState) {
        this.supplierState = supplierState;
    }

    public long getShoppingCartSize() {
        return shoppingCartSize;
    }

    public void setShoppingCartSize(long shoppingCartSize) {
        this.shoppingCartSize = shoppingCartSize;
    }

    @Transient
    public String getCityArea() {
        StringBuilder address = new StringBuilder();
        if (StrKit.notBlank(province)) {
            address.append(province);
        } else {
            address.append("");
        }
        if (StrKit.notBlank(city)) {
            address.append("-").append(city);
        }
        if (StrKit.notBlank(area)) {
            address.append("-").append(area);
        }
        return address.toString();
    }

    @Transient
    public String getCityArea2() {
        StringBuilder address = new StringBuilder();
        if (StrKit.notBlank(province)) {
            address.append(province);
        } else {
            address.append("");
        }
        if (StrKit.notBlank(city)) {
            address.append(city);
        }
        if (StrKit.notBlank(area)) {
            address.append(area);
        }
        return address.toString();
    }

    String levelName;

    @Transient
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public AccountPayee getDefaultAccountPayee() {
        return defaultAccountPayee;
    }

    public void setDefaultAccountPayee(AccountPayee defaultAccountPayee) {
        this.defaultAccountPayee = defaultAccountPayee;
    }
}
