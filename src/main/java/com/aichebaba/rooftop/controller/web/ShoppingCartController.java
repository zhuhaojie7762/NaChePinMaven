package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.interceptor.web.WebLoginInterceptor;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.ShoppingCart;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.service.FollowService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.ShoppingCartService;
import com.aichebaba.rooftop.service.SkuService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("prototype")
@Before(WebLoginInterceptor.class)
public class ShoppingCartController extends BaseController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private FollowService followService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

    /**
     * 购物车
     */
    @ActionKey("/shoppingCart.html")
    public void shoppingCart() {
        if (curCustomerId() == 0) {
            setAttr("shoppingCarts", null);
        } else {
            List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCarts(curCustomerId());
            followService.setFollowInfo2(shoppingCarts, curCustomerId());
            setAttr("shoppingCarts", shoppingCarts);
        }
        render("shoppingCart.html");
    }

    public void add() {
        if (curCustomerId() == 0) {
            error("请先登录!");
            return;
        }
        String color = getPara("color", "");
        String size = getPara("size", "");
        int skuId = getParaToInt("skuId", 0);
        int specPropValueId = getParaToInt("specPropValueId", 0);
        String specPropValue = getPara("specPropValue");
        int quantity = getParaToInt("quantity", 1);
        String goodsCode = getPara("goodsCode", "");
        Goods goods = goodsService.getByCode(goodsCode);
        if (goods == null) {
            error("商品编号有误");
            return;
        }
        if (specPropValueId <= 0) {
            error("请选择规格");
            return;
        }
        if (goods == null || !goods.getStatus().equals(GoodsStatus.online)) {
            error("商品已下架");
            return;
        }
        ShoppingCart shoppingCart = shoppingCartService.getCartByGoods(goodsCode, curCustomerId(), skuId, color, size);
        if (shoppingCart == null) {
            shoppingCart = shoppingCartService.add(goodsCode, curCustomerId(), skuId, specPropValue, color, size);
        }
        quantity += shoppingCart.getQuantity();
        shoppingCart.setQuantity(quantity);
        shoppingCartService.update(shoppingCart);
        long data = shoppingCartService.getShoppingCartSize(curCustomerId());
        getCurCustomer().setShoppingCartSize(data);
        ok("添加购物车成功", data);
    }

    /**
     * 删除
     */
    public void del() {
        int cartId = getParaToInt("id");
        shoppingCartService.deleteById(cartId);
        long data = shoppingCartService.getShoppingCartSize(curCustomerId());
        getCurCustomer().setShoppingCartSize(data);
        ok("购物车商品删除成功");
    }

    /**
     * 批量删除
     */
    public void delAll() {
        String code = getPara("ids");
        if(StrKit.isBlank(code)){
            error("请先选择商品");
        }

        String[] ids = code.split(",");
        shoppingCartService.deleteByIds(ids);
        long data = shoppingCartService.getShoppingCartSize(curCustomerId());
        getCurCustomer().setShoppingCartSize(data);
        ok("购物车商品删除成功");
    }

    public void chgNum() {
        int cartId = getParaToInt("id");
        int num = getParaToInt("num", 0);
        ShoppingCart cart = shoppingCartService.getCartById(cartId);
        if (cart != null) {
            Sku sku = skuService.getSkuById(cart.getSkuId());
            if (sku.getAvailableStock() < num) {
                error("库存不足", sku.getAvailableStock());
                return;
            } else {
                cart.setQuantity(num);
                shoppingCartService.update(cart);
            }
            ok("");
        } else {
            ok("");
        }
    }
}
