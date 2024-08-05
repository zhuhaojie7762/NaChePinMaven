package com.aichebaba.rooftop.controller.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.SkuPropValue;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.SkuPropValueType;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SkuService;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;

@Controller
@Scope("prototype")
public class OnlineController extends BaseController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

	@Autowired
	private GoodClassService goodClassService;
	@Autowired
	private BrandService brandService;

    @ActionKey("list.html")
    public void list() {
		// String goodsName = getPara("goodsName", "");
		// String itemNo = getPara("itemNo", "");
		// String code = getPara("goodsCode", "");
		// int startStock = getParaToInt("startStock", 0);
		// int endStock = getParaToInt("endStock", 0);
		//
		// PageList<Goods> pager = goodsService.findGoods(null,
		// GoodsStatus.online, goodsName, null, code, itemNo,
		// curCustomerId(), null, null, startStock, endStock, getPageParam());
		// setAttr("pager", pager);
    	
    	
    	setAttr("firstclasslist", goodClassService.getChildren(0));
		setAttr("brands", brandService.findAll());

		// 商品名称
		String name = getPara("name");

		String itemNo = getPara("itemNo");

		Integer firstclassid = getParaToInt("firstclassid");
		if (null != firstclassid) {
			setAttr("secondclasslist", goodClassService.getChildren(firstclassid));
		}

		Integer secondclassid = getParaToInt("secondclassid");
		if (null != secondclassid) {
			setAttr("thirdclasslist", goodClassService.getChildren(secondclassid));
		}

		Integer thirdclassid = getParaToInt("thirdclassid");

		Integer brandid = getParaToInt("brandid");

		Double minprice = getParaToDouble("minprice");
		Double maxprice = getParaToDouble("maxprice");

		Integer minsellcnt = getParaToInt("minsellcnt");
		Integer maxsellcnt = getParaToInt("maxsellcnt");

		Integer submittype = getParaToInt("submittype");

		PageList<Goods> pager = goodsService.findGoods(firstclassid, 
													   secondclassid, 
													   thirdclassid, 
													   null,
													   null,
													   brandid,
													   minprice != null ? (int) (minprice * 100) : null, 
													   maxprice != null ? (int) (maxprice * 100) : null,
													   minsellcnt, 
													   maxsellcnt, 
													   itemNo, 
													   name, 
													   GoodsStatus.online, 
													   curCustomerId(),
													   null,
													   submittype,
													   null,
													   false,
													   getPageParam());

		setAttr("pager", pager);

        render("list.html");
    }

    public void offShelf() {
        String code = getPara("code");
        if (StrKit.isBlank(code)) {
            error("请选择商品");
            return;
        } else {
            goodsService.offShelf(code, "");
        }
        ok("下架成功");
    }

    public void addStock() {
        String code = getPara("code");
        int num = getParaToInt("stock", 0);
        if (StrKit.isBlank(code)) {
            error("请选择商品");
            return;
        } else {
            goodsService.addStock(code, num);
        }
        ok("库存增加成功");
    }

    public void getGoodsByCode() {
        String code = getPara("code");
        setAttr("goodsCode", code);
        List<SkuPropValue> list = skuService.getSkuPropValuesByGoodsCode(code);
        List<Sku> skus = skuService.getSkuByGoodsCode(code);
        for(Sku sku : skus){
            String[] skuPropValues = sku.getProperties().split(";");
            for(String pValue : skuPropValues){
                String[] pp = pValue.split(":");
                if(SkuPropValueType.spec.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuSpecValue(getSkuPropValue(Integer.parseInt(pp[1]), list));
                }else if(SkuPropValueType.color.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuColorValue(getSkuPropValue(Integer.parseInt(pp[1]), list));
                }else if(SkuPropValueType.size.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuSizeValue(getSkuPropValue(Integer.parseInt(pp[1]), list));
                }
            }
        }

        Collections.sort(skus, new Comparator<Sku>() {
            public int compare(Sku arg0, Sku arg1) {
                //第一次比较规格
                int i = arg0.getSkuSpecValue().compareTo(arg1.getSkuSpecValue());

                //如果规格相同则进行第二次比较
                if (i == 0) {
                    //第二次比较颜色
                    int j = arg0.getSkuColorValue().compareTo(arg1.getSkuColorValue());
                    //如果颜色相同则返回按尺寸排序
                    if (j == 0) {
                        return arg0.getSkuSizeValue().compareTo(arg1.getSkuSizeValue());
                    }
                    return j;
                }
                return i;
            }
        });

        setAttr("skus", skus);

        String html = TemplateUtils.html("web/member/online/stock.html", getRequest());
        success(html.toString());
    }

    private String getSkuPropValue(int value, List<SkuPropValue> list){
        String result = "";
        for(SkuPropValue item : list){
            if(item.getId() == value){
                result = item.getValue();
            }
        }
        return result;
    }

    @Tx
    public void changeStock() {
        Integer[] ids = getParaValuesToInt("id");
        Integer[] nums = getParaValuesToInt("stock");
        String code = getPara("goodsCode", "");
        int sum = 0;
        if (ids == null || ids.length == 0) {
            return;
        } else {
            for (int i = 0; i < ids.length; i++) {
                if (nums[i] < 0) {
                    error("请输入正确的库存");
                    return;
                } else if (ids[i] < 0) {
                    error("请选择商品");
                    return;
                } else {
                    skuService.changeStock(ids[i], nums[i]);
                    Sku sku = skuService.getSkuById(ids[i]);
                    sum = sku.getAvailableStock() + sum;
                }
            }
            goodsService.changeStock(code, sum);
        }
        ok("库存增加成功");
    }
}
