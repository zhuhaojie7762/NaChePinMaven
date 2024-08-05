package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/5.
 */
@Repository
public class CityDao extends Dao<City, Integer> {

    public CityDao() {
        super("city", City.class);
    }

    public List<City> findAllDisplay() {
        return findByWhere("display=? order by pinyin ", true);
    }

    public List<City> findByProvince(int provinceId, boolean display) {
        return findByWhere("provinceId=? and display=?", provinceId, display);
    }

    public List<City> findByProvince(int provinceId) {
        return findByWhere("provinceId=? ", provinceId);
    }

    public PageList<City> findPager(int provinceId, String provinceName, String name, Boolean display, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select c.*, p.name provinceName from city c left join province p on p.id=c.provinceId where 1=1");
        if (provinceId > 0) {
            sql.append(" and p.id = ?");
            params.add(provinceId);
        }
        if (StrKit.notBlank(provinceName)) {
            sql.append(" and p.name like ?");
            params.add(StringUtils.likeValue(provinceName));
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and c.name like ?");
            params.add(StringUtils.likeValue(name));
        }
        if (display != null) {
            sql.append(" and c.display = ?");
            params.add(display);
        }
        return findPaging(sql, pageParam, params);
    }

    public City findByCity(Integer proId, String name) {
        return getByWhere("provinceId = ? and name like ?", new Object[]{proId, StringUtils.likeValue(name)});
    }
}
