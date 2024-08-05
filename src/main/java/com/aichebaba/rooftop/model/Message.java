package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.MessageObjectType;
import com.aichebaba.rooftop.model.enums.MessageType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private int id;
    @EnumVal(EnumValType.Name)
    private MessageType type;
    private int receiver;
    private String receiverPhone;
    private String content;
    private String relatedObject;
    @EnumVal(EnumValType.Name)
    private MessageObjectType objectType;
    private boolean ready;
    private boolean send;
    private int atTime;
    private Date created;

    public Message() {}

    public Message(MessageType type, int receiver, String receiverPhone, String content, String relatedObject,
                   MessageObjectType objectType, int atTime, Date created) {
        this.type = type;
        this.receiver = receiver;
        this.receiverPhone = receiverPhone;
        this.content = content;
        this.relatedObject = relatedObject;
        this.objectType = objectType;
        this.atTime = atTime;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRelatedObject() {
        return relatedObject;
    }

    public void setRelatedObject(String relatedObject) {
        this.relatedObject = relatedObject;
    }

    public MessageObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(MessageObjectType objectType) {
        this.objectType = objectType;
    }

    public boolean getReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean getSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public int getAtTime() {
        return atTime;
    }

    public void setAtTime(int atTime) {
        this.atTime = atTime;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
