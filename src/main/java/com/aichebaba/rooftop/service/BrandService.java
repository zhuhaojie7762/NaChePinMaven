package com.aichebaba.rooftop.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aichebaba.rooftop.dao.BrandClassDao;
import com.aichebaba.rooftop.dao.BrandCustomerDao;
import com.aichebaba.rooftop.dao.BrandDao;
import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.BrandClass;
import com.aichebaba.rooftop.model.BrandCustomer;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.utils.DateUtil;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;

@Service
public class BrandService {
    @Autowired
    private BrandDao brandDao;

    @Autowired
    private BrandClassDao brandClassDao;

    @Autowired
    private BrandCustomerDao brandCustomerDao;

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页获取品牌信息
     * @param params
     *          id
     *          name    名称
     *          createdFrom     创建时间-开始
     *          createdEnd      创建时间-结束
     * @param pageParam
     * @return
     */
    public PageList<Brand> findBrands(Map<String, Object> params, PageParam pageParam){
        Date createdEnd = (Date) params.get("createdEnd");
        if (createdEnd != null) {
            params.put("createdEnd", DateUtil.getDayEnd(createdEnd));
        }
        return brandDao.findBrands(params, pageParam);
    }

    /**
     * 通过ID获取品牌信息
     * @param brandId   品牌ID
     * @return  品牌信息
     */
    public Brand getById(int brandId) {
        return brandDao.getByPK(brandId);
    }

    /**
     * 通过名称获取品牌信息
     * @param name  品牌名称
     * @return 品牌信息
     */
    public Brand getByName(String name){
        return brandDao.getByWhere("name = ?", name);
    }

    /**
     * 保存品牌信息
     * @param brand
     * @return
     */
    public Brand save(Brand brand) {
        if (brand.getId() > 0) {
            brandDao.update(brand);
        } else {
            brand.setCreated(new Date());
            Object o = brandDao.add(brand);
            brand.setId(Integer.parseInt(o.toString()));
        }
        return brand;
    }

	/**
	 * 查找所有
	 * 
	 * @return
	 */
	public List<Brand> findAll() {
		return brandDao.findAll();
	}

    /**
     * 获取品牌-分类关联信息
     * @param brandId
     * @param classId
     * @return
     */
    public BrandClass getBrandClassBy2Id(int brandId, int classId) {
        return brandClassDao.getBrandClassBy2Id(brandId, classId);
    }

    /**
     * 删除品牌-分类关联
     * @param id
     */
    public void delBrandClassById(int id) {
        brandClassDao.delByIds(id);
    }

	/**
	 * 保存
	 *
	 * @param brandId
     * @param classId
	 * @return
	 */
	public BrandClass save(int brandId, int classId) {
		BrandClass brandClass = brandClassDao.getBrandClassBy2Id(brandId, classId);
		if (null == brandClass) {
			brandClass = new BrandClass();
			brandClass.setBrandId(brandId);
			brandClass.setClassId(classId);
			brandClass.setId(Integer.valueOf(brandClassDao.add(brandClass).toString()));
		}
		return brandClass;
	}

    public PageList<Brand> findAll(Brand brand, PageParam pageParam) {
        return brandDao.findAll(brand, pageParam);
    }

    public List<BrandClass> findByBrandClass(AmosQuerier querier) {
        return brandClassDao.findByBrandClass(querier);
    }

    public List<Brand> findBrandByChoose(AmosQuerier querier) {
        return brandDao.findBrandByChoose(querier);
    }

    /**
     * 获取指定分类的品牌
     * @param classId   分类ID
     * @return
     */
    public List<Brand> findBrandByClassId(int classId){
        return brandDao.findBrandByClassId(classId);
    }

    /**
     * 获取指定供货商的品牌
     * @param customerId   供货商ID
     * @return
     */
    public List<Brand> findBrandByCustomerId(int customerId){
        return brandDao.findBrandByCustomerId(customerId);
    }

    /**
     * 保存供货商-品牌关系
     * @param brandId       品牌ID
     * @param customerId    供货商ID
     * @return
     */
    public BrandCustomer saveBrandCustomer(int brandId, int customerId){
        BrandCustomer brandCustomer = brandCustomerDao.getBrandCustomerBy2Id(brandId, customerId);
        if (null == brandCustomer) {
            brandCustomer = new BrandCustomer();
            brandCustomer.setBrandId(brandId);
            brandCustomer.setCustomerId(customerId);
            brandCustomer.setId(Integer.valueOf(brandCustomerDao.add(brandCustomer).toString()));
        }
        return brandCustomer;
    }

    /**
     * 获取供货商-品牌关系
     * @param id
     * @return
     */
    public BrandCustomer getBrandCustomerById(int id){
        BrandCustomer brandCustomer = brandCustomerDao.getById(id);
        return brandCustomer;
    }

    public BrandCustomer getBrandCustomerBrandId(int brandId){
        return brandCustomerDao.get("brandId = ?", brandId);
    }

    /**
     * 删除供货商-品牌关系
     * @param id
     */
    public void delBrandCustomer(int id) {
        brandCustomerDao.delByIds(id);
    }

    /**
     * 删除品牌
     * @param brandId
     * @return
     */
    public Boolean delBrand(int brandId){
        AmosQuerier querier = AmosDB.newQuerier().equals("`brandid`", brandId).limit(1);
        List<Goods> goodses = goodsService.findByGoods(querier);
        if (goodses.size() > 0) {
            return false;
        }
        brandClassDao.deleteByWhere("brandId = ?", brandId);
        brandCustomerDao.deleteByWhere("brandId = ?", brandId);
        brandDao.delByIds(brandId);
        return true;
    }

	public PageList<Brand> findBrands(Integer firstclassid, 
									  Integer secondclassid, 
									  Integer thirdclassid, 
									  String name,
			                          Integer top, 
			                          PageParam pageParam) {
		return brandDao.findBrands(firstclassid, 
								   secondclassid, 
								   thirdclassid, 
								   name, 
								   top, 
								   pageParam);
	}

	public int updateTopStatus(int newstatus, int oldstatus) {
		return brandClassDao.updateTopStatus(newstatus, oldstatus);
	}

	public void updateTopStatusByIdAndThirdclassid(Integer id, Integer thirdclassid, int topstate) {
		brandDao.updateTopStatusByIdAndThirdclassid(id, thirdclassid, topstate);
	}
}
