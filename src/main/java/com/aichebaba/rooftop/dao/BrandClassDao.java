package com.aichebaba.rooftop.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.BrandClass;
import com.jfinal.plugin.activerecord.dao.Dao;

@Repository
public class BrandClassDao extends Dao<BrandClass, Integer> {
    public BrandClassDao() {
        super("brand_class", BrandClass.class);
    }

    public BrandClass getBrandClassBy2Id(int brandId, int classId) {
        return getByWhere("brandId = ? and classId = ?", brandId, classId);
    }

    public List<BrandClass> findByBrandClass(AmosQuerier querier) {
        StringBuilder sql = new StringBuilder("select * from brand_class where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

	public int updateTopStatus(int newstatus, int oldstatus) {
		return (int) updateByWhere("top=?", "top=?", newstatus, oldstatus);
	}
}
