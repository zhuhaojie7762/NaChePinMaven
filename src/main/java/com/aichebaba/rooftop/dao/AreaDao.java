package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Area;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AreaDao extends Dao<Area, Integer> {
    public AreaDao() {
        super("areas", Area.class);
    }

    public Area getByName(String name) {
        return getByWhere("name=?", name);
    }

    public PageList<Area> findAreas(String name, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.*, u.name pickUserName from areas a left join user u on a.pickUserId=u.id where 1=1");
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(name)) {
            sql.append(" and a.name like ?");
            paras.add(StringUtils.likeValue(name));
        }
        return findPaging(sql, pageParam, paras);
    }
}
