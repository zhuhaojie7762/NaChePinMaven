package com.aichebaba.rooftop.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aichebaba.rooftop.dao.FollowDao;
import com.aichebaba.rooftop.dao.GoodsDao;
import com.aichebaba.rooftop.dao.SeekDao;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsSubmitType;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.aichebaba.rooftop.vo.SearchParams;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;

@Service
public class GoodsService {

    private static Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private FollowDao followDao;

    @Autowired
    private SeekDao seekDao;

    @Autowired
    private SalesVolumeService salesVolumeService;

	public Goods findbyId(String id) {
		return goodsDao.findGoodsById(id);
	}

	public Goods findbyCode(String code) {
		return goodsDao.findGoodsByCode(code);
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
        return goodsDao.findGoods(type, status, goodsName, brand, code, itemNo, sellerId, startOffline, endOffline, startStock,
                endStock, exceptGoodsName, pageParam);
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
     * @param pageParam    分页设置
     * @return
     */
    public PageList<Goods> findGoods(GoodsType type, GoodsStatus status, String goodsName, String brand, String code,
                                     String itemNo, int sellerId, Date startOffline, Date endOffline, int startStock, int endStock,
                                     PageParam pageParam) {
        return goodsDao.findGoods(type, status, goodsName, brand, code, itemNo, sellerId, startOffline, endOffline, startStock,
                endStock, null, pageParam);
    }

	/**
	 * 分页查询商品
	 * 
	 * @param firstclassid
	 *            一级分类
	 * @param secondclassid
	 *            二级分类
	 * @param thirdclassid
	 *            三级分类
	 * @param brandid
	 *            品牌分类
	 * @param minprice
	 *            单价范围
	 * @param maxprice
	 *            单价范围
	 * @param minsellcnt
	 *            销量范围
	 * @param maxsellcnt
	 *            销量范围
	 * @param itemNo
	 *            商品货号
	 * @param name
	 *            商品名称
	 * @param pageParam
	 *            分页设置
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
		return goodsDao.findGoods(firstclassid, 
								  secondclassid, 
								  thirdclassid, 
								  fourthclassid,
								  fifthclassid,
								  brandid, 
								  minprice, 
								  maxprice, 
								  minsellcnt, 
								  maxsellcnt,
								  itemNo, 
								  name, 
								  status, 
								  customerId,
								  customerName, 
								  submittype, 
								  top,
								  orderbytop,
								  pageParam);
	}

    public PageList<Goods> findGoods(GoodsType type, GoodsStatus status, SearchParams searchParams) {
        PageParam pageParam = searchParams.getPageParam();
        if (!searchParams.getOrders().isEmpty()) {
            pageParam.getSortMap().clear();
        }
        for (Map.Entry<String, String> entry : searchParams.getOrders().entrySet()) {
            pageParam.putSort(entry.getKey(), entry.getValue());
        }
        return goodsDao.findGoods(type, status, searchParams);
    }

    public PageList<Goods> findGoods(String code, String name, String itemNo, Boolean isSpecial, int sellerId,
            GoodsStatus status, GoodsType type, PageParam pageParam) {
        return goodsDao.findGoods(code, name, itemNo, isSpecial, sellerId, status, type, pageParam);
    }

    /**
     * 分页查询商品(分类列表页用)
     * @param status        商品状态
     * @param searchParams  搜索条件
     * @return
     */
    public PageList<Goods> findClassGoods(GoodsStatus status, SearchParams searchParams){
        PageParam pageParam = searchParams.getPageParam();
        if (!searchParams.getOrders().isEmpty()) {
            pageParam.getSortMap().clear();
        }
        for (Map.Entry<String, String> entry : searchParams.getOrders().entrySet()) {
            pageParam.putSort(entry.getKey(), entry.getValue());
        }
        return goodsDao.findClassGoods(status, searchParams);
    }

    /**
     * 查询热销商品
     * @param type  商品类型
     * @param status    商品状态
     * @param exceptGoodsName  含有该名称的除外(不显示专拍的商品)
     * @param pageParam
     * @return
     */
    public PageList<Goods> findHotGoods(GoodsType type, GoodsStatus status, String exceptGoodsName, PageParam pageParam){
        return goodsDao.findHotGoods(type, status, exceptGoodsName, pageParam);
    }

    /**
     * 分页查询商品
     * @param type      商品类型
     * @param status    商品状态
     * @param brand     商品品牌
     * @param exceptGoodsName  含有该名称的除外(不显示专拍的商品)
     * @param pageParam
     * @return
     */
    public PageList<Goods> findBrandGoods(GoodsType type, GoodsStatus status, String brand, String exceptGoodsName, PageParam pageParam) {
        return goodsDao.findBrandGoods(type, status, brand, exceptGoodsName, pageParam);
    }

    public Goods getById(int id) {
        return goodsDao.getById(id);
    }

    public Goods getByCode(String code) {
        return goodsDao.getByCode(code);
    }

    public Map<String, Goods> getGoodsByCodes(Collection<String> codes) {
        if (codes.isEmpty())
            return new HashMap<>();
        return Maps.uniqueIndex(goodsDao.getGoodsByCodes(codes), Goods.CODE_VALUE);
    }

    public void offline(int id, String reason, int curUserId) {
        Goods goods = goodsDao.getById(id);
        if (goods != null) {
            goods.setStatus(GoodsStatus.offline);
            goodsDao.update(goods);
            logger.info("goods offline {}: reason:{}, user: {}", id, reason, curUserId);
        }
    }

	public void applyOnline(int id, int curUserId, GoodsSubmitType submittype) {
        Goods goods = goodsDao.getById(id);
        if (goods != null) {
            goods.setStatus(GoodsStatus.wait_audit);
			goods.setSubmittype(submittype);
            goodsDao.update(goods);
            logger.info("goods apply online {}: user: {}", id, curUserId);
        }
    }

	public void applyOnline(int id, int curUserId) {
		Goods goods = goodsDao.getById(id);
		if (goods != null) {
			goods.setStatus(GoodsStatus.wait_audit);
			goods.setSubmittype(GoodsSubmitType.admin);
			goodsDao.update(goods);
			logger.info("goods apply online {}: user: {}", id, curUserId);
		}
	}

    public void delete(String code) {
        Goods goods = goodsDao.getByCode(code);
        if (goods != null) {
            goods.setStatus(GoodsStatus.deleted);
            goods.setDeleted(new Date());
            goodsDao.update(goods);
        }
    }

    public void audit(int id, boolean result, String checkRemark, int curUserId) {
        Goods goods = goodsDao.getById(id);
        if (goods != null) {
            goods.setStatus(result ? GoodsStatus.online : GoodsStatus.back_audit);
            goods.setCheckRemark(checkRemark);
            goods.setCheckerId(curUserId);
            goods.setCheckTime(new Date());
            if (result) {
                goods.setOnlineTime(new Date());
            }
            goodsDao.update(goods);
            logger.info("goods {} audit result: {}, checkRemark: {}, checkerId: {}", id, result, checkRemark,
                    curUserId);
        }
    }

    /**
     * 修改商品关注数
     *
     * @param code
     * @param followCnt
     */
    public void updateFollowCnt(String code, int followCnt) {
        Goods goods = goodsDao.getByCode(code);
        goods.setFollowCnt(followCnt);
        goodsDao.update(goods);
    }

    /**
     * 用户关注商品
     */
    public PageList<Goods> getFollow(int customerId, PageParam pageParam) {
        return goodsDao.attention(customerId, pageParam);
    }

    /**
     * 添加商品
     *
     * @return
     */
    public Goods add(Goods goods) {
        goods.setCode(goodsDao.getNextCode());
        goods.setCreated(new Date());
		if (goods.getStatus() == null) {
			goods.setStatus(GoodsStatus.wait_audit);
		}
        Object o = goodsDao.add(goods);
        goods.setId(Integer.parseInt(o.toString()));
        return goods;
    }

    /**
     * 修改商品信息
     *
     * @param goods
     */
    public void update(Goods goods) {
        goodsDao.update(goods);
    }

    /**
     * 商品下架
     * @param code          下架商品编号
     * @param offlineRemark 下架理由
     */
    public void offShelf(String code, String offlineRemark) {
        Goods goods = goodsDao.getByCode(code);
        goods.setStatus(GoodsStatus.offline);
        goods.setOfflineTime(new Date());
        goods.setOfflineRemark(offlineRemark);
        update(goods);
    }

    /**
     * 添加库存
     * @param code  商品编号
     * @param num   添加数量
     */
    public void addStock(String code, int num) {
        goodsDao.addStock(code, num);
    }

    /**
     * 付款减库存
     * @param code
     * @param num
     */
    public void minusStock(String code, int num) {
        goodsDao.minusStock(code, num);
        salesVolumeService.addSales(code, num);
    }

    /**
     * 获取小批量定制列表
     */
    public PageList<Seek> findSeeks(SeekStatus status, int customerId, PageParam pageParam){
       return  seekDao.findSeeks(status, customerId, pageParam);
    }

    public void changeStock(String code, int sum){
        goodsDao.changeStock(code, sum);
    }

    public void updateGoodsByWeight(int sellerId, int weight){
         goodsDao.updateGoodsByWeight(sellerId, weight);
    }

    /**
     * 获取品牌
     * @param type      商品类型
     * @param status    商品状态
     * @return
     */
    public List<String> findBrands(GoodsType type, GoodsStatus status) {
        return goodsDao.findBrandOrderByCount(type, status);
    }

    /**
     * 获取商品数量
     * @param status    商品状态
     * @return
     */
    public Long getGoodsCount(GoodsStatus status){
        return goodsDao.getGoodsCount(status);
    }

    /******************************************新版本商品分类管理******************************************************/
    public List<Goods> findByHomeGoods(AmosQuerier querier) {
        return goodsDao.findByHomeGoods(querier);
    }

    public List<Goods> findByGoods(AmosQuerier querier) {
        return goodsDao.findByGoods(querier);
    }

    public List<Goods> findByGoodsLeftSales(AmosQuerier querier) {
        return goodsDao.findByGoodsLeftSales(querier);
    }

    public PageList<Goods> findByGoodsLeftSalesPage(AmosQuerier querier, PageParam pageParam) {
        return goodsDao.findByGoodsLeftSalesPage(querier, pageParam);
    }

	public int updateStatusByIds(Integer[] ids, GoodsStatus status) {
		return goodsDao.updateStatusByIds(ids, status);
	}

	public int updateStatusById(Integer id, GoodsStatus status) {
		return goodsDao.updateStatusById(id, status);
	}

    /**
     * 修改指定品牌的名称
     * @param brandId       需修改的品牌ID
     * @param brandName     修改后的品牌名
     */
    public void updateBrandName(int brandId, String brandName){
        goodsDao.updateBrandName(brandId, brandName);
    }

	public int updateTopStatusById(Integer id, int top) {
		return goodsDao.updateTopStatusById(id, top);
	}

	public int updateTopStatus(int newstatus, int oldstatus) {
		return goodsDao.updateTopStatus(newstatus, oldstatus);
	}
}
