package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.enums.UserState;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends GeneralCodeDao<User, Integer> {
    public UserDao() {
        super("user", User.class, "YG", "seq_user");
    }

    public PageList<User> findUsers(UserState state, String name, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from user where 1=1 ");
        if (state != null) {
            sql.append(" and state=?");
            paras.add(state.ordinal());
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and name like ?");
            paras.add(StringUtils.likeValue(name));
        }

        return findPaging(sql, pageParam, paras);
    }
}
