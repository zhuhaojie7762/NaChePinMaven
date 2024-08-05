package com.aichebaba.rooftop.controller.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
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

@Controller
@Scope("prototype")
public class NewGoodsController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(NewGoodsController.class);

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

    @ActionKey("add.html")
    public void index() throws Exception {
        setAttr("imagePolicy", policy("image"));
        setAttr("filePolicy", policy("file"));
        render("add.html");
    }

    @Tx
    public void add() {
        try {
            String goodsType = getPara("type", "");
            String brand = getPara("brand", "");
            String name = getPara("name", "");
            String itemNo = getPara("itemNo", "");
//            Double weight = getParaToDouble("weight", 0d);

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
            if (specValues == null) {
                error("请添加规格");
                return;
            }
            List<String> specList = new ArrayList<>();
            for (String specValue : specValues) {
                if (StrKit.isBlank(specValue)) {
                    error("规格不能为空");
                    return;
                }
                if (repeatCheck(specValue, specList)) {
                    error("规格有值重复");
                    return;
                } else {
                    specList.add(specValue);
                }
            }
//            String[] specImgs = getParaValues("specImg");

            String[] specPropValues = getParaValues("specPropValue");
            String[] colorPropValues = getParaValues("colorPropValue");
            String[] sizePropValues = getParaValues("sizePropValue");
            String[] goodsImgs = getParaValues("goodsImg");
            String[] prices = getParaValues("price");
            String[] stocks = getParaValues("stock");
            String[] retailPrices = getParaValues("retailPrice");
            String[] weights = getParaValues("weight");
            Double minPrice = 999999999d;
            for (String price : prices) {
                if (StrKit.isBlank(price)) {
                    error("请填写价格");
                    return;
                }
                try {
                    Double dPrice = Double.parseDouble(price);
                    if (dPrice <= 0) {
                        error("请填写合适的价格");
                        return;
                    }
                    if (minPrice > dPrice) {
                        minPrice = dPrice;
                    }
                } catch (Exception ex) {
                    error("请填写合适的价格");
                    return;
                }
            }
            Integer sumStock = 0;
            for (String stock : stocks) {
                if (StrKit.isBlank(stock)) {
                    error("请填写库存");
                    return;
                }
                try {
                    Integer intStock = Integer.parseInt(stock);
                    if (intStock < 0) {
                        error("请填写合适的库存");
                        return;
                    }
                    sumStock += intStock;
                } catch (Exception ex) {
                    error("请填写合适的库存");
                    return;
                }
            }
            Double minRetailPrice = 999999999d;
            for (String retailPrice : retailPrices) {
                if (StrKit.isBlank(retailPrice)) {
                    error("请填写建议零售价");
                    return;
                }
                try {
                    Double dPrice = Double.parseDouble(retailPrice);
                    if (dPrice <= 0) {
                        error("请填写合适的建议零售价");
                        return;
                    }
                    if (minRetailPrice > dPrice) {
                        minRetailPrice = dPrice;
                    }
                } catch (Exception ex) {
                    error("请填写合适的建议零售价");
                    return;
                }
            }
            Double minWeight = Double.MAX_VALUE;
            for (String weight : weights) {
                if (StrKit.isBlank(weight)) {
                    error("请填写重量");
                    return;
                }
                try {
                    Double dWeight = Double.parseDouble(weight);
                    if (dWeight < 0) {
                        error("请填写合适的重量");
                        return;
                    }
                    if (minWeight > dWeight) {
                        minWeight = dWeight;
                    }
                } catch (Exception ex) {
                    error("请填写合适的重量");
                    return;
                }
            }

            String fitCar = getPara("fitCar", "");

            Integer isSpecial = getParaToInt("isSpecial", 0);
            String comment = getPara("comment", "");

            String[] tempHeadImgUrls = getParaValues("headImgUrl");
            String[] headImgUrls = new String[5];
            boolean hasImg = false;
            int i = 0;
            for (String temp : tempHeadImgUrls) {
                if (StrKit.notBlank(temp)) {
                    headImgUrls[i++] = temp;
                    hasImg = true;
                }
            }
            if (!hasImg) {
                error("请上传图片");
                return;
            }

            String dataPackageUrl1 = getPara("dataPackageUrl1", "");
            String dataPackageUrl2 = getPara("dataPackageUrl2", "");
            String imgPackageUrl1 = getPara("imgPackageUrl1", "");
            String imgPackageUrl2 = getPara("imgPackageUrl2", "");
            if (StrKit.isBlank(dataPackageUrl1) && StrKit.isBlank(dataPackageUrl2) && StrKit.isBlank(imgPackageUrl1) && StrKit.isBlank(imgPackageUrl2)) {
                error("请上传至少一个图片包或数据包");
                return;
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

            Goods goods = new Goods();
            goods.setType(GoodsType.valOf(goodsType));
            goods.setBrand(brand);
            goods.setName(name);
            goods.setItemNo(itemNo);
            goods.setWeight((int) (minWeight * 1000));
//            goods.setColor(color.toString());
//            goods.setSize(size.toString());
            goods.setFitCar(fitCar);
            goods.setStock(sumStock);
            goods.setIsSpecial(isSpecial == 1);
            goods.setComment(comment);
            goods.setRetailPrice((int) (minRetailPrice * 100));
            goods.setWholesalePrice((int) (minPrice * 100));
            goods.setSellerId(curCustomerId());

			goods.setSubmittype(GoodsSubmitType.seller);

            goods.setHeadImgUrl1(headImgUrls[0]);
            goods.setHeadImgUrl2(headImgUrls[1]);
            goods.setHeadImgUrl3(headImgUrls[2]);
            goods.setHeadImgUrl4(headImgUrls[3]);
            goods.setHeadImgUrl5(headImgUrls[4]);
            goods.setDataPackageUrl1(dataPackageUrl1);
            goods.setDataPackageUrl2(dataPackageUrl2);
            goods.setImgPackageUrl1(imgPackageUrl1);
            goods.setImgPackageUrl2(imgPackageUrl2);
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
            goods = goodsService.add(goods);

            //规格
            Map<String, Integer> specValueToId = new HashMap<>();
//            int sIndex=0;
            for (String value : specList) {
                SkuPropValue skuPropValue = new SkuPropValue();
                skuPropValue.setGoodsCode(goods.getCode());
                skuPropValue.setSkuPropId(SkuPropValueType.spec.getValue());   //规格
                skuPropValue.setValue(value);
//                skuPropValue.setImg(specImgs[sIndex]);
                skuPropValue.setImg("");
                skuPropValue.setCreated(new Date());
                skuPropValue = skuService.saveSkuPropValue(skuPropValue);
                specValueToId.put(value, skuPropValue.getId());
//                sIndex++;
            }

            //颜色
            Map<String, Integer> colorValueToId = new HashMap<>();
            for (String value : colorList) {
                SkuPropValue skuPropValue = new SkuPropValue();
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
                SkuPropValue skuPropValue = new SkuPropValue();
                skuPropValue.setGoodsCode(goods.getCode());
                skuPropValue.setSkuPropId(SkuPropValueType.size.getValue());
                skuPropValue.setValue(value);
                skuPropValue.setImg("");
                skuPropValue.setCreated(new Date());
                skuPropValue = skuService.saveSkuPropValue(skuPropValue);
                sizeValueToId.put(value, skuPropValue.getId());
            }

            for (int index = 0; index < specPropValues.length; index++) {
                Sku sku = new Sku();
                sku.setGoodsCode(goods.getCode());
                StringBuffer properties = new StringBuffer();
                properties.append(SkuPropValueType.spec.getValue() + ":" + specValueToId.get(specPropValues[index]) + ";");
                properties.append(SkuPropValueType.color.getValue() + ":" + colorValueToId.get(colorPropValues[index]) + ";");
                properties.append(SkuPropValueType.size.getValue() + ":" + sizeValueToId.get(sizePropValues[index]) + ";");
                sku.setProperties(properties.toString());
                sku.setImgUrl(goodsImgs[index]);
                sku.setPrice((int) (Double.parseDouble(prices[index]) * 100));
                sku.setStock(Integer.parseInt(stocks[index]));
                sku.setRetailPrice((int) (Double.parseDouble(retailPrices[index]) * 100));
                sku.setWeight(((int) Math.round(Double.parseDouble(weights[index]) * 100)) * 10);
                sku.setCreated(new Date());
                skuService.saveSku(sku);
            }

        } catch (Exception ex) {
            error("保存出错");
            logger.error(ex.getMessage(), ex);
            return;
        }
        ok("添加成功");
    }

    private Boolean repeatCheck(String value, List<String> list) {
        if (list == null) {
            return false;
        }
        for (String item : list) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
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
        String policy = JSON.toJSONString(info);
        policy = Base64.encode(policy, "UTF-8");
        String signature = policy + "&" + Constant.UPYUN_SIGNATURE;
        signature = MD5.md5(signature, "UTF-8");
        param.put("policy", policy);
        param.put("signature", signature);
        return param;
    }

    public void getPolicy() throws Exception {
        String type = getPara("type", "file");
        ok("", policy(type));
    }

	/**
	 * 发布商品 分类选择
	 */
	@ActionKey("fabu")
	public void fabu() {
		Integer classid = getParaToInt("classid");
		Integer goodid = getParaToInt("goodid");
		setAttr("goodid", goodid);
		if (null != classid && classid != 0) {
			setAttr("thirdClass", goodClassService.getById(classid));
		}
		render("fabugoods.html");
	}

	/**
	 * 发布商品 分类选择
	 * 
	 * @throws Exception
	 */
	@ActionKey("add/v2")
	public void newAddGoods() throws Exception {
		Integer classid = getParaToInt("classid");
		if (null == classid) {
			// 分类Id 为空 返回分类选择页面
			render("fabugoods.html");
			return;
		}

		GoodsClass goodsClass = goodClassService.getById(classid);

		if (null != goodsClass) {
			setAttr("thirdclassName", goodsClass.getName());
			setAttr("thirdclassid", goodsClass.getId());

			goodsClass = goodClassService.getById(goodsClass.getPid());

			if (null != goodsClass) {
				setAttr("secondclassName", goodsClass.getName());
				setAttr("secondclassid", goodsClass.getId());

				goodsClass = goodClassService.getById(goodsClass.getPid());

				if (null != goodsClass) {
					setAttr("firstclassName", goodsClass.getName());
					setAttr("firstclassid", goodsClass.getId());
				}
			}
		}

		setAttr("imagePolicy", policy("image"));
		setAttr("filePolicy", policy("file"));

		setAttr("brands", brandService.findAll());
		setAttr("providers", customerService.findProviders());

		List<GoodsClass> list = goodClassService.getChildren(classid);
		for (GoodsClass entity : list) {
			entity.setChildren(goodClassService.getChildren(entity.getId()));
		}

		// 属性
		setAttr("attrs", list);

		render("new_addGoods.html");
	}

	@ActionKey("add2")
	@Tx
	public void add2() {
		Integer status;
		try {
			Integer thirdclassid = getParaToInt("thirdclassid");
			Integer secondclassid = getParaToInt("secondclassid");
			Integer firstclassid = getParaToInt("firstclassid");
			Integer brandid = getParaToInt("brandid");

			status = getParaToInt("status");

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

			if (status == null) {
				error("提交状态错误");
				return;
			}

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
			if (specValues == null) {
				error("请添加规格");
				return;
			}
			List<String> specList = new ArrayList<>();
			for (String specValue : specValues) {
				if (StrKit.isBlank(specValue)) {
					error("规格不能为空");
					return;
				}
				if (repeatCheck(specValue, specList)) {
					error("规格有值重复");
					return;
				} else {
					specList.add(specValue);
				}
			}
			// String[] specImgs = getParaValues("specImg");

			String[] specPropValues = getParaValues("specPropValue");
			String[] colorPropValues = getParaValues("colorPropValue");
			String[] sizePropValues = getParaValues("sizePropValue");
			String[] goodsImgs = getParaValues("goodsImg");
			String[] prices = getParaValues("price");
			String[] stocks = getParaValues("stock");
			String[] retailPrices = getParaValues("retailPrice");
			String[] weights = getParaValues("weight");
			Double minPrice = 999999999d;
			for (String price : prices) {
				if (StrKit.isBlank(price)) {
					error("请填写价格");
					return;
				}
				try {
					Double dPrice = Double.parseDouble(price);
					if (dPrice <= 0) {
						error("请填写合适的价格");
						return;
					}
					if (minPrice > dPrice) {
						minPrice = dPrice;
					}
				} catch (Exception ex) {
					error("请填写合适的价格");
					return;
				}
			}
			Integer sumStock = 0;
			for (String stock : stocks) {
				if (StrKit.isBlank(stock)) {
					error("请填写库存");
					return;
				}
				try {
					Integer intStock = Integer.parseInt(stock);
					if (intStock < 0) {
						error("请填写合适的库存");
						return;
					}
					sumStock += intStock;
				} catch (Exception ex) {
					error("请填写合适的库存");
					return;
				}
			}
			Double minRetailPrice = 999999999d;
			for (String retailPrice : retailPrices) {
				if (StrKit.isBlank(retailPrice)) {
					error("请填写建议零售价");
					return;
				}
				try {
					Double dPrice = Double.parseDouble(retailPrice);
					if (dPrice <= 0) {
						error("请填写合适的建议零售价");
						return;
					}
					if (minRetailPrice > dPrice) {
						minRetailPrice = dPrice;
					}
				} catch (Exception ex) {
					error("请填写合适的建议零售价");
					return;
				}
			}
			Double minWeight = Double.MAX_VALUE;
			for (String weight : weights) {
				if (StrKit.isBlank(weight)) {
					error("请填写重量");
					return;
				}
				try {
					Double dWeight = Double.parseDouble(weight);
					if (dWeight < 0) {
						error("请填写合适的重量");
						return;
					}
					if (minWeight > dWeight) {
						minWeight = dWeight;
					}
				} catch (Exception ex) {
					error("请填写合适的重量");
					return;
				}
			}

			String fitCar = getPara("fitCar", "");

			Integer isSpecial = getParaToInt("isSpecial", 0);
			String comment = getPara("comment", "");

			String[] tempHeadImgUrls = getParaValues("headImgUrl");
			String[] headImgUrls = new String[5];
			boolean hasImg = false;
			int i = 0;
			for (String temp : tempHeadImgUrls) {
				if (StrKit.notBlank(temp)) {
					headImgUrls[i++] = temp;
					hasImg = true;
				}
			}
			if (!hasImg) {
				error("请上传图片");
				return;
			}

			String dataPackageUrl1 = getPara("dataPackageUrl1", "");
			String dataPackageUrl2 = getPara("dataPackageUrl2", "");
			String imgPackageUrl1 = getPara("imgPackageUrl1", "");
			String imgPackageUrl2 = getPara("imgPackageUrl2", "");
			if (StrKit.isBlank(dataPackageUrl1) && StrKit.isBlank(dataPackageUrl2) && StrKit.isBlank(imgPackageUrl1)
					&& StrKit.isBlank(imgPackageUrl2)) {
				error("请上传至少一个图片包或数据包");
				return;
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

			Goods goods = new Goods();

			goods.setThirdclassid(thirdclassid);
			goods.setSecondclassid(secondclassid);
			goods.setFirstclassid(firstclassid);
			goods.setBrandid(brandid);

			Brand brand = brandService.getById(brandid);
			if (null != brand) {
				goods.setBrand(brand.getName());
			}

			goods.setType(GoodsType.valOf(goodsType));

			goods.setStatus(GoodsStatus.valOf(status));

			if (GoodsStatus.online == goods.getStatus()) {
				goods.setOnlineTime(new Date());
			}

			goods.setName(name);
			goods.setItemNo(itemNo);
			goods.setWeight((int) (minWeight * 1000));
			// goods.setColor(color.toString());
			// goods.setSize(size.toString());
			goods.setFitCar(fitCar);
			goods.setStock(sumStock);
			goods.setIsSpecial(isSpecial == 1);
			goods.setComment(comment);
			goods.setRetailPrice((int) (minRetailPrice * 100));
			goods.setWholesalePrice((int) (minPrice * 100));
			goods.setSellerId(curCustomerId());

			goods.setSubmittype(GoodsSubmitType.seller);

			goods.setHeadImgUrl1(headImgUrls[0]);
			goods.setHeadImgUrl2(headImgUrls[1]);
			goods.setHeadImgUrl3(headImgUrls[2]);
			goods.setHeadImgUrl4(headImgUrls[3]);
			goods.setHeadImgUrl5(headImgUrls[4]);
			goods.setDataPackageUrl1(dataPackageUrl1);
			goods.setDataPackageUrl2(dataPackageUrl2);
			goods.setImgPackageUrl1(imgPackageUrl1);
			goods.setImgPackageUrl2(imgPackageUrl2);
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
			goods = goodsService.add(goods);

			// 规格
			Map<String, Integer> specValueToId = new HashMap<>();
			// int sIndex=0;
			for (String value : specList) {
				SkuPropValue skuPropValue = new SkuPropValue();
				skuPropValue.setGoodsCode(goods.getCode());
				skuPropValue.setSkuPropId(SkuPropValueType.spec.getValue()); // 规格
				skuPropValue.setValue(value);
				// skuPropValue.setImg(specImgs[sIndex]);
				skuPropValue.setImg("");
				skuPropValue.setCreated(new Date());
				skuPropValue = skuService.saveSkuPropValue(skuPropValue);
				specValueToId.put(value, skuPropValue.getId());
				// sIndex++;
			}

			// 颜色
			Map<String, Integer> colorValueToId = new HashMap<>();
			for (String value : colorList) {
				SkuPropValue skuPropValue = new SkuPropValue();
				skuPropValue.setGoodsCode(goods.getCode());
				skuPropValue.setSkuPropId(SkuPropValueType.color.getValue()); // 颜色
				skuPropValue.setValue(value);
				skuPropValue.setImg("");
				skuPropValue.setCreated(new Date());
				skuPropValue = skuService.saveSkuPropValue(skuPropValue);
				colorValueToId.put(value, skuPropValue.getId());
			}

			// 尺寸
			Map<String, Integer> sizeValueToId = new HashMap<>();
			for (String value : sizeList) {
				SkuPropValue skuPropValue = new SkuPropValue();
				skuPropValue.setGoodsCode(goods.getCode());
				skuPropValue.setSkuPropId(SkuPropValueType.size.getValue());
				skuPropValue.setValue(value);
				skuPropValue.setImg("");
				skuPropValue.setCreated(new Date());
				skuPropValue = skuService.saveSkuPropValue(skuPropValue);
				sizeValueToId.put(value, skuPropValue.getId());
			}

			for (int index = 0; index < specPropValues.length; index++) {
				Sku sku = new Sku();
				sku.setGoodsCode(goods.getCode());
				StringBuffer properties = new StringBuffer();
				properties.append(
						SkuPropValueType.spec.getValue() + ":" + specValueToId.get(specPropValues[index]) + ";");
				properties.append(
						SkuPropValueType.color.getValue() + ":" + colorValueToId.get(colorPropValues[index]) + ";");
				properties.append(
						SkuPropValueType.size.getValue() + ":" + sizeValueToId.get(sizePropValues[index]) + ";");
				sku.setProperties(properties.toString());
				sku.setImgUrl(goodsImgs[index]);
				sku.setPrice((int) (Double.parseDouble(prices[index]) * 100));
				sku.setStock(Integer.parseInt(stocks[index]));
				sku.setRetailPrice((int) (Double.parseDouble(retailPrices[index]) * 100));
				sku.setWeight(((int) Math.round(Double.parseDouble(weights[index]) * 100)) * 10);
				sku.setCreated(new Date());
				skuService.saveSku(sku);
			}

			for (Integer value : values) {
				GoodsExt goodsExt = new GoodsExt();
				goodsExt.setClassid(value);
				goodsExt.setGoodid(goods.getId());
				goodsExt.setCreated(new Date());
				goodsExtService.save(goodsExt);
			}

			brandService.save(brandid, thirdclassid);

		} catch (Exception ex) {
			error("保存出错");
			logger.error(ex.getMessage(), ex);
			return;
		}
		ok("添加成功", status);
	}

}
