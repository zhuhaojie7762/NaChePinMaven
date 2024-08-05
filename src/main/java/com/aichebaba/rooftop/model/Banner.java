package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.BannerType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

/**
 * Created by Administrator on 2017-1-10.
 */
public class Banner {
    private int id;
    private String image;
    private String url;
    @EnumVal(EnumValType.Name)
    private BannerType type;
    private String comment;
    private int sort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BannerType getType() {
        return type;
    }

    public void setType(BannerType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
