package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.County;
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
public class CountyDao extends Dao<County, Integer> {

    public CountyDao() {
        super("county", County.class);
    }

    public List<County> findAllDisplay() {
        return findByWhere("display=? order by pinyin ", true);
    }

    public List<County> findByCity(int cityId, boolean display) {
        return findByWhere("cityId=? and display=?", cityId, display);
    }

    public List<County> findByCity(int cityId) {
        return findByWhere("cityId=?", cityId);
    }

    public PageList<County> findPager(int provinceId, String provinceName, int cityId, String cityName, String name,
                                      Boolean display, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select co.*, p.name provinceName, c.name cityName from county co left join city c on co.cityId=c.id")
                .append(" left join province p on p.id=c.provinceId where 1=1");
        if (provinceId > 0) {
            sql.append(" and p.id = ?");
            params.add(provinceId);
        }
        if (cityId > 0) {
            sql.append(" and c.id = ?");
            params.add(cityId);
        }
        if (StrKit.notBlank(provinceName)) {
            sql.append(" and p.name like ?");
            params.add(StringUtils.likeValue(provinceName));
        }
        if (StrKit.notBlank(cityName)) {
            sql.append(" and c.name like ?");
            params.add(StringUtils.likeValue(cityName));
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and co.name like ?");
            params.add(StringUtils.likeValue(name));
        }
        if (display != null) {
            sql.append(" and co.display = ?");
            params.add(display);
        }
        return findPaging(sql, pageParam, params);
    }

    public County findByCounty(Integer cityId, String name) {
        return getByWhere("cityId = ? and name like ?", new Object[]{cityId, StringUtils.likeValue(name)});
    }
}
