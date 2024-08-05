package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.SkuDao;
import com.aichebaba.rooftop.dao.SkuPropDao;
import com.aichebaba.rooftop.dao.SkuPropValueDao;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.SkuProp;
import com.aichebaba.rooftop.model.SkuPropValue;
import com.google.common.base.Splitter;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuService {

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private SkuPropDao skuPropDao;

    @Autowired
    private SkuPropValueDao skuPropValueDao;

    public List<Sku> getSkuByGoodsCode(String goodsCode) {
        return skuDao.getSkuByGoodsCode(goodsCode);
    }

    public List<SkuProp> getSkuProp() {
        return skuPropDao.findAll();
    }

    public List<SkuPropValue> getSkuPropValuesByGoodsCode(String goodsCode) {
        return skuPropValueDao.getPropValuesByGoodsCode(goodsCode);
    }

    public List<SkuPropValue> getPropValuesByGoodsCodeAndPropId(String goodsCode, int skuPropId){
        return skuPropValueDao.getPropValuesByGoodsCodeAndPropId(goodsCode, skuPropId);
    }

    public Sku getSkuById(int skuId) {
        return skuDao.getById(skuId);
    }

    public void delSkuById(Integer... skuIds){
        skuDao.delByIds(skuIds);
    }

    public SkuPropValue getSkuSpecValueById(int specValueId) {
        return skuPropValueDao.getById(specValueId);
    }

    public String getSkuPropValues(String goodsCode, String properties) {
        Iterable<String> it = Splitter.on(";").split(properties);
        StringBuilder sb = new StringBuilder();
        for (String pv : it) {
            if (StrKit.notBlank(pv)) {
                String[] pp = pv.split(":");
                int propId = Integer.parseInt(pp[0]);
                int propValueId = Integer.parseInt(pp[1]);
                SkuProp prop = skuPropDao.getById(propId);
                SkuPropValue value = skuPropValueDao.getPropValueByGoodsCodeAndPropId(propValueId, goodsCode, propId);
                sb.append(prop.getName()).append(':').append(value.getValue()).append(';');
            }
        }
        return sb.toString();
    }

    public SkuPropValue getSkuPropValueByValue(String goodsCode, int skuPropId, String value) {
        return skuPropValueDao.getSkuPropValueByValue(goodsCode, skuPropId, value);
    }

    public boolean lockStock(int skuId, int quantity) {
        Sku sku = skuDao.getSkuForLock(skuId);
        if (sku.getAvailableStock() < quantity) {
            return false;
        } else {
            sku.setLockStock(sku.getLockStock() + quantity);
            skuDao.update(sku);
            return true;
        }
    }

    public SkuPropValue saveSkuPropValue(SkuPropValue skuPropValue) {
        if (skuPropValue.getId() > 0) {
            skuPropValueDao.update(skuPropValue);
        } else {
            Object o = skuPropValueDao.add(skuPropValue);
            skuPropValue.setId(Integer.parseInt(o.toString()));
        }
        return skuPropValue;
    }

    public Sku saveSku(Sku sku) {
        if (sku.getId() > 0) {
            skuDao.update(sku);
        } else {
            Object o = skuDao.add(sku);
            sku.setId(Integer.parseInt(o.toString()));
        }
        return sku;
    }

    public void minusStock(int skuId, int quantity) {
        Sku sku = skuDao.getSkuForLock(skuId);
        sku.setLockStock(sku.getLockStock() - quantity);
        sku.setStock(sku.getStock() - quantity);
        skuDao.update(sku);
    }

    public void changeStock(int id, int num) {
        skuDao.changeStock(id, num);
    }
}
