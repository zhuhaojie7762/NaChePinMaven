package com.aichebaba.rooftop.controller.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.config.Constant;
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
import com.aichebaba.rooftop.utils.Base64;
import com.aichebaba.rooftop.utils.MD5;
import com.alibaba.fastjson.JSON;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;

@Controller
@Scope("prototype")
public class BackGoodsController extends BaseController {
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
        String code = getPara("goodsCode", "");
        String goodsName = getPara("goodsName", "");
        String itemNo = getPara("goodsItemNo", "");

        PageList<Goods> pager = goodsService.findGoods(null, GoodsStatus.back_audit, goodsName, null, code, itemNo,
                curCustomerId(), null, null, 0, 0, getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    @ActionKey("{code}.html")
    public void edit() throws Exception {
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
        setAttr("imgUrl", AppConfig.getImgPath());
        setAttr("imagePolicy", policy("image"));
        setAttr("filePolicy", policy("file"));

        List<SkuPropValue> specPropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.spec.getValue());
        setAttr("spec", specPropValues);
        setAttr("specLength", specPropValues.size());
        List<SkuPropValue> colorPropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.color.getValue());
        setAttr("color", colorPropValues);
        List<SkuPropValue> sizePropValues = skuService.getPropValuesByGoodsCodeAndPropId(code, SkuPropValueType.size.getValue());
        setAttr("size", sizePropValues);

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

        render("../offline/edit.html");
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

    private Map<String, String> policy(String type) throws Exception {
        Map<String, String> param = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("bucket", "nachepin-storage");
        info.put("expiration", String.valueOf(System.currentTimeMillis() + 60 * 60 - 8 * 60 * 60));
        if (type.equals("file")) {
            info.put("save-key", "/file/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        } else {
            info.put("save-key", "/images/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        }
//        info.put("x-gmkerl-type", "fix_max");
//        info.put("x-gmkerl-value", Constant.IMG_SIZE_L);
        String policy = JSON.toJSONString(info);
        policy = Base64.encode(policy, "UTF-8");
        String signature = policy + "&" + Constant.UPYUN_SIGNATURE;
        signature = MD5.md5(signature, "UTF-8");
        param.put("policy", policy);
        param.put("signature", signature);
        return param;
    }
}
