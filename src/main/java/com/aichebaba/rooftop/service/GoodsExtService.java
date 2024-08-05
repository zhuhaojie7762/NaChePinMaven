package com.aichebaba.rooftop.service;

import java.util.List;

import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aichebaba.rooftop.dao.GoodsExtDao;
import com.aichebaba.rooftop.model.GoodsExt;

@Service
public class GoodsExtService {
	private static Logger logger = LoggerFactory.getLogger(GoodsExtService.class);

	@Autowired
	private GoodsExtDao goodsExtDao;

	public GoodsExt save(GoodsExt goodsExt) {
		if (goodsExt.getId() > 0) {
			goodsExtDao.update(goodsExt);
		} else {
			Object o = goodsExtDao.add(goodsExt);
			goodsExt.setId(Integer.parseInt(o.toString()));
		}
		return goodsExt;
	}

	public List<GoodsExt> getGoodsValue(Integer goodId){
		return goodsExtDao.findByWhere("goodid = ?", goodId);
	}

	public void deleteByGoodId(int id) {
		logger.info("删除属性值,商品id = {},记录数 {}", id, goodsExtDao.deleteByWhere(" goodid = ? ", id));
	}

	public List<GoodsExt> findByGoodsExt(AmosQuerier querier) {
		return goodsExtDao.findByGoodsExt(querier);
	}
}
