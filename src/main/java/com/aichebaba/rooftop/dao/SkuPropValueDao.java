package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.SkuPropValue;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SkuPropValueDao extends Dao<SkuPropValue, Integer> {

    public SkuPropValueDao() {
        super("sku_prop_value", SkuPropValue.class);
    }

    public List<SkuPropValue> getPropValuesByGoodsCode(String goodsCode) {
        return findByWhere("goodsCode = ? order by skuPropId asc", goodsCode);
    }

    public List<SkuPropValue> getPropValuesByGoodsCodeAndPropId(String goodsCode, int propId) {
        return findByWhere("goodsCode = ? and skuPropId = ? order by id", goodsCode, propId);
    }

    public SkuPropValue getPropValueByGoodsCodeAndPropId(int id, String goodsCode, int propId) {
        return getByWhere("id=? and skuPropId=? and goodsCode = ?", id, propId, goodsCode);
    }

    public SkuPropValue getSkuPropValueByValue(String goodsCode, int skuPropId, String value) {
        return getByWhere("goodsCode=? and skuPropId=? and value=?", goodsCode, skuPropId, value);
    }
}
