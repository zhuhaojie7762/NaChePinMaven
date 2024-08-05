package com.aichebaba.rooftop.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.vo.AttrSearch;
import com.aichebaba.rooftop.vo.SearchParams;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;

@Repository
public class GoodsDao extends GeneralCodeDao<Goods, Integer> {

    private Logger logger = LoggerFactory.getLogger(GoodsDao.class);

    public GoodsDao() {
        super("goods", Goods.class, "SP", "seq_goods");
    }

    /**
     * 分页查询商品
     *
     * @param type         商品类型
     * @param status       商品状态
     * @param goodsName    商品名
     * @param brand        品牌
     * @param code         商品编码
     * @param itemNo       商品货号
     * @param sellerId     卖家ID
     * @param startOffline 下架时间(开始)
     * @param endOffline   下架时间(结束)
     * @param startStock   库存(开始)
     * @param endStock     库存(结束)
     * @param exceptGoodsName  含有该名称的除外(不显示专拍的商品)
     * @param pageParam    分页设置
     * @return
     */
    public PageList<Goods> findGoods(GoodsType type, GoodsStatus status, String goodsName, String brand, String code,
            String itemNo, int sellerId, Date startOffline, Date endOffline, int startStock, int endStock, String exceptGoodsName,
            PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("select g.* from goods g").append(" where 1 = 1 ");

        if (type != null) {
            sql.append(" and g.type = ? ");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        if (StrKit.notBlank(goodsName)) {
            sql.append(" and g.name like ? ");
            params.add(StringUtils.likeAllValue(goodsName));
        }
        if (StrKit.notBlank(brand)) {
            sql.append(" and g.brand like ? ");
            params.add(StringUtils.likeAllValue(brand));
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and g.code = ? ");
            params.add(code);
        }
        if (StrKit.notBlank(itemNo)) {
            sql.append(" and g.itemNo = ? ");
            params.add(itemNo);
        }
        if (sellerId > 0) {
            sql.append(" and g.sellerId = ? ");
            params.add(sellerId);
        }
        if (startOffline != null) {
            sql.append(" and g.offlineTime >= ? ");
            params.add(startOffline);
        }
        if (endOffline != null) {
            sql.append(" and g.offlineTime <= ? ");
            params.add(endOffline);
        }
        if (startStock > 0) {
            sql.append(" and g.stock >= ? ");
            params.add(startStock);
        }
        if (endStock > 0) {
            sql.append(" and g.stock <= ? ");
            params.add(endStock);
        }

        if(StrKit.notBlank(exceptGoodsName)){
            sql.append(" and g.name not like ? ");
            params.add(StringUtils.likeAllValue(exceptGoodsName));
        }

        sql.append(" order by g.created desc ");
        return findPaging(sql, pageParam, params);
    }
    
	public Goods findGoodsById(String id) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				"select g.*, c.name customerName, c.supplierCompany, ifnull(s.totalCount, 0) sellcnt, c1.name firstclassname, c2.name secondclassname, c3.name thirdclassname, b.name brand from goods g left join sales_volume s on g.code = s.goodsCode left join brand b on g.brandid = b.id ");

		sql.append(
				" left join goods_class c1 on g.firstclassid = c1.id left join goods_class c2 on g.secondclassid = c2.id left join goods_class c3 on g.thirdclassid = c3.id, customer c where g.sellerid = c.id ");

		sql.append(" and g.id = ? ");

		return getBySQL(sql.toString(), id);
	}

	public Goods findGoodsByCode(String code) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				"select g.*, c.name customerName, c.supplierCompany, ifnull(s.totalCount, 0) sellcnt, c1.name firstclassname, c2.name secondclassname, c3.name thirdclassname, b.name brand from goods g left join sales_volume s on g.code = s.goodsCode left join brand b on g.brandid = b.id ");

		sql.append(
				" left join goods_class c1 on g.firstclassid = c1.id left join goods_class c2 on g.secondclassid = c2.id left join goods_class c3 on g.thirdclassid = c3.id, customer c where g.sellerid = c.id ");

		sql.append(" and g.code = ? ");

		return getBySQL(sql.toString(), code);
	}

    /**
	 * 分页查询商品
	 * 
	 * @param firstclassid  一级分类
	 * @param secondclassid 二级分类
	 * @param thirdclassid  三级分类
	 * @param minprice		单价范围
	 * @param maxprice		单价范围
	 * @param minsellcnt    销量范围
	 * @param maxsellcnt    销量范围
	 * @param itemNo		商品货号
	 * @param name			商品名称
	 * @param pageParam		分页设置
	 * @return
	 */
    public PageList<Goods> findGoods(Integer firstclassid, 
    								 Integer secondclassid, 
    								 Integer thirdclassid, 
    								 Integer fourthclassid,
    								 Integer fifthclassid,
    								 Integer brandid,
    								 Integer minprice, 
    								 Integer maxprice, 
    								 Integer minsellcnt, 
    								 Integer maxsellcnt, 
    								 String itemNo,
    								 String name,
    								 GoodsStatus status,
    								 Integer customerId,
    								 String customerName,
    								 Integer submittype,
    								 Integer top,
    								 boolean orderbytop,
    								 PageParam pageParam) {

        StringBuilder sql = new StringBuilder();

        List<Object> params = new ArrayList<>();
        
        if (fifthclassid == null) {
        	
        	sql.append("SELECT "
					+ "	       g.*, "
					+ "	       c.name customerName, "
					+ "	       c.supplierCompany, "
					+ "	       IFNULL(s.totalCount, 0) sellcnt, "
					+ "	       c1.name firstclassname, "
					+ "	       c2.name secondclassname, "
					+ "	       c3.name thirdclassname "
					+ "	  FROM "
					+ "	       goods g "
					+ "	           LEFT JOIN "
					+ "	       sales_volume s ON g.code = s.goodsCode "
					+ "	           LEFT JOIN "
					+ "	       goods_class c1 ON g.firstclassid = c1.id "
					+ "	           LEFT JOIN "
					+ "	       goods_class c2 ON g.secondclassid = c2.id "
					+ "	           LEFT JOIN "
					+ "	       goods_class c3 ON g.thirdclassid = c3.id, "
					+ "	       customer c "
					+ "   WHERE "
					+ "	    g.sellerid = c.id ");
        	
        	
        } else {
        	
        	sql.append("SELECT 		"
	        		+ "      g.*, "
	        		+ "      c.name customerName, "
	        		+ "      c.supplierCompany, "
	        		+ "      IFNULL(s.totalCount, 0) sellcnt, "
	        		+ "      c1.name firstclassname, "
	        		+ "      c2.name secondclassname, "
	        		+ "      c3.name thirdclassname "
					+ "	 FROM "
	        		+ "      goods g "
	        		+ "          LEFT JOIN "
	        		+ "      sales_volume s ON g.code = s.goodsCode "
	        		+ "          LEFT JOIN "
	        		+ "      goods_class c1 ON g.firstclassid = c1.id "
	        		+ "          LEFT JOIN "
	        		+ "      goods_class c2 ON g.secondclassid = c2.id "
	        		+ "          LEFT JOIN "
	        		+ "      goods_class c3 ON g.thirdclassid = c3.id, "
	        		+ "      customer c, "
	        		+ "      (SELECT  "
	        		+ "          goodid "
	        		+ "      FROM "
	        		+ "          goods_ext "
	        		+ "      WHERE "
					+ "          classid = ?) ext "
					+ "   WHERE "
					+ "      g.sellerid = c.id AND g.id = ext.goodid ");
        	
			params.add(fifthclassid);
        }

		if (orderbytop) {
			pageParam.putSort("field( g.top, 3, 1, 2, 0 )", "asc");
			pageParam.putSort("g.toptime ", "desc");
		}

		
		if (firstclassid != null) {
			sql.append(" and g.firstclassid = ? ");
			params.add(firstclassid);
        }
		
		if (secondclassid != null) {
			sql.append(" and g.secondclassid = ? ");
			params.add(secondclassid);
		}

		if (thirdclassid != null) {
			sql.append(" and g.thirdclassid = ? ");
			params.add(thirdclassid);
        }

		if (brandid != null) {
			sql.append(" and g.brandid = ? ");
			params.add(brandid);
		}

		if (submittype != null) {
			sql.append(" and g.submittype = ? ");
			params.add(submittype);
		}

		if (top != null) {
			if (top == 0) {
				sql.append(" and g.top in (0,2) ");
			} else {
				sql.append(" and g.top in (3,1) ");
			}
		}

        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());

			if (status == GoodsStatus.online) {
				pageParam.putSort(" g.onlineTime ", "desc");
			}

			if (status == GoodsStatus.offline) {
				pageParam.putSort(" g.offlineTime ", "desc");
			}

			if (status == GoodsStatus.wait_audit) {
				pageParam.putSort(" g.created ", "desc");
			}

		} else {
			// 状态为空
			sql.append(" and status in (0, 10, 20) ");
			pageParam.putSort(" g.created ", "desc");
        }

		if (StrKit.notBlank(name)) {
            sql.append(" and g.name like ? ");
			params.add(StringUtils.likeAllValue(name));
        }

        if (StrKit.notBlank(itemNo)) {
            sql.append(" and g.itemNo = ? ");
            params.add(itemNo);
        }

		if (null != minprice && null != maxprice) {
			sql.append(" and g.retailprice between ? and ? ");
			params.add(Math.min(minprice, maxprice));
			params.add(Math.max(minprice, maxprice));
		}

		if (StrKit.notBlank(customerName)) {
			sql.append(" and c.name like ? ");
			params.add(StringUtils.likeAllValue(customerName));
		}

		if (null != customerId) {
			sql.append(" and c.id = ? ");
			params.add(customerId);
		}

		if (null != minsellcnt && null != maxsellcnt) {
			sql.append(" AND s.totalCount BETWEEN ? AND ? ");
			params.add(Math.min(minsellcnt, maxsellcnt));
			params.add(Math.max(minsellcnt, maxsellcnt));
		}

        return findPaging(sql, pageParam, params);
    }

    public PageList<Goods> findGoods(GoodsType type, GoodsStatus status, SearchParams searchParams) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("select g.* from goods g ")
                .append(" left outer join sales_volume s on s.goodsCode = g.code ")
                .append(" where 1 = 1 ");
        if (type != null) {
            sql.append(" and g.type = ? ");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        if (searchParams.notEmpty()) {
            Pair<String, List<Object>> pair = searchParams.getSqlParams();
            sql.append(pair.getKey());
            params.addAll(pair.getValue());
        }
        SearchParams.Where wd = searchParams.getParams().get("wd");
        if (wd != null)
        {
            sql.append(" and (g.name like ? || g.brand like ? )");
            params.add(StringUtils.likeAllValue(wd.getValue()));
            params.add(StringUtils.likeAllValue(wd.getValue()));
        }
        PageParam pageParam = searchParams.getPageParam();
        if (pageParam.getSortMap().isEmpty()) {
            pageParam.putSort("g.onlineTime", "desc");
        } else {
            pageParam.replaceSortField("time", "g.onlineTime");
            pageParam.replaceSortField("price", "g.wholesalePrice");
            pageParam.replaceSortField("saleNum", "s.totalCount");
        }
        return findPaging(sql, pageParam, params);
    }

    /**
     * 商品查询
     *
     * @param code
     * @param name
     * @param itemNo
     * @param isSpecial
     * @param sellerId
     * @param status
     * @param pageParam
     * @return
     */
    public PageList<Goods> findGoods(String code, String name, String itemNo, Boolean isSpecial, int sellerId,
            GoodsStatus status, GoodsType type, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from goods where 1=1");
        if (StrKit.notBlank(code)) {
            sql.append(" and code = ?");
            params.add(code);
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and name like ?");
            params.add(StringUtils.likeValue(name));
        }
        if (StrKit.notBlank(itemNo)) {
            sql.append(" and itemNo = ?");
            params.add(itemNo);
        }
        if (isSpecial != null) {
            sql.append(" and isSpecial = ?");
            params.add(isSpecial);
        }
        if (sellerId > 0) {
            sql.append(" and sellerId = ?");
            params.add(sellerId);
        }
        if (status != null) {
            sql.append(" and status = ?");
            params.add(status.getVal());
		}
        if (type != null) {
            sql.append(" and type = ?");
            params.add(type.name());
        }
        pageParam.putSort("id", "desc");
        return findPaging(sql, pageParam, params);
    }

    /**
     * 分页查询商品(分类列表页用)
     * @param status        商品状态
     * @param searchParams  搜索条件
     * @return
     */
    public PageList<Goods> findClassGoods(GoodsStatus status, SearchParams searchParams){
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("select g.* from goods g ")
                .append(" left outer join sales_volume s on s.goodsCode = g.code ")
                .append(" where 1 = 1 ");

        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        if (searchParams.notEmpty()) {
            Pair<String, List<Object>> pair = searchParams.getSqlParams();
            sql.append(pair.getKey());
            params.addAll(pair.getValue());
        }
        if (!searchParams.getAttrs().isEmpty()) {
            sql.append(" and g.id in (")
                    .append("select DISTINCT g0.goodid from ");
            List<AttrSearch> attrs = new LinkedList<>();
            attrs.addAll(searchParams.getAttrs().values());
            for (int i = 0; i < attrs.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("goods_ext g").append(i);
            }
            sql.append(" where g0.classid = ? ");
            params.add(attrs.get(0).getValue());
            for (int i = 1; i < attrs.size(); i++) {
                sql.append(" and g0.goodid = g" + i + ".goodid ")
                        .append(" and g" + i + ".classid = ?");
                params.add(attrs.get(i).getValue());
            }
            sql.append(")");
        }
        PageParam pageParam = searchParams.getPageParam();
        if (pageParam.getSortMap().isEmpty()) {
            pageParam.putSort("g.onlineTime", "desc");
        } else {
            pageParam.replaceSortField("time", "g.onlineTime");
            pageParam.replaceSortField("price", "g.wholesalePrice");
            pageParam.replaceSortField("saleNum", "s.totalCount");
        }
        return findPaging(sql, pageParam, params);
    }

    public PageList<Goods> findHotGoods(GoodsType type, GoodsStatus status, String exceptGoodsName, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select g.* from goods g ")
                .append(" left outer join sales_volume s on s.goodsCode = g.code ")
                .append(" where 1 = 1 ");
        if (type != null) {
            sql.append(" and g.type = ? ");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        if (StrKit.notBlank(exceptGoodsName)) {
            sql.append(" and g.name not like ? ");
            params.add(StringUtils.likeAllValue(exceptGoodsName));
        }
        sql.append(" order by s.lastWeek desc, g.created desc ");

        return findPaging(sql, pageParam, params);
    }

    public PageList<Goods> findBrandGoods(GoodsType type, GoodsStatus status, String brand, String exceptGoodsName, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select * from goods g where 1=1");
        if (type != null) {
            sql.append(" and g.type = ? ");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        if (StrKit.notBlank(brand)){
            sql.append(" and g.brand = ? ");
            params.add(brand);
        }
        if (StrKit.notBlank(exceptGoodsName)) {
            sql.append(" and g.name not like ? ");
            params.add(StringUtils.likeAllValue(exceptGoodsName));
        }
        sql.append(" order by g.created desc ");
        return findPaging(sql, pageParam, params);
    }

    public PageList<Goods> attention(int customerId, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append(
                "SELECT g.code, g.itemNo, g.name, g.headImgUrl1, g.wholesalePrice, g.isSpecial, f.id followId from follow f")
                .append(" JOIN goods g on f.goodsCode = g.code").append(" where 1=1");
        if (customerId > 0) {
            sql.append(" and f.customerId = ?");
            paras.add(customerId);
        }
        return findPaging(sql, pageParam, paras);
    }

    public void addStock(String code, int num) {
        Goods goods = getByCode(code);
        chgStock(goods, num);
    }

    public void minusStock(String code, int num) {
        Goods goods = getByCode(code);
//        if (goods != null) {
//            goods.setSaleNum(goods.getSaleNum() + num);
//        }
        chgStock(goods, -num);
    }

    public void changeStock(String code, int sum){
        Goods good = getByCode(code);
        good.setStock(sum);
        update(good);
    }

    /**
     * @param goods
     * @param num   减少库存传负数
     */
    private void chgStock(Goods goods, int num) {
        if (goods != null) {
            logger.info("goods[{}] stock change src: {}, num: {}, new:{}", goods.getCode(), goods.getStock(), num,
                    goods.getStock() + num);
            goods.setStock(goods.getStock() + num);
            if (goods.getStock() < 0) {
                goods.setStock(0);
            }
            update(goods);
        }
    }

    public Goods getByCode(String code) {
        return getByWhere("code = ?", code);
    }

    public List<Goods> getGoodsByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty())
            return Collections.emptyList();
        return findByWhere(inSql("code", codes.size()), codes.toArray());
    }

    public void updateGoodsByWeight(int sellerId, int weight){
        updateByWhere("weighting=?", "sellerId=?", weight, sellerId);
    }

    public List<String> findBrandOrderByCount(GoodsType type, GoodsStatus status) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("select brand from goods g ")
                .append(" where 1 = 1 ");
        if (type != null) {
            sql.append(" and g.type = ? ");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" and g.status = ? ");
            params.add(status.getVal());
        }
        sql.append(" group by g.brand ");
        sql.append(" order by count(g.id) desc ");

        return findSingleList(sql.toString(), params.toArray());
    }

	public int updateStatusById(Integer id, GoodsStatus status) {
		String setTime = "";
		if (status == GoodsStatus.online) {
			setTime = ", onlineTime = ? ";
		} else if (status == GoodsStatus.offline) {
			setTime = ", offlineTime = ? ";
		} else if (status == GoodsStatus.deleted) {
			setTime = ", deleted = ? ";
		} else {
			setTime = "";
		}

		if (StringUtils.isBlank(setTime)) {
			return (int) updateByWhere(" status = ? " + setTime, " id = ? ", status.getVal(), id);
		} else {
			return (int) updateByWhere(" status = ? " + setTime, " id = ? ", status.getVal(), new Date(), id);
		}
	}

	public int updateStatusByIds(Integer[] ids, GoodsStatus status) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		String setTime = "";

		if (status == GoodsStatus.online) {
			setTime = ", onlineTime = now() ";
		} else if (status == GoodsStatus.offline) {
			setTime = ", offlineTime = now() ";
		} else if (status == GoodsStatus.deleted) {
			setTime = ", deleted = now() ";
		} else {
			setTime = "";
		}

		sql.append("update goods set status = ? " + setTime + " where id in ( ");
		params.add(status.getVal());

		for (int i = 0; i < ids.length; i++) {
			if (i != ids.length - 1) {
				sql.append(" ?, ");
			} else {
				sql.append(" ? ");
			}
			params.add(ids[i]);
		}

		sql.append(" ) ");

		return (int) update(sql.toString(), params.toArray());
	}

    // S HeDaoYuan ///////////////////////////////////////////////////
    public List<Goods> findByHomeGoods(AmosQuerier querier) {
        StringBuilder sql = new StringBuilder("select g.* from goods g join goods_ext ge on g.id = ge.goodid where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

    public List<Goods> findByGoods(AmosQuerier querier) {
        StringBuilder sql = new StringBuilder("select * from goods where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

    public List<Goods> findByGoodsLeftSales(AmosQuerier querier) {
        StringBuilder sql = new StringBuilder("select g.* from goods g left join sales_volume s on s.goodsCode = g.code where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

    public PageList<Goods> findByGoodsLeftSalesPage(AmosQuerier querier, PageParam pageParam) {
        StringBuilder sql = new StringBuilder("select g.* from goods g left join sales_volume s on s.goodsCode = g.code where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findPaging(sql, pageParam, paramList);
    }
    // E HeDaoYuan ///////////////////////////////////////////////////

    /**
     * 获取商品数量
     * @param status    商品状态
     * @return
     */
    public Long getGoodsCount(GoodsStatus status){
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from goods ")
                .append(" where status = ? ");
        return getSingle(sql.toString(), status.getVal());
    }

    /**
     * 修改指定品牌的名称
     * @param brandId       需修改的品牌ID
     * @param brandName     修改后的品牌名
     */
    public void updateBrandName(int brandId, String brandName){
        StringBuilder sql = new StringBuilder();
        sql.append("update goods set ")
                .append(" brand = ? ")
                .append(" where brandid = ? ");
        update(sql.toString(), brandName, brandId);
    }

	public int updateTopStatusById(Integer id, int top) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();
		sql.append("update goods set top = ?, toptime = now() where id = ? ");
		params.add(top);
		params.add(id);
		return (int) update(sql.toString(), params.toArray());
	}

	public int updateTopStatus(int newstatus, int oldstatus) {
		return (int) updateByWhere("top=?", "top=?", newstatus, oldstatus);
	}
}
