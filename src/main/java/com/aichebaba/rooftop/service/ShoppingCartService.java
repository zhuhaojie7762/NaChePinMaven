package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.GoodsDao;
import com.aichebaba.rooftop.dao.ShoppingCartDao;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private GoodsDao goodsDao;

    public List<ShoppingCart> getShoppingCarts(int customerId) {
        return shoppingCartDao.getShoppingCarts(customerId);
    }

    public List<ShoppingCart> getShoppingCarts(Collection<Integer> ids) {
        return shoppingCartDao.getShoppingCarts(ids);
    }

    public ShoppingCart getCartById(int cartId) {
        return shoppingCartDao.getById(cartId);
    }

    /**
     * 根据商品编号和客户ID获取购物车信息
     *
     * @param goodsCode  商品编号
     * @param customerId 客户ID
     * @return
     */
    public ShoppingCart getCartByGoods(String goodsCode, int customerId, int skuId, String color, String size) {
        return shoppingCartDao.getCartByGoodsCode(goodsCode, customerId, skuId, color, size);
    }

    /**
     * 添加购物车商品
     *
     * @param goodsCode
     * @param customerId
     * @param color
     * @param size
     * @return
     */
    public ShoppingCart add(String goodsCode, int customerId, int skuId, String specPropValue,
                            String color, String size) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setCreated(new Date());
        shoppingCart.setCustomerId(customerId);
        shoppingCart.setSkuId(skuId);
        shoppingCart.setGoodsCode(goodsCode);
        shoppingCart.setSpecPropValue(specPropValue);
        shoppingCart.setColor(color);
        shoppingCart.setSize(size);
        Goods goods = goodsDao.getByCode(goodsCode);
        shoppingCart.setGoodsName(goods.getName());
        shoppingCart.setGoodsItemNo(goods.getItemNo());
        shoppingCart.setQuantity(0);
        shoppingCart.setCreated(new Date());
        Object o = shoppingCartDao.add(shoppingCart);
        shoppingCart.setId(Integer.parseInt(o.toString()));
        return shoppingCart;
    }

    /**
     * 修改购物车信息
     *
     * @param shoppingCart
     */
    public void update(ShoppingCart shoppingCart) {
        shoppingCartDao.update(shoppingCart);
    }

    public long getShoppingCartSize(int customerId) {
        return shoppingCartDao.getShoppingCartSize(customerId);
    }

    public void moveItems(Collection<Integer> ids) {
        shoppingCartDao.moveItems(ids);
    }

    public void deleteById(int shoppingCartId) {
        shoppingCartDao.delByIds(shoppingCartId);
    }
    public void deleteByIds(String... shoppingCartIds) {
        shoppingCartDao.delByIds(shoppingCartIds);
    }
}
