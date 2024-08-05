package com.aichebaba.rooftop.model;

import com.google.common.base.Function;
import com.jfinal.plugin.activerecord.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

public class MemberLevel implements Serializable {

    public static final Function<MemberLevel, Integer> IdValue = new Function<MemberLevel, Integer>() {
        @Override
        public Integer apply(MemberLevel memberLevel) {
            return memberLevel.getId();
        }
    };
    public static final Function<MemberLevel, Integer> levelValue = new Function<MemberLevel, Integer>() {
        @Override
        public Integer apply(MemberLevel memberLevel) {
            return memberLevel.getLevel();
        }
    };

    private int id;
    private int level;
    private String name;
    private Long totalMoney;
    private Long totalQuantity;
    private Boolean andOr;
    private String info;
    private Date created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Long totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Boolean getAndOr() {
        return andOr;
    }

    public void setAndOr(Boolean andOr) {
        this.andOr = andOr;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 非持久属性
     */
    private Long memberCount;

    @Transient
    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }
}
