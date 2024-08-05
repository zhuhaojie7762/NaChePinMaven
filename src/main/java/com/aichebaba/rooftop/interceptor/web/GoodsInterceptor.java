package com.aichebaba.rooftop.interceptor.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.vo.SearchParams;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageParam;

public class GoodsInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
            BaseController c = (BaseController) inv.getController();
        /*if (inv.getActionKey().startsWith("/goods/list.html")) {
            boolean news = c.getParaToBoolean("news", false);
            String tab = c.getPara("tab", "all");
            SearchParams curParams;
            init(c);
            if ("all".equals(tab)) {
                curParams = getCurParams(c, "allParams", tab, news);
            } else if ("cushion".equals(tab)) {
                curParams = getCurParams(c, "cushionParams", tab, news);
            } else if ("floorMat".equals(tab)) {
                curParams = getCurParams(c, "floorMatParams", tab, news);
            } else if ("wheelCover".equals(tab)) {
                curParams = getCurParams(c, "wheelCoverParams", tab, news);
            } else if ("carTrim".equals(tab)) {
                curParams = getCurParams(c, "carTrimParams", tab, news);
            } else if ("seat".equals(tab)) {
                curParams = getCurParams(c, "seatParams", tab, news);
            } else {
                curParams = getCurParams(c, "otherParams", tab, news);
            }

            String brand = c.getPara("brand");
            if (StrKit.notBlank(brand)) {
                String value = "like_" + brand;
                curParams.addParam("brand", brand, value, "g.brand", brand);
            }
            String goodsName = c.getPara("goodsName");
            if (StrKit.notBlank(goodsName)) {
                String value = "like_" + goodsName;
                curParams.addParam("goodsName", goodsName, value, "g.name", goodsName);
            }
            String itemNo = c.getPara("itemNo");
            if (StrKit.notBlank(itemNo)) {
                String value = "likes_" + itemNo;
                curParams.addParam("itemNo", itemNo, value, "g.itemNo", itemNo);
            }
            String color = c.getPara("color");
            if (StrKit.notBlank(color)) {
                String value = "like_" + color;
                curParams.addParam("color", color, value, "g.color", color);
            }
            String size = c.getPara("size");
            if (StrKit.notBlank(size)) {
                String value = "like_" + size;
                curParams.addParam("size", size, value, "g.size", size);
            }
            String minPrice = c.getPara("minPrice");
            String maxPrice = c.getPara("maxPrice");
            if (StrKit.notBlank(minPrice, maxPrice)) {
                long min = (long) (Double.valueOf(minPrice) * 100);
                long max = (long) (Double.valueOf(maxPrice) * 100);
                String value = "between_" + min + "_" + max + "元";
                String name = minPrice + "~" + maxPrice + "元";
                curParams.addParam("price", minPrice, value, "g.wholesalePrice", name);
                curParams.addParam("minPrice", minPrice, "");
                curParams.addParam("maxPrice", maxPrice, "");
            } else if (StrKit.notBlank(minPrice)) {
                long min = (long) (Double.valueOf(minPrice) * 100);
                String value = "ge_" + min;
                curParams.addParam("minPrice", minPrice, value, "g.wholesalePrice", "大于" + minPrice + "元");
            } else if (StrKit.notBlank(maxPrice)) {
                long max = (long) (Double.valueOf(maxPrice) * 100);
                String value = "le_" + max;
                curParams.addParam("maxPrice", maxPrice, value, "g.wholesalePrice", "小于" + maxPrice + "元");
            }
            String fitCar = c.getPara("fitCar");
            if (StrKit.notBlank(fitCar)) {
                String value = "like_" + fitCar;
                curParams.addParam("fitCar", fitCar, value, "g.fitCar", fitCar);
            }
            String sorts = c.getPara("sorts");
            if (StrKit.notBlank(sorts)) {
                curParams.getOrders().clear();
                curParams.addOrderBy(sorts);
            }

            curParams.getPageParam().setCurNo(c.getPageNo());
        } else {
            c.removeSessionAttr("cushionParams");
            c.removeSessionAttr("floorMatParams");
            c.removeSessionAttr("wheelCoverParams");
            c.removeSessionAttr("carTrimParams");
            c.removeSessionAttr("seatParams");
            c.removeSessionAttr("otherParams");
        }*/
        if (inv.getActionKey().startsWith("/goods/list.html")) {

            SearchParams searchParams = new SearchParams();
            searchParams.setPageParam(new PageParam(20, 1));

            //品牌
            String brand = c.getPara("brand");
            if (StrKit.notBlank(brand)) {
                String value = "like_" + brand;
                searchParams.addParam("brand", brand, value, "g.brand", brand);
            }
            //商品名
            String goodsName = c.getPara("goodsName");
            if (StrKit.notBlank(goodsName)) {
                String value = "like_" + goodsName;
                searchParams.addParam("goodsName", goodsName, value, "g.name", goodsName);
            }
            //货号
            String itemNo = c.getPara("itemNo");
            if (StrKit.notBlank(itemNo)) {
                String value = "likes_" + itemNo;
                searchParams.addParam("itemNo", itemNo, value, "g.itemNo", itemNo);
            }
            //颜色
            String color = c.getPara("color");
            if (StrKit.notBlank(color)) {
                String value = "like_" + color;
                searchParams.addParam("color", color, value, "g.color", color);
            }
            //尺寸
            String size = c.getPara("size");
            if (StrKit.notBlank(size)) {
                String value = "like_" + size;
                searchParams.addParam("size", size, value, "g.size", size);
            }
            //价格
            String minPrice = c.getPara("minPrice");
            String maxPrice = c.getPara("maxPrice");
            if (StrKit.notBlank(minPrice, maxPrice)) {
                long min = (long) (Double.valueOf(minPrice) * 100);
                long max = (long) (Double.valueOf(maxPrice) * 100);
                String value = "between_" + min + "_" + max + "元";
                String name = minPrice + "~" + maxPrice + "元";
                searchParams.addParam("price", minPrice, value, "g.wholesalePrice", name);
                searchParams.addParam("minPrice", minPrice, "");
                searchParams.addParam("maxPrice", maxPrice, "");
            } else if (StrKit.notBlank(minPrice)) {
                long min = (long) (Double.valueOf(minPrice) * 100);
                String value = "ge_" + min;
                searchParams.addParam("minPrice", minPrice, value, "g.wholesalePrice", "大于" + minPrice + "元");
            } else if (StrKit.notBlank(maxPrice)) {
                long max = (long) (Double.valueOf(maxPrice) * 100);
                String value = "le_" + max;
                searchParams.addParam("maxPrice", maxPrice, value, "g.wholesalePrice", "小于" + maxPrice + "元");
            }
            //关键字
            String wd = c.getPara("wd");
            if(StrKit.notBlank(wd)){
                searchParams.addParam("wd", wd, "搜索:" + wd);
            }
            //排序
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

//    private SearchParams getCurParams(BaseController c, String key, String tab, boolean news) {
//        if (news) {
//            SearchParams searchParams = new SearchParams();
//            searchParams.setPageParam(new PageParam(12, 1));
//            c.setSessionAttr(key, searchParams);
//            searchParams.addParam("tab", tab, "");
//            return searchParams;
//        } else {
//            return c.getSessionAttr(key);
//        }
//    }

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
