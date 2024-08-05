package com.aichebaba.rooftop.controller.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.SkuPropValue;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.SkuPropValueType;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SkuService;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;

@Controller
@Scope("prototype")
public class WaitCheckController extends BaseController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SkuService skuService;
	@Autowired
	private GoodClassService goodClassService;
	@Autowired
	private BrandService brandService;

    @ActionKey("list.html")
    public void list(){
//        String code = getPara("code", "");
//        String goodsName = getPara("goodsName", "");
//        String itemNo = getPara("itemNo", "");
//
//        PageList<Goods> pager = goodsService.findGoods(null, GoodsStatus.wait_audit, goodsName, null, code, itemNo,
//                curCustomerId(), null, null, 0, 0, getPageParam());
//        setAttr("pager", pager);
        
        
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
													   GoodsStatus.wait_audit, 
													   curCustomerId(),
													   null,
													   submittype,
													   null,
													   false,
													   getPageParam());

		setAttr("pager", pager);
        
        
        
        
        
        
        render("list.html");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    @ActionKey("{code}.html")
    public void details(){
        String code = getUrlPara("code");
		Goods goods = goodsService.findbyCode(code);
        
		Integer classid = getParaToInt("classid");

		if (classid != null) {
			GoodsClass thirdclass = goodClassService.getById(classid);
			if (thirdclass != null) {
				goods.setThirdclassid(thirdclass.getId());
				goods.setThirdclassname(thirdclass.getName());

				GoodsClass secondclass = goodClassService.getById(thirdclass.getPid());
				if (secondclass != null) {
					goods.setSecondclassid(secondclass.getId());
					goods.setSecondclassname(secondclass.getName());

					GoodsClass firstclass = goodClassService.getById(secondclass.getPid());

					if (firstclass != null) {
						goods.setFirstclassid(firstclass.getId());
						goods.setFirstclassname(firstclass.getName());
					}
				}
			}
		}

		setAttr("brands", brandService.findAll());

        setAttr("goods", goods);
        List<SkuPropValue> specPropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.spec.getValue());
        setAttr("spec", specPropValues.size());
        List<SkuPropValue> colorPropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.color.getValue());
        setAttr("color", colorPropValues.size());
        List<SkuPropValue> sizePropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.size.getValue());
        setAttr("size", sizePropValues.size());

        List<Sku> skus = skuService.getSkuByGoodsCode(code);
        for(Sku sku : skus){
            String[] skuPropValues = sku.getProperties().split(";");
            for(String pValue : skuPropValues){
                String[] pp = pValue.split(":");
                if(SkuPropValueType.spec.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuSpecValue(getSkuPropValue(Integer.parseInt(pp[1]), specPropValues));
                }else if(SkuPropValueType.color.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuColorValue(getSkuPropValue(Integer.parseInt(pp[1]), colorPropValues));
                }else if(SkuPropValueType.size.getVal().equals(Integer.valueOf(pp[0]))){
                    sku.setSkuSizeValue(getSkuPropValue(Integer.parseInt(pp[1]), sizePropValues));
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
        setAttr("sku", skus);

		List<GoodsClass> list = Collections.emptyList();
		if (null != goods.getThirdclassid() && goods.getThirdclassid() != 0) {

			list = goodClassService.getChildren(goods.getThirdclassid());

			List<GoodsClass> valuelist = goodClassService.findGoodsClassByGoodid(goods.getId());

			for (GoodsClass entity : list) {
				entity.setChildren(goodClassService.getChildren(entity.getId()));
				for (GoodsClass value : valuelist) {
					if (value.getPid() == entity.getId()) {
						entity.setValue(value);
						break;
					}
				}
			}
		}

		// 属性
		setAttr("attrs", list);

        render("details.html");
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
}
