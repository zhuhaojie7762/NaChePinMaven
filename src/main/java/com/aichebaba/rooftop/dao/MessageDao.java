package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Message;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageDao extends Dao<Message, Integer> {

    public MessageDao() {
        super("message", Message.class);
    }

    public List<Message> findUnSendMessage(int atTime) {
        return findByWhere(" send= ? and atTime < ? limit 500", false, atTime);
    }
}
