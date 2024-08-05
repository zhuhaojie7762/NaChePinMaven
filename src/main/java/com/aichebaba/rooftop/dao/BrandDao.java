package com.aichebaba.rooftop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;

@Repository
public class BrandDao extends Dao<Brand, Integer> {
    public BrandDao(){
        super("brand", Brand.class);
    }

    /**
     * 分页获取品牌信息
     * @param params
     *          id
     *          name            名称
     *          createdFrom     创建时间-开始
     *          createdEnd      创建时间-结束
     * @param pageParam
     * @return
     */
    public PageList<Brand> findBrands(Map<String, Object> params, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        Integer intTemp;
        String strTemp;
        Date dtTemp;

        sql.append("select * from brand b ")
                .append("where 1 = 1");

        //id
        intTemp = (Integer) params.get("id");
        if (intTemp != null && intTemp > 0) {
            sql.append(" and b.id = ? ");
            paras.add(intTemp);
        }

        //名称
        strTemp = (String) params.get("name");
        if (StrKit.notBlank(strTemp)) {
            sql.append(" and b.name like ? ");
            paras.add(StringUtils.likeAllValue(strTemp));
        }

        //创建时间-开始
        dtTemp = (Date) params.get("createdFrom");
        if (dtTemp != null) {
            sql.append(" and b.created >= ? ");
            paras.add(dtTemp);
        }

        //创建时间-结束
        dtTemp = (Date) params.get("createdEnd");
        if (dtTemp != null) {
            sql.append(" and b.created <= ? ");
            paras.add(dtTemp);
        }

        return findPaging(sql, pageParam, paras);
    }

    public PageList<Brand> findAll(Brand brand, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from brand where 1 = 1");

        List<Object> param = new ArrayList<>();
        if (StrKit.notBlank(brand.getPinyin())) {
            sql.append(" and pinyin = ?");
            param.add(brand.getPinyin());
        }

        return findPaging(sql, pageParam, param);
    }

    /**
     * 获取指定分类的品牌
     * @param classId   分类ID
     * @return
     */
    public List<Brand> findBrandByClassId(int classId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select b.* from brand b ")
                .append(" inner join brand_class c on b.id = c.brandId ")
                .append(" where c.classId = ? ");
        return findSQL(sql.toString(), classId);
    }

    /**
     * 获取指定供货商的品牌
     * @param customerId   供货商ID
     * @return
     */
    public List<Brand> findBrandByCustomerId(int customerId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select b.* from brand b ")
                .append(" inner join brand_customer c on b.id = c.brandId ")
                .append(" where c.customerId = ? ");
        return findSQL(sql.toString(), customerId);
    }

	public PageList<Brand> findBrands(Integer firstclassid, 
									  Integer secondclassid, 
									  Integer thirdclassid, 
									  String name,
									  Integer top, 
									  PageParam pageParam) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT "
				 + " 	    b.id, "
				 + " 	    b.name, "
				 + " 	    b.logo, "
				 + " 	    b.comment, "
				 + " 	    b.created, "
				 + " 	    b.pinyin, "
				 + " 	    c1.id thirdclassid, "
				 + " 	    c1.name thirdclassname, "
				 + " 	    c2.id secondclassid, "
				 + " 	    c2.name secondclassname, "
				 + " 	    c3.id firstclassid, "
				 + " 	    c3.name firstclassname, "
				 + " 	    cust.supplierCompany supplierCompany, "
				 + " 	    bc.top, "
				 + " 	    bc.toptime "
				 + " 	FROM "
				 + " 	    brand b "
				 + " 	        LEFT JOIN "
				 + " 	    brand_customer bcust ON b.id = bcust.brandId "
				 + " 	        LEFT JOIN "
				 + " 	    customer cust ON bcust.customerId = cust.id "
				 + "        , brand_class bc, goods_class c1, goods_class c2, goods_class c3 "
				 + "    WHERE b.id = bc.brandId AND bc.classId = c1.id AND c1.pid = c2.id AND c2.pid = c3.id");
		
		List<Object> paras = new ArrayList<>();
		
		if (firstclassid != null) {
			sql.append(" and c3.id = ? ");
			paras.add(firstclassid);
		}

		if (secondclassid != null) {
			sql.append(" and c2.id = ? ");
			paras.add(secondclassid);
		}

		if (thirdclassid != null) {
			sql.append(" and c1.id = ? ");
			paras.add(thirdclassid);
		}

		if (StrKit.notBlank(name)) {
			sql.append(" and b.name like ? ");
			paras.add(StringUtils.likeAllValue(name));
		}

		if (top != null) {
			if (top == 0) {
				sql.append(" and bc.top in (0,2) ");
			} else {
				sql.append(" and bc.top in (3,1) ");
			}
		}

		pageParam.putSort("field( bc.top, 3, 1, 2, 0 )", "asc");
		pageParam.putSort("bc.toptime ", "desc");

		return findPaging(sql, pageParam, paras);
	}

    public List<Brand> findBrandByChoose(AmosQuerier querier) {
		StringBuilder sql = new StringBuilder(
				"select b.id, b.name, b.logo, b.comment, b.created, b.pinyin from brand b right join brand_class bc on b.id = bc.brandId where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

	public int updateTopStatusByIdAndThirdclassid(Integer id, Integer thirdclassid, int topstate) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sql.append("update brand_class set top = ?, toptime = now() where brandid = ? AND classid = ? ");
		params.add(topstate);
		params.add(id);
		params.add(thirdclassid);
		return (int) update(sql.toString(), params.toArray());

	}
}
