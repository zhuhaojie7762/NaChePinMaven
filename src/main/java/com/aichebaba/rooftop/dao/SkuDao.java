package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Sku;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkuDao extends Dao<Sku, Integer> {

    private Logger logger = LoggerFactory.getLogger(GoodsDao.class);

    public SkuDao() {
        super("sku", Sku.class);
    }

    public List<Sku> getSkuByGoodsCode(String goodsCode) {
        return findByWhere("goodsCode = ? order by id ", goodsCode);
    }

    public Sku getGoodsSkuByProperties(String code, String properties) {
        return getByWhere("goodsCode = ? and properties=?", code, properties);
    }

    public Sku getSkuForLock(int id) {
        return getByWhere("id = ? for update", id);
    }

    public void changeStock(int id, int num){
        Sku sku = getSkuForLock(id);
        if(sku != null){
            logger.info("sku[{}] stock change src: {}, num: {}, new:{}", sku.getId(), sku.getStock(), num,
                    sku.getLockStock() + num);
            sku.setStock(num+sku.getLockStock());
            if(sku.getStock() < 0){
                sku.setStock(0);
            }
            update(sku);
        }
    }
}
