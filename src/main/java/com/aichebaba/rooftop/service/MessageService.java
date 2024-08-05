package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.MessageDao;
import com.aichebaba.rooftop.model.Message;
import com.aichebaba.rooftop.model.enums.MessageObjectType;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.alibaba.fastjson.JSON;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.aichebaba.rooftop.model.enums.MessageType.CUSTOMER;
import static com.aichebaba.rooftop.model.enums.MessageType.USER;

@Service
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public void addUserMessage(int id, String receivePhone, String content, Object object,
                               MessageObjectType type, int atTime) {
        messageDao.add(new Message(USER, id, receivePhone, content, JSON.toJSONString(object),
                type, atTime, new Date()));
    }

    public void addCustomerMessage(int id, String receivePhone, String content, Object object,
                                   MessageObjectType type, int atTime) {
        messageDao.add(new Message(CUSTOMER, id, receivePhone, content, JSON.toJSONString(object),
                type, atTime, new Date()));
    }

    public void sendMessages() {
        List<Message> messages = messageDao.findUnSendMessage(LocalTime.now().getHourOfDay());
        for (Message msg : messages) {
            SMSUtil.sendSmsMsg2(msg.getReceiverPhone(), msg.getContent());
            msg.setSend(true);
            messageDao.update(msg);
        }
    }
}
