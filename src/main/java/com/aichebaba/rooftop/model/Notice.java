package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.NoticeType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

public class Notice implements Serializable {
    private int id;
    private String title;
    private String content;
    private Date created;
    private Date sort;
    private int state;
    private int delUserId;
    private Date delTime;
    @EnumVal(EnumValType.Name)
    private NoticeType type;
    private String attachment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getSort() {
        return sort;
    }

    public void setSort(Date sort) {
        this.sort = sort;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDelUserId() {
        return delUserId;
    }

    public void setDelUserId(int delUserId) {
        this.delUserId = delUserId;
    }

    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    public NoticeType getType() {
        return type;
    }

    public void setType(NoticeType type) {
        this.type = type;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
