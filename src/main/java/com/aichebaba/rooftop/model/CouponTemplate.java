package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.CouponSendType;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
public class CouponTemplate implements Serializable {

    private int id;

    private String name;

    private int money;

    private Date startTime;

    private Date endTime;

    private int num;

    private int sendNum;

    private int condition;

    @EnumVal(EnumValType.Name)
    private UseType useType;

    @EnumVal(EnumValType.Name)
    private CouponTemplateStatus status;

    @EnumVal(EnumValType.Name)
    private CouponSendType sendType;

    private boolean display;

    private boolean donation;

    private Date created;

    private int createUserId;

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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public UseType getUseType() {
        return useType;
    }

    public void setUseType(UseType useType) {
        this.useType = useType;
    }

    public CouponTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(CouponTemplateStatus status) {
        this.status = status;
    }

    public CouponSendType getSendType() {
        return sendType;
    }

    public void setSendType(CouponSendType sendType) {
        this.sendType = sendType;
    }

    public boolean getDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean getDonation() {
        return donation;
    }

    public void setDonation(boolean donation) {
        this.donation = donation;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    /**
     *  非持久属性
     */
    private String activityName;    //活动名

    @Transient
    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
