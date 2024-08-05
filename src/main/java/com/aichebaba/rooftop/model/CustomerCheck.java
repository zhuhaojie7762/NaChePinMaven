package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.model.enums.CustomerAuditState;
import com.aichebaba.rooftop.model.enums.CustomerAuditType;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class CustomerCheck implements Serializable {
    private int id;
    private int customerId;
    private Date created;

    @EnumVal(EnumValType.Name)
    private CustomerAuditType type;
    private int checkerId;

    @EnumVal(EnumValType.Ordinal)
    private CustomerAuditState status;
    private String comment;
    private String shopUrl;
    private String wangwang;
    private String qq;
    private String address;
    private String fullAddress;
    private String postCode;
    private String alipayCode;
    private String tenpayCode;
    private String inGoodsType;
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
    private String alipayName;
    private String weiXin;
    private String sign;
    private int provinceId;
    private String province;

    /**
     * 市
     */
    private int cityId;
    private String city;

    /**
     * 区县
     */
    private int countyId;
    private String area;
    private Date purchaseTime;
    private Date supplierTime;
    private Date passPurchaseTime;
    private Date passSupplierTime;
    private String remarks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public CustomerAuditType getType() {
        return type;
    }

    public void setType(CustomerAuditType type) {
        this.type = type;
    }

    public int getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(int checkerId) {
        this.checkerId = checkerId;
    }

    public CustomerAuditState getStatus() {
        return status;
    }

    public void setStatus(CustomerAuditState status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getInGoodsType() {
        return inGoodsType;
    }

    public void setInGoodsType(String inGoodsType) {
        this.inGoodsType = inGoodsType;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Transient
    public String[] getOutGoodsTypes() {
        if (StrKit.notBlank(outGoodsType)) {
            return outGoodsType.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }

    @Transient
    public String[] getInGoodsTypes() {
        if (StrKit.notBlank(inGoodsType)) {
            return inGoodsType.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }

    @Transient
    public Boolean getCushionState(){
        if (StrKit.isBlank(outGoodsType)) {
            return false;
        }
        for(String type : getOutGoodsTypes()){
            if(type.equals(GoodsType.cushion.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getFloorMatState(){
        if (StrKit.isBlank(outGoodsType)) {
            return false;
        }
        for(String type : getOutGoodsTypes()){
            if(type.equals(GoodsType.floorMat.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getCarTrimState(){
        if (StrKit.isBlank(outGoodsType)) {
            return false;
        }
        for(String type : getOutGoodsTypes()){
            if(type.equals(GoodsType.carTrim.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getWheelCoverState(){
        if (StrKit.isBlank(outGoodsType)) {
            return false;
        }
        for(String type : getOutGoodsTypes()){
            if(type.equals(GoodsType.wheelCover.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getOtherState(){
        if (StrKit.isBlank(outGoodsType)) {
            return false;
        }
        for(String type : getOutGoodsTypes()){
            if(type.equals(GoodsType.other.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getOutGoodsTypes2() {
        if (StrKit.isBlank(outGoodsType))
            return "";
        String[] types = outGoodsType.split(Constant.SPLIT_MARK);
        StringBuilder sb = new StringBuilder();
        for (String type : types) {
            sb.append(Customer.GOODS_TYPES.get(type)).append(Constant.SPLIT_MARK);
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

    @Transient
    public Boolean getInCushionState(){
        if (StrKit.isBlank(inGoodsType)) {
            return false;
        }
        for(String type : getInGoodsTypes()){
            if(type.equals(GoodsType.cushion.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getInFloorMatState(){
        if (StrKit.isBlank(inGoodsType)) {
            return false;
        }
        for(String type : getInGoodsTypes()){
            if(type.equals(GoodsType.floorMat.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getInCarTrimState(){
        if (StrKit.isBlank(inGoodsType)) {
            return false;
        }
        for(String type : getInGoodsTypes()){
            if(type.equals(GoodsType.carTrim.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getInWheelCoverState(){
        if (StrKit.isBlank(inGoodsType)) {
            return false;
        }
        for(String type : getInGoodsTypes()){
            if(type.equals(GoodsType.wheelCover.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public Boolean getInOtherState(){
        if (StrKit.isBlank(inGoodsType)) {
            return false;
        }
        for(String type : getInGoodsTypes()){
            if(type.equals(GoodsType.other.name())){
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getCityArea(){
        StringBuilder address = new StringBuilder();
        if(StrKit.notBlank(province)) {
            address.append(province);
        }else{
            address.append("");
        }
        if(StrKit.notBlank(city)){
            address.append("-").append(city);
        }
        if(StrKit.notBlank(area)){
            address.append("-").append(area);
        }
        return address.toString();
    }

    @Transient
    public String getCityArea2(){
        StringBuilder address = new StringBuilder();
        if(StrKit.notBlank(province)) {
            address.append(province);
        }else{
            address.append("");
        }
        if(StrKit.notBlank(city)){
            address.append(city);
        }
        if(StrKit.notBlank(area)){
            address.append(area);
        }
        return address.toString();
    }

    @Transient
    public String[] getBrandItem(){
        if (StrKit.notBlank(brand)) {
            return brand.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }
}
