package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.SkuPropValue;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SkuService;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class GoodOnlineController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

    public void index() {
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String code = null;
        String name = null;
        String itemNo = null;
        Boolean isSpecial = getParaToBoolean("isSpecial");
        int sellerId = 0;
        if (StrKit.notBlank(fieldName, value)) {
            if ("code".equals(fieldName)) {
                code = value;
            } else if ("name".equals(fieldName)) {
                name = value;
            } else if ("itemNo".equals(fieldName)) {
                itemNo = value;
            } else if ("sellerId".equals(fieldName)) {
                sellerId = Integer.parseInt(value);
            }
        }
        String type = getPara("type");
        GoodsType goodsType = null;
        if(StrKit.notBlank(type)) {
            goodsType = GoodsType.valOf(type);
        }
        PageList<Goods> pager = goodsService
                .findGoods(code, name, itemNo, isSpecial, sellerId, GoodsStatus.online, goodsType, getPageParam());
        setAttr("pager", pager);
        render("online.html");
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        Goods goods = goodsService.getById(id);
        List<Sku> skus = skuService.getSkuByGoodsCode(goods.getCode());
        for (Sku sku : skus) {
            sku.setSkuSpecValue(skuService.getSkuPropValues(goods.getCode(), sku.getProperties()));
        }
        setAttr("skus", skus);
        setAttr("goods", goods);

        Customer sellerCustomer = customerService.getById(goods.getSellerId());
        setAttr("sellerCustomer", sellerCustomer);
    }

    public void offline() {
        int id = getParaToInt("id", 0);
        String reason = getPara("reason");
        if (StrKit.isBlank(reason)) {
            error("请填写下架原因");
            return;
        }
        goodsService.offline(id, reason, getCurUserId());
        ok("下架成功");
    }
}
