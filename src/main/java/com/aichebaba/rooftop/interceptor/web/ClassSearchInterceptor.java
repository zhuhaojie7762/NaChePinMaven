package com.aichebaba.rooftop.interceptor.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.vo.AttrSearch;
import com.aichebaba.rooftop.vo.SearchParams;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageParam;

import java.util.ArrayList;
import java.util.List;

public class ClassSearchInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
            BaseController c = (BaseController) inv.getController();
        if (inv.getActionKey().startsWith("/goods/class") && !c.isAjax()) {

            SearchParams searchParams = new SearchParams();
            searchParams.setPageParam(new PageParam(20, 1));
            //3级分类
            int classId = c.getUrlParaToInt("classId");
            if (classId > 0) {
                searchParams.addParam("thirdclassid", "eq_" + classId, "g.thirdclassid", "");
            }

            //品牌
            String brand = c.getPara("brand");
            if(StrKit.notBlank(brand)){
                String[] pp = brand.split("_");
                Brand curbrand = c.getBrandBySession(Integer.parseInt(pp[1]));
                String name = "品牌:" + curbrand.getName();
                searchParams.addParam("brand", brand, "g.brandid", name);
            }

            // 属性
            String attr = c.getPara("attr");
            if (StrKit.notBlank(attr))
            {
                String[] items = attr.split("-");
                List<String> names = new ArrayList<>();
                for(String item : items){
                    String[] pp = item.split("_");
                    int id = Integer.parseInt(pp[1]);

                    //属性型值
                    GoodsClass value = c.getGoodsClassBySession(id);
                    if(value != null) {
                        //属性
                        GoodsClass attri = c.getGoodsClassBySession(value.getPid());
                        if(attri != null) {
                            String name = attri.getName() + ":" + value.getName();
                            searchParams.addAttr(pp[1], pp[0], name);
                        }
                    }
                }
            }

            String sorts = c.getPara("sorts");
            if (StrKit.notBlank(sorts)) {
                searchParams.getOrders().clear();
                searchParams.addOrderBy(sorts);
            }

            searchParams.getPageParam().setCurNo(c.getPageNo());

            c.setAttr("searchParams", searchParams);
        }
        inv.invoke();
    }

    private SearchParams getCurParams(BaseController c, String key, String tab, boolean news) {
        if (news) {
            SearchParams searchParams = new SearchParams();
            searchParams.setPageParam(new PageParam(12, 1));
            c.setSessionAttr(key, searchParams);
            searchParams.addParam("tab", tab, "");
            return searchParams;
        } else {
            return c.getSessionAttr(key);
        }
    }

/*    private void init(BaseController c) {
        SearchParams allParams = c.getSessionAttr("allParams");
        if (allParams == null) {
            allParams = new SearchParams();
            allParams.setPageParam(new PageParam(12, 1));
            allParams.addParam("tab", "all", "");
            c.setSessionAttr("allParams", allParams);
        }
        SearchParams cushionParams = c.getSessionAttr("cushionParams");
        if (cushionParams == null) {
            cushionParams = new SearchParams();
            cushionParams.setPageParam(new PageParam(12, 1));
            cushionParams.addParam("tab", "cushion", "");
            c.setSessionAttr("cushionParams", cushionParams);
        }
        SearchParams floorMatParams = c.getSessionAttr("floorMatParams");
        if (floorMatParams == null) {
            floorMatParams = new SearchParams();
            floorMatParams.setPageParam(new PageParam(12, 1));
            floorMatParams.addParam("tab", "floorMat", "");
            c.setSessionAttr("floorMatParams", floorMatParams);
        }
        SearchParams wheelCoverParams = c.getSessionAttr("wheelCoverParams");
        if (wheelCoverParams == null) {
            wheelCoverParams = new SearchParams();
            wheelCoverParams.setPageParam(new PageParam(12, 1));
            wheelCoverParams.addParam("tab", "wheelCover", "");
            c.setSessionAttr("wheelCoverParams", wheelCoverParams);
        }
        SearchParams carTrimParams = c.getSessionAttr("carTrimParams");
        if (carTrimParams == null) {
            carTrimParams = new SearchParams();
            carTrimParams.setPageParam(new PageParam(12, 1));
            carTrimParams.addParam("tab", "carTrim", "");
            c.setSessionAttr("carTrimParams", carTrimParams);
        }
        SearchParams seatParams = c.getSessionAttr("seatParams");
        if (seatParams == null) {
            seatParams = new SearchParams();
            seatParams.setPageParam(new PageParam(12, 1));
            seatParams.addParam("tab", "seat", "");
            c.setSessionAttr("seatParams", seatParams);
        }
        SearchParams otherParams = c.getSessionAttr("otherParams");
        if (otherParams == null) {
            otherParams = new SearchParams();
            otherParams.addParam("tab", "other", "");
            otherParams.setPageParam(new PageParam(12, 1));
            c.setSessionAttr("otherParams", otherParams);
        }
    }*/
}
