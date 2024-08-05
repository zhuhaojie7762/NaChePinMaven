package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.SalesVolumeDao;
import com.aichebaba.rooftop.model.SalesVolume;
import com.jfinal.aop.Tx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesVolumeService {
    @Autowired
    private SalesVolumeDao salesVolumeDao;

    /**
     * 增加销量
     * @param goodsCode 商品编号
     * @param num 增加的销量数
     */
    @Tx
    public void addSales(String goodsCode, int num) {
        SalesVolume salesVolume = salesVolumeDao.getByGooodsCode(goodsCode);
        if (salesVolume == null) {
            salesVolume = new SalesVolume();
            salesVolume.setGoodsCode(goodsCode);
            salesVolume.setThisWeek(num);
            salesVolume.setThisMonth(num);
            salesVolume.setTotalCount(num);
            salesVolumeDao.add(salesVolume);
        } else {
            salesVolume.setThisWeek(salesVolume.getThisWeek() + num);
            salesVolume.setThisMonth(salesVolume.getThisMonth() + num);
            salesVolume.setTotalCount(salesVolume.getTotalCount() + num);
            salesVolumeDao.update(salesVolume);
        }
    }

    /**
     * 每周清空销量
     */
    @Tx
    public void cleanWeekSales(){
        List<SalesVolume> salesVolumeList = salesVolumeDao.findAll();
        for(SalesVolume salesVolume : salesVolumeList){
            salesVolume.setLastWeek(salesVolume.getThisWeek());
            salesVolume.setThisWeek(0);
            salesVolumeDao.update(salesVolume);
        }
    }

    /**
     * 每月清空销量
     */
    @Tx
    public void cleanMonthSales(){
        List<SalesVolume> salesVolumeList = salesVolumeDao.findAll();
        for(SalesVolume salesVolume : salesVolumeList){
            salesVolume.setLastMonth(salesVolume.getThisMonth());
            salesVolume.setThisMonth(0);
            salesVolumeDao.update(salesVolume);
        }
    }

    /**
     * 获取商品的销量情况
     * @param goodsCode     商品编号
     * @return
     */
    public SalesVolume getByGoodsCode(String goodsCode){
        return salesVolumeDao.getByWhere("goodsCode = ?", goodsCode);
    }
}
