package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.UserDao;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.enums.UserState;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public PageList<User> findUsers(UserState state, String name, PageParam pageParam) {
        return userDao.findUsers(state, name, pageParam);
    }

    public User getById(int id) {
        return userDao.getByPK(id);
    }

    public User getByCode(String code) {
        return userDao.getByWhere("code=?", code);
    }

    public User getByUsername(String username) {
        return userDao.getByWhere("username=?", username);
    }

    public User getByPhone(String phone) {
        return userDao.getByWhere("phone=?", phone);
    }

    public User save(User user) {
        if (user.getId() > 0) {
            userDao.update(user);
        } else {
            user.setCode(userDao.getNextCode());
            Object id = userDao.add(user);
            user.setId(Integer.valueOf(id.toString()));
        }
        return user;
    }
}
