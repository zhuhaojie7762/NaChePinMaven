package com.aichebaba.rooftop.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.aichebaba.rooftop.model.GoodsClass;
import com.jfinal.plugin.activerecord.dao.Dao;

@Repository
public class GoodsClassDao extends Dao<GoodsClass, Integer> {
    public GoodsClassDao() {
		super("goods_class", GoodsClass.class);
    }

	public GoodsClass getByName(String name) {
        return getByWhere("name=?", name);
    }

	public List<GoodsClass> getAll() {
		return findAll();
	}

	public List<GoodsClass> findByLevel(int level) {
		return findByWhere("level=?", level);
	}

	public List<GoodsClass> findGoodsClassByGoodid(int id) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				"select class.* from goods_ext ext , goods_class class where ext.classid = class.id and ext.goodid = ? ");

		return findSQL(sql.toString(), id);
	}

	/**
	 *
	 * @param brandId
	 * @return
	 */
	public List<GoodsClass> findGoodsClassByBrandId(int brandId) {
		StringBuilder sql = new StringBuilder();

		sql.append("select c.* from goods_class c ")
				.append(" inner join brand_class b on c.id = b.classId ")
				.append(" where b.brandId = ? ");

		return findSQL(sql.toString(), brandId);
	}

}
