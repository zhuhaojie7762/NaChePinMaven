package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsSubmitType;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.google.common.base.Function;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

public class Goods implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int TOP_STATE_TOP				= 3; // 置顶
	public static final int TOP_STATE_WAIT_CANCEL_TOP	= 2; // 待取消置顶 默认
	public static final int TOP_STATE_WAIT_TOP			= 1; // 待置顶
	public static final int TOP_STATE_NO_TOP			= 0; // 未置顶 默认

	public static final Function<Goods, String> CODE_VALUE = new Function<Goods, String>() {
        @Override
        public String apply(Goods g) {
            return g.getCode();
        }
    };

    private int id;
    /**
     * 商品编号
     */
    private String code;
    private String name;
    private String brand;
    /**
     * 货号
     */
    private String itemNo;
    @EnumVal(EnumValType.Name)
    private GoodsType type;
    private Date created;
    private Date onlineTime;
    private long wholesalePrice;
    private long preferentialPrice;
    private long retailPrice;
    private int weight;
    private String color;
    private String size;
    private String fitCar;
    private int stock;
    private Date arrivalTime;
    private String comment;
    @EnumVal(EnumValType.Manual)
    private GoodsStatus status;
    private int sellerId;
    private String dataPackageUrl1;
    private String dataPackageUrl2;
    private String dataPackageUrl3;
    private String imgPackageUrl1;
    private String imgPackageUrl2;
    private String imgPackageUrl3;
    private String headImgUrl1;
    private String headImgUrl2;
    private String headImgUrl3;
    private String headImgUrl4;
    private String headImgUrl5;
    private String bodyImgUrl1;
    private String bodyImgUrl2;
    private String bodyImgUrl3;
    private String bodyImgUrl4;
    private String bodyImgUrl5;
    private String bodyImgUrl6;
    private String bodyImgUrl7;
    private String bodyImgUrl8;
    private String bodyImgUrl9;
    private String bodyImgUrl10;
    private String bodyImgUrl11;
    private String bodyImgUrl12;
    private Boolean isSpecial;
    private int pv;
    private int followCnt;
    private String checkRemark;
    private int checkerId;
    private Date checkTime;
    private String producer;
    private String origin;
    private Date offlineTime;
    private String offlineRemark;
    private Date deleted;
    private int weighting;
//    private int saleNum;
    private String certificateNo;
    private int certificateFlag;
    private String applicant;
    private String makerName;
    private String productName;
    private String type3cNo;
    private String series;
    private String fitWeight;
    private String fitAge;
    private String interfaces;
    private String fixedMode;

	// 新增 三级分类id
	private Integer thirdclassid;
	private String thirdclassname;
	// 新增 二级分类id
	private Integer secondclassid;
	private String secondclassname;
	// 新增 一级分类id
	private Integer firstclassid;
	private String firstclassname;
	// 新增 品牌id
	private Integer brandid;

	// 供货商
	private String customerName;

	// 供货商
	private String supplierCompany;

	// 总销量
	private Long sellcnt;

	// 提交人 0 供货商 1 后台
	@EnumVal(EnumValType.Manual)
	private GoodsSubmitType submittype;
	// 置顶标识 0 未置顶 1置顶
	private Integer top;
	// 置顶时间
	private Date toptime;

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GoodsType getType() {
        return type;
    }

    public void setType(GoodsType type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(long wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public long getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(long preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public long getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(long retailPrice) {
        this.retailPrice = retailPrice;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public GoodsStatus getStatus() {
        return status;
    }

    public void setStatus(GoodsStatus status) {
        this.status = status;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getDataPackageUrl1() {
        return dataPackageUrl1;
    }

    public void setDataPackageUrl1(String dataPackageUrl1) {
        this.dataPackageUrl1 = dataPackageUrl1;
    }

    public String getDataPackageUrl2() {
        return dataPackageUrl2;
    }

    public void setDataPackageUrl2(String dataPackageUrl2) {
        this.dataPackageUrl2 = dataPackageUrl2;
    }

    public String getDataPackageUrl3() {
        return dataPackageUrl3;
    }

    public void setDataPackageUrl3(String dataPackageUrl3) {
        this.dataPackageUrl3 = dataPackageUrl3;
    }

    public String getImgPackageUrl1() {
        return imgPackageUrl1;
    }

    public void setImgPackageUrl1(String imgPackageUrl1) {
        this.imgPackageUrl1 = imgPackageUrl1;
    }

    public String getImgPackageUrl2() {
        return imgPackageUrl2;
    }

    public void setImgPackageUrl2(String imgPackageUrl2) {
        this.imgPackageUrl2 = imgPackageUrl2;
    }

    public String getImgPackageUrl3() {
        return imgPackageUrl3;
    }

    public void setImgPackageUrl3(String imgPackageUrl3) {
        this.imgPackageUrl3 = imgPackageUrl3;
    }

    public String getHeadImgUrl1() {
        return headImgUrl1;
    }

    public void setHeadImgUrl1(String headImgUrl1) {
        this.headImgUrl1 = headImgUrl1;
    }

    public String getHeadImgUrl2() {
        return headImgUrl2;
    }

    public void setHeadImgUrl2(String headImgUrl2) {
        this.headImgUrl2 = headImgUrl2;
    }

    public String getHeadImgUrl3() {
        return headImgUrl3;
    }

    public void setHeadImgUrl3(String headImgUrl3) {
        this.headImgUrl3 = headImgUrl3;
    }

    public String getHeadImgUrl4() {
        return headImgUrl4;
    }

    public void setHeadImgUrl4(String headImgUrl4) {
        this.headImgUrl4 = headImgUrl4;
    }

    public String getHeadImgUrl5() {
        return headImgUrl5;
    }

    public void setHeadImgUrl5(String headImgUrl5) {
        this.headImgUrl5 = headImgUrl5;
    }

    public String getBodyImgUrl1() {
        return bodyImgUrl1;
    }

    public void setBodyImgUrl1(String bodyImgUrl1) {
        this.bodyImgUrl1 = bodyImgUrl1;
    }

    public String getBodyImgUrl2() {
        return bodyImgUrl2;
    }

    public void setBodyImgUrl2(String bodyImgUrl2) {
        this.bodyImgUrl2 = bodyImgUrl2;
    }

    public String getBodyImgUrl3() {
        return bodyImgUrl3;
    }

    public void setBodyImgUrl3(String bodyImgUrl3) {
        this.bodyImgUrl3 = bodyImgUrl3;
    }

    public String getBodyImgUrl4() {
        return bodyImgUrl4;
    }

    public void setBodyImgUrl4(String bodyImgUrl4) {
        this.bodyImgUrl4 = bodyImgUrl4;
    }

    public String getBodyImgUrl5() {
        return bodyImgUrl5;
    }

    public void setBodyImgUrl5(String bodyImgUrl5) {
        this.bodyImgUrl5 = bodyImgUrl5;
    }

    public String getBodyImgUrl6() {
        return bodyImgUrl6;
    }

    public void setBodyImgUrl6(String bodyImgUrl6) {
        this.bodyImgUrl6 = bodyImgUrl6;
    }

    public String getBodyImgUrl7() {
        return bodyImgUrl7;
    }

    public void setBodyImgUrl7(String bodyImgUrl7) {
        this.bodyImgUrl7 = bodyImgUrl7;
    }

    public String getBodyImgUrl8() {
        return bodyImgUrl8;
    }

    public void setBodyImgUrl8(String bodyImgUrl8) {
        this.bodyImgUrl8 = bodyImgUrl8;
    }

    public String getBodyImgUrl9() {
        return bodyImgUrl9;
    }

    public void setBodyImgUrl9(String bodyImgUrl9) {
        this.bodyImgUrl9 = bodyImgUrl9;
    }

    public String getBodyImgUrl10() {
        return bodyImgUrl10;
    }

    public void setBodyImgUrl10(String bodyImgUrl10) {
        this.bodyImgUrl10 = bodyImgUrl10;
    }

    public String getBodyImgUrl11() {
        return bodyImgUrl11;
    }

    public void setBodyImgUrl11(String bodyImgUrl11) {
        this.bodyImgUrl11 = bodyImgUrl11;
    }

    public String getBodyImgUrl12() {
        return bodyImgUrl12;
    }

    public void setBodyImgUrl12(String bodyImgUrl12) {
        this.bodyImgUrl12 = bodyImgUrl12;
    }

    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(int followCnt) {
        this.followCnt = followCnt;
    }

    public String getCheckRemark() {
        return checkRemark;
    }

    public void setCheckRemark(String checkRemark) {
        this.checkRemark = checkRemark;
    }

    public int getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(int checkerId) {
        this.checkerId = checkerId;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public String getOfflineRemark() {
        return offlineRemark;
    }

    public void setOfflineRemark(String offlineRemark) {
        this.offlineRemark = offlineRemark;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public int getCertificateFlag() {
        return certificateFlag;
    }

    public void setCertificateFlag(int certificateFlag) {
        this.certificateFlag = certificateFlag;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType3cNo() {
        return type3cNo;
    }

    public void setType3cNo(String type3cNo) {
        this.type3cNo = type3cNo;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getFitWeight() {
        return fitWeight;
    }

    public void setFitWeight(String fitWeight) {
        this.fitWeight = fitWeight;
    }

    public String getFitAge() {
        return fitAge;
    }

    public void setFitAge(String fitAge) {
        this.fitAge = fitAge;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String interfaces) {
        this.interfaces = interfaces;
    }

    public String getFixedMode() {
        return fixedMode;
    }

    public void setFixedMode(String fixedMode) {
        this.fixedMode = fixedMode;
    }

    //    public int getSaleNum() {
//        return saleNum;
//    }
//
//    public void setSaleNum(int saleNum) {
//        this.saleNum = saleNum;
//    }

    //    @Transient
//    private String goodsId;
//
//    public String getGoodsId() {
//        return goodsId;
//    }
//
//    public void setGoodsId(String goodsId) {
//        this.goodsId = goodsId;
//    }

    /**
     * 非持久属性
     */
    private Boolean followed;   //是否被关注
    private int followId;
    @Transient
    private SalesVolume salesVolume;

    public SalesVolume getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(SalesVolume salesVolume) {
        this.salesVolume = salesVolume;
    }

    @Transient
    public int getFollowId() {
        return followId;
    }

    public void setFollowId(int followId) {
        this.followId = followId;
    }

    @Transient
    public String[] getColors() {
        if (StrKit.notBlank(color)) {
            return color.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }

    @Transient
    public String[] getSizes() {
        if (StrKit.notBlank(size)) {
            return size.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }

    @Transient
    public String[] getFitCars() {
        if (StrKit.notBlank(size)) {
            return fitCar.split(Constant.SPLIT_MARK);
        } else {
            return null;
        }
    }

    @Transient
    public List<String> getHeadImgUrls() {
        List<String> imgs = new ArrayList<>();
        if (StrKit.notBlank(headImgUrl1)) {
            imgs.add(headImgUrl1);
        }
        if (StrKit.notBlank(headImgUrl2)) {
            imgs.add(headImgUrl2);
        }
        if (StrKit.notBlank(headImgUrl3)) {
            imgs.add(headImgUrl3);
        }
        if (StrKit.notBlank(headImgUrl4)) {
            imgs.add(headImgUrl4);
        }
        if (StrKit.notBlank(headImgUrl5)) {
            imgs.add(headImgUrl5);
        }
        return imgs;
    }

    @Transient
    public List<String> getBodyImgUrls() {
        List<String> imgs = new ArrayList<>();
        if (StrKit.notBlank(bodyImgUrl1)) {
            imgs.add(bodyImgUrl1);
        }
        if (StrKit.notBlank(bodyImgUrl2)) {
            imgs.add(bodyImgUrl2);
        }
        if (StrKit.notBlank(bodyImgUrl3)) {
            imgs.add(bodyImgUrl3);
        }
        if (StrKit.notBlank(bodyImgUrl4)) {
            imgs.add(bodyImgUrl4);
        }
        if (StrKit.notBlank(bodyImgUrl5)) {
            imgs.add(bodyImgUrl5);
        }
        if (StrKit.notBlank(bodyImgUrl6)) {
            imgs.add(bodyImgUrl6);
        }
        if (StrKit.notBlank(bodyImgUrl7)) {
            imgs.add(bodyImgUrl7);
        }
        if (StrKit.notBlank(bodyImgUrl8)) {
            imgs.add(bodyImgUrl8);
        }
        if (StrKit.notBlank(bodyImgUrl9)) {
            imgs.add(bodyImgUrl9);
        }
        if (StrKit.notBlank(bodyImgUrl10)) {
            imgs.add(bodyImgUrl10);
        }
        if (StrKit.notBlank(bodyImgUrl11)) {
            imgs.add(bodyImgUrl11);
        }
        if (StrKit.notBlank(bodyImgUrl12)) {
            imgs.add(bodyImgUrl12);
        }
        return imgs;
    }

    @Transient
    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

	public Long getSellcnt() {
		return sellcnt;
	}

	public void setSellcnt(Long sellcnt) {
		this.sellcnt = sellcnt;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSupplierCompany() {
		return supplierCompany;
	}

	public void setSupplierCompany(String supplierCompany) {
		this.supplierCompany = supplierCompany;
	}

	public String getThirdclassname() {
		return thirdclassname;
	}

	public void setThirdclassname(String thirdclassname) {
		this.thirdclassname = thirdclassname;
	}

	public String getSecondclassname() {
		return secondclassname;
	}

	public void setSecondclassname(String secondclassname) {
		this.secondclassname = secondclassname;
	}

	public String getFirstclassname() {
		return firstclassname;
	}

	public void setFirstclassname(String firstclassname) {
		this.firstclassname = firstclassname;
	}

	public Integer getThirdclassid() {
		return thirdclassid;
	}

	public void setThirdclassid(Integer thirdclassid) {
		this.thirdclassid = thirdclassid;
	}

	public Integer getSecondclassid() {
		return secondclassid;
	}

	public void setSecondclassid(Integer secondclassid) {
		this.secondclassid = secondclassid;
	}

	public Integer getFirstclassid() {
		return firstclassid;
	}

	public void setFirstclassid(Integer firstclassid) {
		this.firstclassid = firstclassid;
	}

	public Integer getBrandid() {
		return brandid;
	}

	public void setBrandid(Integer brandid) {
		this.brandid = brandid;
	}

	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Date getToptime() {
		return toptime;
	}

	public void setToptime(Date toptime) {
		this.toptime = toptime;
	}

	public GoodsSubmitType getSubmittype() {
		return submittype;
	}

	public void setSubmittype(GoodsSubmitType submittype) {
		this.submittype = submittype;
	}
}
