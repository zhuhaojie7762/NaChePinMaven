package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.newversion.goods.GoodsAssist;
import com.aichebaba.rooftop.vo.SearchParams;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@Scope("prototype")
public class GoodsController extends BaseController {

    @ActionKey("list.html")
    public void list(){
        //查询商品
        PageList<Goods> pager = goodsService.findGoods(null, GoodsStatus.online, getSearchParams());
        followService.setFollowInfo(pager.getData(), curCustomerId());
        setAttr("pager", pager);
        //现在Url
        setAttr("url", getRequest().getRequestURI());
        //在线商品数量
        Long count = goodsService.getGoodsCount(GoodsStatus.online);
        setAttr("goodsCount", count);

        render("list.html");
    }

    private SearchParams getSearchParams()
    {
        return getAttr("searchParams");
    }

    @ActionKey("{code}.html")
    public void details() {
        String code = getUrlPara("code", "");
        Goods goods = goodsService.getByCode(code);
        //下架或删除商品显示为404   2016-09-29
        if(goods.getStatus().equals(GoodsStatus.offline) || goods.getStatus().equals(GoodsStatus.deleted)){
            redirect("/loseGoods.html");
            return;
        }
        setAttr("goods", goods);
        SalesVolume salesVolume = salesVolumeService.getByGoodsCode(code);
        setAttr("salesVolume", salesVolume);
        List<SkuProp> props = skuService.getSkuProp();
        List<SkuPropValue> propValues = skuService.getSkuPropValuesByGoodsCode(code);
        Multimap<Integer, SkuPropValue> propIdValues = Multimaps.index(propValues, SkuPropValue.propIdValue);
        for (SkuProp prop : props) {
            prop.setPropValues(propIdValues.get(prop.getId()));
            setAttr(prop.getNameEn(), prop);
        }
        List<Sku> skus = skuService.getSkuByGoodsCode(code);
        Map<String, Sku> skuMap = Maps.uniqueIndex(skus, Sku.PROPS_VALUE);
        setAttr("skus", skuMap);
        if (getCurCustomer() != null) {
            Follow follow = followService.findFollow(curCustomerId(), code);
            if (follow == null) {
                setAttr("followed", false);
            } else {
                setAttr("followed", true);
            }
        } else {
            setAttr("followed", false);
        }

        /********************************************** 新版分割线 ****************************************************/
        // 规格参数
        List<GoodsExt> goodsExts = goodsExtService.getGoodsValue(goods.getId());
        List<String> property = new ArrayList<>();
        for (GoodsExt goodsExt : goodsExts) {
            GoodsClass value = goodClassService.getById(goodsExt.getClassid());
            GoodsClass key = goodClassService.getById(value.getPid());
            property.add(key.getName() + "：" + value.getName());
        }
        setAttr("property", property);

        // 全部分类
        List<GoodsClass> list = goodClassService.getLevel(2);
        setAttr("goodsClassList", list);

        AmosQuerier querier;
        // 热销排行
        querier = AmosDB.newQuerier().equals("g.thirdclassid", goods.getThirdclassid()).equals("g.`status`", goods.getStatus().getVal()).orders("totalCount", AmosQuerier.ORDER_DESC).orders("onlineTime", AmosQuerier.ORDER_DESC).limit(10);
        List<Goods> hotGoods = goodsService.findByGoodsLeftSales(querier);
        setAttr("thirdClassGoods", hotGoods);

        // 交易记录
        List<Order> orders = orderService.getTradingRecord(code);
        setAttr("orderSize", orders.size());
        for (Order order : orders) {
            String userName = customerService.getById(order.getBuyerId()).getUsername();
            order.setBuyerUsername(GoodsAssist.getEncryptAccount(userName));
        }
        setAttr("orders", GoodsAssist.getShowSize(10, orders));

        // 相似推荐
        querier = AmosDB.newQuerier().equals("`status`", goods.getStatus().getVal()).equals("thirdclassid", goods.getThirdclassid()).limit(6);
        List<Goods> similar = goodsService.findByGoods(querier);
        setAttr("similar", similar);

        render("details.html");
    }

    /**
     * 适配车型
     */
    @ActionKey("shipeichexing.html")
    public void shipeichexing() {
        render("shipeichexing.html");
    }

    /**
     * 关注
     */
    public void follow() {
        int customerId = curCustomerId();
        if (customerId == 0) {
            error("请先登录!");
            return;
        }
        String goodsCode = getPara("goodsCode", "");
        Follow follow = followService.findFollow(customerId, goodsCode);
        if (follow != null) {
            error("已关注");
            return;
        }

        int followCnt = followService.addFollow(customerId, goodsCode);
        ok("成功", followCnt);
    }

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private FollowService followService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private SalesVolumeService salesVolumeService;
    @Autowired
    private GoodClassService goodClassService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsExtService goodsExtService;
}
