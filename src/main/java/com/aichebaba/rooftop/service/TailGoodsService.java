package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.TailGoodsDao;
import com.aichebaba.rooftop.model.TailGoods;
import com.aichebaba.rooftop.model.enums.TailGoodsStatus;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TailGoodsService {
    @Autowired
    private TailGoodsDao tailGoodsDao;

    /**
     * 分页查询尾货处理
     * @param status  状态
     * @param pageParam 分页设置
     * @return
     */
    public PageList<TailGoods> findTailGoods(TailGoodsStatus status, int sellerId, PageParam pageParam){
        return tailGoodsDao.findTailGoods(status, sellerId, pageParam);
    }

    public PageList<TailGoods> findTails(String code, String goodsName, PageParam pageParam){
        return tailGoodsDao.findTails(code, goodsName, pageParam);
    }

    public TailGoods findTailGoodsByCode(String code){
        return tailGoodsDao.getByPK(code);
    }

    /**
     * 修改
     * @param tailGoods
     * @return
     */
    public void update(TailGoods tailGoods){
        tailGoodsDao.update(tailGoods);
    }

    /**
     * 添加
     * @param tailGoods
     * @return
     */
    public TailGoods add(TailGoods tailGoods){
        tailGoods.setCode(tailGoodsDao.getNextCode());
        tailGoods.setCreated(new Date());
        tailGoodsDao.add(tailGoods);
        return tailGoods;
    }

    /**
     * 处理完结
     * @param code
     */
    public void end(String code){
        TailGoods tailGoods = tailGoodsDao.getByPK(code);
        tailGoods.setStatus(TailGoodsStatus.offline);
        tailGoods.setClosed(new Date());
        tailGoodsDao.update(tailGoods);
    }

    public void delete(String code){
        tailGoodsDao.deleteByWhere("code = ?", code);
    }

    public TailGoods getTailGoods(String code){
        return tailGoodsDao.getTailGoods(code);
    }
}
