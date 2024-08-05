package com.aichebaba.rooftop.controller.web.brand;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.BrandClass;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.vo.Json;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class BrandController extends BaseController {
    @ActionKey("brand.html")
    public void index(){
        Brand brand = new Brand();
        brand.setPinyin(getPara("flag"));
        if (brand.getPinyin() == null) {
            PageList<Brand> brands = brandService.findAll(brand, new PageParam(18, 1));
            setAttr("brands", brands);
            render("brand.html");
        } else {
            renderJson(Json.success("", brandService.findAll(brand, new PageParam(99999, 1))));
        }
    }

    public void brandLoad() {
        renderJson(Json.success("", brandService.findAll(new Brand(), new PageParam(6, getParaToInt("pageFlag")))));
    }

    @ActionKey("brandDetail.html")
    public void goBrandMsg() {
        Integer brandId = getParaToInt("brandId");
        if(brandId == null){
            render("../page404.html");
            return;
        }

        Brand brand = brandService.getById(brandId);
        setAttr("brand", brand);

        Goods goods = new Goods();
        goods.setStatus(GoodsStatus.online);

        AmosQuerier querier;
        // 品牌分类
        querier = AmosDB.newQuerier().equals("brandId", brandId);
        List<BrandClass> brandClass = brandService.findByBrandClass(querier);
        List<Map<String, String>> brandClazz = new ArrayList<>();
        for (BrandClass clazz : brandClass) {
            Map<String, String> map = new HashMap<>();

            GoodsClass goodsClass = goodClassService.getById(clazz.getClassId());
            map.put("classId", goodsClass.getId() + "");

            querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("thirdclassid", goodsClass.getId()).equals("brandId", brandId);
            map.put("classContent", goodsClass.getName() + "（" + goodsService.findByGoods(querier).size() + "）");

            brandClazz.add(map);
        }
        setAttr("brandClazz", brandClazz);

        // 新品上市
        querier = AmosDB.newQuerier().equals("`status`", goods.getStatus().getVal()).equals("brandid", brand.getId()).orders("onlineTime", AmosQuerier.ORDER_DESC).limit(2);
        List<Goods> newGoods = goodsService.findByGoods(querier);
        setAttr("newGoods", newGoods);

        // 热门推荐
        querier = AmosDB.newQuerier().equals("g.`status`", goods.getStatus().getVal()).equals("brandid", brand.getId()).orders("totalCount", AmosQuerier.ORDER_DESC).orders("onlineTime", AmosQuerier.ORDER_DESC).limit(2);
        List<Goods> hotGoods = goodsService.findByGoodsLeftSales(querier);
        setAttr("hotGoods", hotGoods);

        // S 全部产品
        String field = getPara("field");
        String sort = getPara("sort");
        PageList<Goods> pageGoods;
        if (StrKit.isBlank(field) || StrKit.isBlank(sort)) {
            querier = AmosDB.newQuerier().equals("g.`status`", goods.getStatus().getVal()).equals("brandid", brand.getId()).orders("onlineTime", AmosQuerier.ORDER_DESC);
            pageGoods = goodsService.findByGoodsLeftSalesPage(querier, getPageParam());
        } else {
            querier = AmosDB.newQuerier().equals("g.`status`", goods.getStatus().getVal()).equals("brandid", brand.getId()).orders(field, sort);
            pageGoods = goodsService.findByGoodsLeftSalesPage(querier, getPageParam());
        }
        setAttr("field", field);
        setAttr("sort", sort);
        setAttr("pageGoods", pageGoods);
        // E 全部产品

        render("brandDetail.html");
    }

    public PageParam getPageParam() {
        PageParam pageParam = new PageParam(16, getPageNo());
        pageParam.setSort(getPara("sort"));
        setAttr("sorts", pageParam.getSortMap());
        setAttr("sortValue", pageParam.getSortValue());
        return pageParam;
    }

    @Autowired
    private BrandService brandService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodClassService goodClassService;
}
