package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Province;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProvinceDao extends Dao<Province, Integer> {

    public ProvinceDao() {
        super("province", Province.class);
    }

    public List<Province> findAllDisplay(Boolean display) {
        return findByWhere("display=? order by pinyin ", display);
    }

    public PageList<Province> findPager(String name, Boolean display, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from province where 1=1");
        if (StrKit.notBlank(name)) {
            sql.append(" and name like ?");
            params.add(StringUtils.likeValue(name));
        }
        if (display != null) {
            sql.append(" and display = ?");
            params.add(display);
        }
        return findPaging(sql, pageParam, params);
    }

    public Province findByProvince(String name) {
//        StringBuilder sql = new StringBuilder();
//        List<Object> params = new ArrayList<>();
//        sql.append("select * from province");
//        if (StrKit.notBlank(name)) {
//            sql.append(" where name = ?");
//            params.add(name);
//        }
//        return getBySQL(sql.toString(), params);

        return  getByWhere("name like ?", StringUtils.likeValue(name));
    }
}
