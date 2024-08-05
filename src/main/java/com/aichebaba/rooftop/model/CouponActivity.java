package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.CouponActivityStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.vo.CouponActivityCondition;
import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
public class CouponActivity implements Serializable {
    private int id;

    private String name;

    private int couponTemplateId;

    private Date startTime;

    private Date endTime;

    private String condition;

    private boolean andOr;

    private int targetType;

    private String memberLevelId;

    private String memberRange;

    @EnumVal(EnumValType.Name)
    private CouponActivityStatus status;

    private String icon;

    private Date created;

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

    public int getCouponTemplateId() {
        return couponTemplateId;
    }

    public void setCouponTemplateId(int couponTemplateId) {
        this.couponTemplateId = couponTemplateId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCondition() {
        return condition;
    }

    @Transient
    public CouponActivityCondition getConditionJson() {
        if (StrKit.notBlank(condition)) {
            return JSON.parseObject(condition, CouponActivityCondition.class);
        } else {
            return null;
        }
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean getAndOr() {
        return andOr;
    }

    public void setAndOr(boolean andOr) {
        this.andOr = andOr;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public String getMemberLevelId() {
        return memberLevelId;
    }

    public void setMemberLevelId(String memberLevelId) {
        this.memberLevelId = memberLevelId;
    }

    public String getMemberRange() {
        return memberRange;
    }

    public void setMemberRange(String memberRange) {
        this.memberRange = memberRange;
    }

    public CouponActivityStatus getStatus() {
        return status;
    }

    public void setStatus(CouponActivityStatus status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Transient
    private List<String> memberLevelNames;
    private String couponName;
    @EnumVal(EnumValType.Name)
    private UseType useType;
    private int money;
    @Transient
    private int memberNum;

    public List<String> getMemberLevelNames() {
        return memberLevelNames;
    }

    public void setMemberLevelNames(List<String> memberLevelNames) {
        this.memberLevelNames = memberLevelNames;
    }

    @Transient
    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    @Transient
    public UseType getUseType() {
        return useType;
    }

    public void setUseType(UseType useType) {
        this.useType = useType;
    }

    @Transient
    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Transient
    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }
}
