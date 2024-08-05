package com.aichebaba.rooftop.controller.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.model.GoodsExt;
import com.aichebaba.rooftop.model.Sku;
import com.aichebaba.rooftop.model.SkuPropValue;
import com.aichebaba.rooftop.model.enums.GoodsStatus;
import com.aichebaba.rooftop.model.enums.GoodsSubmitType;
import com.aichebaba.rooftop.model.enums.GoodsType;
import com.aichebaba.rooftop.model.enums.SkuPropValueType;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsExtService;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SkuService;
import com.aichebaba.rooftop.utils.Base64;
import com.aichebaba.rooftop.utils.MD5;
import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;

@Controller
@Scope("prototype")
public class GoodsOfflineController extends BaseController{
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SkuService skuService;
	@Autowired
	private GoodClassService goodClassService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private GoodsExtService goodsExtService;

    @ActionKey("list.html")
    public void list(){
//        String goodsName = getPara("goodsName", "");
//        String goodsItemNo = getPara("goodsItemNo", "");
//        String goodsCode = getPara("goodsCode", "");
//        int startStock = getParaToInt("startStock", 0);
//        int endStock = getParaToInt("endStock", 0);
//
//        PageList<Goods> pager = goodsService.findGoods(null, GoodsStatus.offline, goodsName, null,goodsCode, goodsItemNo,
//                curCustomerId(), null, null, startStock, endStock, getPageParam());
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
													   GoodsStatus.offline, 
													   curCustomerId(),
													   null,
													   submittype,
													   null,
													   false,
													   getPageParam());

		setAttr("pager", pager);
    	
    	
        render("list.html");
    }

    public void onShelf(){
        String code = getPara("code");
        if (StrKit.isBlank(code)) {
            error("请选择商品");
            return;
        } else {
            Goods goods = goodsService.getByCode(code);
			goodsService.applyOnline(goods.getId(), 0, GoodsSubmitType.seller);
        }
        ok("上架申请成功");
    }

    public void delete(){
        String code = getPara("code");
        if (StrKit.isBlank(code)) {
            error("请选择商品");
            return;
        } else {
            goodsService.delete(code);
        }
        ok("商品删除成功");
    }

    @ActionKey("{code}.html")
    public void edit() throws Exception{
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

        setAttr("imagePolicy", policy("image"));
        setAttr("filePolicy", policy("file"));
        setAttr("goods", goods);
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

        Collections.sort(skus, new Comparator<Sku>(){
            public int compare(Sku arg0, Sku arg1) {
                //第一次比较规格
                int i = arg0.getSkuSpecValue().compareTo(arg1.getSkuSpecValue());

                //如果规格相同则进行第二次比较
                if(i==0){
                    //第二次比较颜色
                    int j=arg0.getSkuColorValue().compareTo(arg1.getSkuColorValue());
                    //如果颜色相同则返回按尺寸排序
                    if(j==0){
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

        render("edit.html");
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
    public void save() throws Exception{
		Integer thirdclassid = getParaToInt("thirdclassid");
		Integer secondclassid = getParaToInt("secondclassid");
		Integer firstclassid = getParaToInt("firstclassid");
		Integer brandid = getParaToInt("brandid");

		if (thirdclassid == null || secondclassid == null || firstclassid == null) {
			error("请选择分类");
			return;
		}

		if (brandid == null) {
			error("请选择品牌");
			return;
		}

		Integer[] values = getParaValuesToInt("vid");

		List<GoodsClass> list = goodClassService.getChildren(thirdclassid);

		if (list.size() != values.length) {
			error("请选择商品所有属性");
			return;
		}

        String code = getPara("code");
        String goodsType = getPara("type", "");
		// String brand = getPara("brand", "");
        String name = getPara("name", "");
        String itemNo = getPara("itemNo", "");
		// Double weight = getParaToDouble("weight", 0d);
        String[] colors = getParaValues("color");
        if (colors == null) {
            error("请添加颜色");
            return;
        }
        List<String> colorList = new ArrayList<>();
        for (String item : colors) {
            if (StrKit.isBlank(item)) {
                error("颜色不能为空");
                return;
            }
            if (repeatCheck(item, colorList)) {
                error("颜色有值重复");
                return;
            } else {
                colorList.add(item);
            }
        }

        String[] sizes = getParaValues("size");
        if (sizes == null) {
            error("请添加尺寸");
            return;
        }
        List<String> sizeList = new ArrayList<>();
        for (String item : sizes) {
            if (StrKit.isBlank(item)) {
                error("尺寸不能为空");
                return;
            }
            if (repeatCheck(item, sizeList)) {
                error("尺寸有值重复");
                return;
            } else {
                sizeList.add(item);
            }
        }

        String[] specValues = getParaValues("specValue");
        if(specValues == null){
            error("请添加规格");
            return;
        }
        List<String> specList = new ArrayList<>();
        for(String specValue : specValues){
            if(StrKit.isBlank(specValue)){
                error("规格不能为空");
                return;
            }
            if(repeatCheck(specValue, specList)){
                error("规格有值重复");
                return;
            }else{
                specList.add(specValue);
            }
        }
//        String[] specImgs = getParaValues("specImg");

        String[] skuIds = getParaValues("skuId");
        String[] specPropValues = getParaValues("specPropValue");
        String[] colorPropValues = getParaValues("colorPropValue");
        String[] sizePropValues = getParaValues("sizePropValue");
        String[] goodsImgs = getParaValues("goodsImg");
        String[] prices = getParaValues("price");
        String[] stocks = getParaValues("stock");
        String[] retailPrices = getParaValues("retailPrice");
        String[] weights = getParaValues("weight");

        Integer[] oldIds = getParaValuesToInt("oldId");
        Double minPrice = 999999999d;
        for(String price : prices){
            if(StrKit.isBlank(price)){
                error("请填写价格");
                return;
            }
            try {
                Double dPrice = Double.parseDouble(price);
                if(dPrice <= 0){
                    error("请填写合适的价格");
                    return;
                }
                if(minPrice > dPrice){
                    minPrice = dPrice;
                }
            }catch (Exception ex){
                error("请填写合适的价格");
                return;
            }
        }
        Integer sumStock = 0;
        for(String stock : stocks){
            if(StrKit.isBlank(stock)){
                error("请填写库存");
                return;
            }
            try {
                Integer intStock = Integer.parseInt(stock);
                if(intStock < 0){
                    error("请填写合适的库存");
                    return;
                }
                sumStock += intStock;
            }catch (Exception ex){
                error("请填写合适的库存");
                return;
            }
        }
        Double minRetailPrice = 999999999d;
        for(String retailPrice : retailPrices){
            if(StrKit.isBlank(retailPrice)){
                error("请填写建议零售价");
                return;
            }
            try {
                Double dPrice = Double.parseDouble(retailPrice);
                if(dPrice <= 0){
                    error("请填写合适的建议零售价");
                    return;
                }
                if(minRetailPrice > dPrice){
                    minRetailPrice = dPrice;
                }
            }catch (Exception ex){
                error("请填写合适的建议零售价");
                return;
            }
        }
        Double minWeight = Double.MAX_VALUE;
        for(String weight : weights){
            if(StrKit.isBlank(weight)){
                error("请填写重量");
                return;
            }
            try {
                Double dWeight = Double.parseDouble(weight);
                if(dWeight < 0){
                    error("请填写合适的重量");
                    return;
                }
                if(minWeight > dWeight){
                    minWeight = dWeight;
                }
            }catch (Exception ex){
                error("请填写合适的重量");
                return;
            }
        }

        String fitCar = getPara("fitCar", "");
        Integer isSpecial = getParaToInt("isSpecial", 0);
        String comment = getPara("comment", "");
        String[] tempHeadImgUrls = new String[5];
        for (int i = 0; i < 5; i++) {
            UploadFile file = getFile("headImgUrl" + (i + 1), "");
            if (file != null) {
                tempHeadImgUrls[i] = file.getSaveDirectory() + File.separator + file.getFileName();
                File temp = new File(tempHeadImgUrls[i]);
                Long fileSize = temp.length();
                if (fileSize > Constant.MAX_IMAGE_SIZE) {
                    error("图片文件太大,请重新上传！");
                    return;
                }
            }
        }

        String dataPackageUrl1 = getPara("dataPackageUrl1", "");
        String dataPackageUrl2 = getPara("dataPackageUrl2", "");
        String imgPackageUrl1 = getPara("imgPackageUrl1", "");
        String imgPackageUrl2 = getPara("imgPackageUrl2", "");

        String[] headImgUrls = new String[5];
        for (int i = 0; i < 5; i++) {
            if (StrKit.notBlank(tempHeadImgUrls[i])) {
                String upFile = PicUpload.picUpload(tempHeadImgUrls[i]);
                if (StrKit.isBlank(upFile)) {
                    error("图片上传失败,请重新上传！");
                    return;
                }
                headImgUrls[i] = upFile;
            }
        }

        String certificateNo = getPara("certificateNo");
        int certificateFlag = getParaToInt("certificateFlag", 0);
        String applicant = getPara("applicant");
        String makerName = getPara("makerName");
        String productName = getPara("productName");
        String type3cNo = getPara("type3cNo");
        String series = getPara("series");
        String fitWeight = getPara("fitWeight");
        String fitAge = getPara("fitAge");
        String interfaces = getPara("interfaces");
        String fixedMode = getPara("fixedMode");

        Goods goods = goodsService.getByCode(code);

		goods.setThirdclassid(thirdclassid);
		goods.setSecondclassid(secondclassid);
		goods.setFirstclassid(firstclassid);
		goods.setBrandid(brandid);

		Brand brand = brandService.getById(brandid);
		if (null != brand) {
			goods.setBrand(brand.getName());
		}

        goods.setType(GoodsType.valOf(goodsType));
		// goods.setBrand(brand);
        goods.setName(name);
        goods.setItemNo(itemNo);
        goods.setWeight((int)(minWeight * 1000));
        goods.setFitCar(fitCar);
        goods.setStock(sumStock);
        goods.setIsSpecial(isSpecial == 1);
        goods.setComment(comment);
        goods.setRetailPrice((int) (minRetailPrice * 100));
        goods.setWholesalePrice((int) (minPrice * 100));
		goods.setSellerId(curCustomerId());
		goods.setSubmittype(GoodsSubmitType.seller);
        goods.setStatus(GoodsStatus.wait_audit);
        if(StrKit.notBlank(headImgUrls[0])) {
            goods.setHeadImgUrl1(headImgUrls[0]);
        }
        if(StrKit.notBlank(headImgUrls[1])) {
            goods.setHeadImgUrl2(headImgUrls[1]);
        }
        if(StrKit.notBlank(headImgUrls[2])) {
            goods.setHeadImgUrl3(headImgUrls[2]);
        }
        if(StrKit.notBlank(headImgUrls[3])) {
            goods.setHeadImgUrl4(headImgUrls[3]);
        }
        if(StrKit.notBlank(headImgUrls[4])) {
            goods.setHeadImgUrl5(headImgUrls[4]);
        }

        if(StrKit.notBlank(dataPackageUrl1)) {
            goods.setDataPackageUrl1(dataPackageUrl1);
        }
        if(StrKit.notBlank(dataPackageUrl2)) {
            goods.setDataPackageUrl2(dataPackageUrl2);
        }
        if(StrKit.notBlank(imgPackageUrl1)) {
            goods.setImgPackageUrl1(imgPackageUrl1);
        }
        if(StrKit.notBlank(imgPackageUrl2)) {
            goods.setImgPackageUrl2(imgPackageUrl2);
        }
        if (goods.getType().equals(GoodsType.seat)) {
            goods.setCertificateNo(certificateNo);
            goods.setCertificateFlag(certificateFlag);
            goods.setApplicant(applicant);
            goods.setMakerName(makerName);
            goods.setProductName(productName);
            goods.setType3cNo(type3cNo);
            goods.setSeries(series);
            goods.setFitWeight(fitWeight);
            goods.setFitAge(fitAge);
            goods.setInterfaces(interfaces);
            goods.setFixedMode(fixedMode);
        }
        goodsService.update(goods);

        //规格
        Map<String, Integer> specValueToId = new HashMap<>();
        for (int sIndex = 0; sIndex < specList.size(); sIndex++) {
            SkuPropValue skuPropValue = skuService.getSkuPropValueByValue(code,SkuPropValueType.spec.getValue(), specList.get(sIndex));
            if(skuPropValue != null){
//                if(StrKit.notBlank(specImgs[sIndex])) {
//                    skuPropValue.setImg(specImgs[sIndex]);
//                    skuService.saveSkuPropValue(skuPropValue);
//                }
                specValueToId.put(specList.get(sIndex), skuPropValue.getId());
                continue;
            }
            skuPropValue = new SkuPropValue();
            skuPropValue.setGoodsCode(goods.getCode());
            skuPropValue.setSkuPropId(SkuPropValueType.spec.getValue());   //规格
            skuPropValue.setValue(specList.get(sIndex));

//            skuPropValue.setImg(specImgs[sIndex]);
            skuPropValue.setImg("");
            skuPropValue.setCreated(new Date());
            skuPropValue = skuService.saveSkuPropValue(skuPropValue);
            specValueToId.put(specList.get(sIndex), skuPropValue.getId());
        }

        //颜色
        Map<String, Integer> colorValueToId = new HashMap<>();
        for (String value : colorList) {
            SkuPropValue skuPropValue = skuService.getSkuPropValueByValue(code,SkuPropValueType.color.getValue(), value);
            if(skuPropValue != null){
                colorValueToId.put(value, skuPropValue.getId());
                continue;
            }
            skuPropValue = new SkuPropValue();
            skuPropValue.setGoodsCode(goods.getCode());
            skuPropValue.setSkuPropId(SkuPropValueType.color.getValue());   //颜色
            skuPropValue.setValue(value);
            skuPropValue.setImg("");
            skuPropValue.setCreated(new Date());
            skuPropValue = skuService.saveSkuPropValue(skuPropValue);
            colorValueToId.put(value, skuPropValue.getId());
        }

        //尺寸
        Map<String, Integer> sizeValueToId = new HashMap<>();
        for (String value : sizeList) {
            SkuPropValue skuPropValue = skuService.getSkuPropValueByValue(code,SkuPropValueType.size.getValue(), value);
            if(skuPropValue != null){
                sizeValueToId.put(value, skuPropValue.getId());
                continue;
            }
            skuPropValue = new SkuPropValue();
            skuPropValue.setGoodsCode(goods.getCode());
            skuPropValue.setSkuPropId(SkuPropValueType.size.getValue());
            skuPropValue.setValue(value);
            skuPropValue.setImg("");
            skuPropValue.setCreated(new Date());
            skuPropValue = skuService.saveSkuPropValue(skuPropValue);
            sizeValueToId.put(value, skuPropValue.getId());
        }

        //删除1期旧数据
        if(oldIds != null) {
            skuService.delSkuById(oldIds);
        }

        for(int index = 0; index<specPropValues.length; index++){
            Sku sku;
            if(StrKit.isBlank(skuIds[index])){
                sku = new Sku();
                sku.setGoodsCode(goods.getCode());
                StringBuffer properties = new StringBuffer();
                properties.append(SkuPropValueType.spec.getValue() + ":" + specValueToId.get(specPropValues[index]) + ";");
                properties.append(SkuPropValueType.color.getValue() + ":" + colorValueToId.get(colorPropValues[index]) + ";");
                properties.append(SkuPropValueType.size.getValue() + ":" + sizeValueToId.get(sizePropValues[index]) + ";");
                sku.setProperties(properties.toString());
                sku.setCreated(new Date());
            }else{
                sku = skuService.getSkuById(Integer.parseInt(skuIds[index]));
            }
            sku.setImgUrl(goodsImgs[index]);
            sku.setPrice((int) (Double.parseDouble(prices[index]) * 100));
            sku.setStock(Integer.parseInt(stocks[index]));
            sku.setRetailPrice((int) (Double.parseDouble(retailPrices[index]) * 100));
            sku.setWeight(((int) Math.round(Double.parseDouble(weights[index]) * 100)) * 10);

            skuService.saveSku(sku);
        }

		// 删除老数据
		goodsExtService.deleteByGoodId(goods.getId());

		for (Integer value : values) {
			GoodsExt goodsExt = new GoodsExt();
			goodsExt.setClassid(value);
			goodsExt.setGoodid(goods.getId());
			goodsExt.setCreated(new Date());
			goodsExtService.save(goodsExt);
		}

		brandService.save(brandid, thirdclassid);

		ok("保存成功", goods.getStatus().getVal());
    }

    private Boolean repeatCheck(String value, List<String> list){
        if(list == null){
            return false;
        }
        for(String item : list){
            if(item.equals(value)){
                return true;
            }
        }
        return false;
    }

    private Map<String, String> policy(String type) throws Exception{
        Map<String, String> param = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("bucket", "nachepin-storage");
        info.put("expiration", String.valueOf(System.currentTimeMillis() + 60 * 60 - 8 * 60 * 60));
        if(type.equals("file")) {
            info.put("save-key", "/file/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        }else{
            info.put("save-key", "/images/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        }
        String policy = JSON.toJSONString(info);
        policy = Base64.encode(policy, "UTF-8");
        String signature = policy + "&" + Constant.UPYUN_SIGNATURE;
        signature = MD5.md5(signature, "UTF-8");
        param.put("policy", policy);
        param.put("signature", signature);
        return param;
    }

	/**
	 * 发布商品 分类选择
	 */
	@ActionKey("fabu")
	public void fabu() {
		Integer classid = getParaToInt("classid");
		Integer goodid = getParaToInt("goodid");
		String code = getPara("code");
		setAttr("goodid", goodid);
		setAttr("code", code);
		if (null != classid && classid != 0) {
			setAttr("thirdClass", goodClassService.getById(classid));
		}
		render("fabugoods.html");
	}
}
