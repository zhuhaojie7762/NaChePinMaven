package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.ShoppingCart;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ShoppingCartDao extends Dao<ShoppingCart, Integer> {
    public ShoppingCartDao() {
        super("shopping_cart", ShoppingCart.class);
    }

    public List<ShoppingCart> getShoppingCarts(Collection<Integer> ids) {
        if (ids.isEmpty())
            return Collections.emptyList();
        return findSQL(
                "select s.*, g.wholesalePrice price, g.isSpecial, g.status goodsStatus from shopping_cart s "
                        + " join goods g on s.goodsCode = g.code " + " where "
                        + inSql("s.id", ids.size()), ids.toArray());
    }

    public List<ShoppingCart> getShoppingCarts(int customerId) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select s.*, sk.price, g.isSpecial, g.status goodsStatus, g.brand, g.sellerId, g.headImgUrl1 headImgUrl, c.name sellerName, c.supplierCompany ")
                .append(" from shopping_cart s ")
                .append(" join goods g on s.goodsCode = g.code ")
                .append(" join sku sk on sk.id = s.skuId ")
                .append(" left outer join customer c on g.sellerId = c.id ")
                .append(" where 1 = 1");
        if (customerId > 0) {
            sql.append(" and s.customerId = ? ");
            paras.add(customerId);
        }
        sql.append(" order by g.brand, g.sellerId, s.created desc ");
        return findSQL(sql.toString(), paras.toArray());
    }

    public long getShoppingCartSize(int customerId) {
        return getSingle("select count(1) from shopping_cart where customerId=?", customerId);
    }

    public ShoppingCart getCartByGoodsCode(String goodsCode, int customerId, int skuId, String color, String size) {
        return getByWhere("goodsCode = ? and customerId = ? and skuId=? and color = ? and size = ? ", goodsCode,
                customerId, skuId, color,
                size);
    }

    public void moveItems(Collection<Integer> ids) {
        deleteByWhere(inSql("id", ids.size()), ids.toArray());
    }
}
