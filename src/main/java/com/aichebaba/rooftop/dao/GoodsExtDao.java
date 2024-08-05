package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import org.springframework.stereotype.Repository;

import com.aichebaba.rooftop.model.GoodsExt;
import com.jfinal.plugin.activerecord.dao.Dao;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GoodsExtDao extends Dao<GoodsExt, Integer> {
	public GoodsExtDao() {
		super("goods_ext", GoodsExt.class);
	}

	public List<GoodsExt> findByGoodsExt(AmosQuerier querier) {
		StringBuilder sql = new StringBuilder("select * from goods_ext where 1 = 1");
		List<Object> paramList = new ArrayList<>();
		AmosDB.list(sql, paramList, querier);
		return findSQL(sql.toString(), paramList.toArray());
	}
}
