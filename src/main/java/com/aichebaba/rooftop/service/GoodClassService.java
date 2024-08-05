package com.aichebaba.rooftop.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aichebaba.rooftop.dao.GoodsClassDao;
import com.aichebaba.rooftop.dao.GoodsDao;
import com.aichebaba.rooftop.dao.GoodsExtDao;
import com.aichebaba.rooftop.model.GoodsClass;
import com.jfinal.plugin.activerecord.dao.PageParam;

@Service
public class GoodClassService {
	private static Logger logger = LoggerFactory.getLogger(GoodsService.class);

	@Autowired
	private GoodsClassDao goodsClassDao;

	@Autowired
	private GoodsExtDao goodsExtDao;

	@Autowired
	private GoodsDao goodsDao;

	public GoodsClass getById(Integer id) {
		return goodsClassDao.getById(id);
	}

	public List<GoodsClass> getAll() {
		return goodsClassDao.findAll();
	}

	public List<GoodsClass> getChildren(Integer pid) {
		return goodsClassDao.findByWhere("pid = ?", pid);
	}

	public List<GoodsClass> getLevel(Integer level) {
		return  goodsClassDao.findByWhere("level = ?", level);
	}

	public boolean edit(Integer id, String name) {
		GoodsClass goodsClass = goodsClassDao.getById(id);
		if (null == goodsClass) {
			logger.error("分类不存在");
			return false;
		}
		goodsClass.setName(name);
		return goodsClassDao.update(goodsClass);
	}

	public boolean edit(GoodsClass entity) {
		return goodsClassDao.update(entity);
	}

	public GoodsClass add(GoodsClass entity) {
		Object id = goodsClassDao.add(entity);
		entity.setId(Integer.parseInt(id.toString()));
		return entity;
	}

	public Object delete(Integer id) {
		return goodsClassDao.delByIds(id);
	}

	/**
	 * 获取所有的三级分类及属性信息
	 * @return
	 */
	public List<GoodsClass> getAllThirdClassInfo(){
		List<GoodsClass> classes = goodsClassDao.findByLevel(2);
		for(GoodsClass goodsClass : classes){
			//获取属性
			List<GoodsClass> attributes = getAllAttributes(goodsClass.getId());
			goodsClass.setChildren(attributes);
		}
		return classes;
	}

	/**
	 * 获取指定分类的属性及属性选项
	 * @param thirdClassId
	 * @return
	 */
	public List<GoodsClass> getAllAttributes(int thirdClassId){
		//获取属性
		List<GoodsClass> attributes = getChildren(thirdClassId);
		//获取属性值选项
		for(GoodsClass item : attributes){
			List<GoodsClass> values = getChildren(item.getId());
			item.setChildren(values);
		}
		return attributes;
	}

	/**
	 * 根据商品id获取属性值列表
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsClass> findGoodsClassByGoodid(int id) {
		return goodsClassDao.findGoodsClassByGoodid(id);
	}

	/**
	 * 获取品牌对应的分类
	 * @param brandId
	 * @return
	 */
	public List<GoodsClass> findGoodsClassByBrandId(int brandId) {
		return goodsClassDao.findGoodsClassByBrandId(brandId);
	}

	/**
	 * 分类是否被使用
	 * 
	 * @param classid
	 * @param level
	 * @return
	 */
	public boolean isUsed(int classid, int level) {
		List<Object> paras = new ArrayList<>();
		paras.add(classid);
		switch (level) {
		case 0:
			return goodsDao.findPaging("select * from goods where firstclassid = ? ", new PageParam(1, 1), paras)
					.getData().size() > 0;
		case 1:
			return goodsDao.findPaging("select * from goods where secondclassid = ? ", new PageParam(1, 1),
					paras).getData().size() > 0;
		case 2:
			return goodsDao.findPaging("select * from goods where thirdclassid = ? ", new PageParam(1, 1),
					paras).getData().size() > 0;
		case 4:
			return goodsExtDao.findPaging("select * from goods_ext where classid = ? ", new PageParam(1, 1),
					paras).getData().size() > 0;
		default:
			return false;
		}
	}
}
