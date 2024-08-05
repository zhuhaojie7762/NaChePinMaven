package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SkuService;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class GoodAuditController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private CustomerService customerService;

    public void index() {
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String goodsCode = "";
        String name = null;
        String itemNo = null;
        Boolean isSpecial = getParaToBoolean("isSpecial");
        int sellerId = 0;
        if (StrKit.notBlank(fieldName, value)) {
            if ("code".equals(fieldName)) {
                goodsCode = value;
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
                .findGoods(goodsCode, name, itemNo, isSpecial, sellerId, GoodsStatus.wait_audit, goodsType, getPageParam());
        setAttr("pager", pager);
        render("audit_list.html");
    }

    public void toAudit() {
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

    @ActionKey(method = RequestMethod.POST)
    public void audit() {
        int id = getParaToInt("id", 0);
        Boolean auditResult = getParaToBoolean("result");
        if (auditResult == null) {
            error("请选择审核意见");
            return;
        }
        String reason = getPara("reason");
        if (!auditResult && StrKit.isBlank(reason)) {
            error("请填写审核备注");
            return;
        }

        Goods good = goodsService.getById(id);
        int weight = customerService.getWeight(good.getSellerId());
        good.setWeighting(weight);
        goodsService.update(good);

        goodsService.audit(id, auditResult, reason, getCurUserId());
        ok("操作成功");
    }
}
