package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.vo.SearchParams;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("prototype")
public class GoodsClassController extends BaseController {

    @Autowired
    private GoodClassService goodClassService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private BrandService brandService;

    @ActionKey("{classId:\\d+}.html")
    public void list(){
        int id = getUrlParaToInt("classId", 0);

        //获取分类信息
        GoodsClass goodsClass = getThirdClass(id);

        setAttr("goodsClass", goodsClass);

        List<Brand> brands = brandService.findBrandByClassId(id);
        setAttr("brands", brands);

        PageList<Goods> pager = goodsService.findClassGoods(GoodsStatus.online, getSearchParams());
        setAttr("pager", pager);
        setAttr("url", getRequest().getRequestURI());

        render("list.html");
    }

    private SearchParams getSearchParams()
    {
        return getAttr("searchParams");
    }
}
