package com.aichebaba.rooftop.controller.admin;


import java.io.File;
import java.io.IOException;
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
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.BrandClass;
import com.aichebaba.rooftop.model.BrandCustomer;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.GoodsService;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;

/**
 * 品牌管理
 */

@Controller
@Scope("prototype")
public class BrandManagerController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BrandManagerController.class);

    @Autowired
    private BrandService brandService;

    @Autowired
    private GoodClassService goodClassService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CustomerService customerService;
    /**
     * 列表页
     */
    public void index() {
        String brandName = getPara("brandName");
        Date createdFrom = getParaToDate("createdFrom", null);
        Date createdEnd = getParaToDate("createdEnd", null);
        Map<String, Object> params = new HashMap<>();
        params.put("name", brandName);
        params.put("createdFrom", createdFrom);
        params.put("createdEnd", createdEnd);
        PageList<Brand> pager = brandService.findBrands(params, getPageParam());
        for (Brand brand : pager.getData()) {
            BrandCustomer bc = brandService.getBrandCustomerBrandId(brand.getId());
            if (bc != null) {
                brand.setCustomer(customerService.getById(bc.getCustomerId()));
            }
        }
        setAttr("pager", pager);
        render("list.html");
    }

    /**
     * 添加页
     */
    public void add() {
        render("edit.html");
    }

    /**
     * 添加页
     */
    public void edit() {
        int id = getParaToInt("id");
        Brand brand = brandService.getById(id);
        setAttr("brand", brand);
        List<GoodsClass> goodsClasses = goodClassService.findGoodsClassByBrandId(id);
        setAttr("class", goodsClasses);
        render("edit.html");
    }

    /**
     * 详情页
     */
    public void detail() {
        int id = getParaToInt("id");
        Brand brand = brandService.getById(id);
        setAttr("brand", brand);
        List<GoodsClass> goodsClasses = goodClassService.findGoodsClassByBrandId(id);
        setAttr("class", goodsClasses);
        render("detail.html");
    }

    /**
     * 保存品牌数据
     */
    @Tx
    public void save() throws IOException{
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        String pinyin = getPara("pinyin");
        UploadFile file = getFile("logo");
        String comment = getPara("comment");
        int customerId = getParaToInt("customerId", 0);

        Brand brand;

        if(id == 0){
            //新添

//            //品牌名称重复判断
//            Brand reBrand = brandService.getByName(name);
//            if(reBrand != null){
//                error("该品牌名称已存在");
//                return;
//            }
//            //图片存在判断
//            if(file == null){
//                error("请选择图片");
//            }

            brand = new Brand();
        }else{
            //修改

            brand = brandService.getById(id);
        }

        //上传图片
        if (file != null) {
            String tempImgUrl = file.getSaveDirectory() + File.separator + file.getFileName();
            File temp = new File(tempImgUrl);
            Long fileSize = temp.length();
            if (fileSize > Constant.MAX_IMAGE_SIZE) {
                error("图片文件太大,请重新上传！");
                return;
            }
            String upFile = PicUpload.picUpload(tempImgUrl);
            if (StrKit.isBlank(upFile)) {
                error("图片上传失败,请重新上传！");
                return;
            }
            brand.setLogo(upFile);
        }

        //如果品牌名称修改，商品表内对应的品牌名也修改
        if (brand.getId() != 0 && !brand.getName().equals(name)) {
            goodsService.updateBrandName(brand.getId(), name);
        }

        brand.setName(name);
        brand.setPinyin(pinyin.toUpperCase());
        brand.setComment(comment);
        brand = brandService.save(brand);

        if(id == 0 && customerId != 0){
            //添加供货商-品牌关联
            brandService.saveBrandCustomer(brand.getId(), customerId);
        }

        ok("保存成功");
    }

    /**
     * 删除品牌-分类关联
     */
    public void delBrandClass() {
        int brandId = getParaToInt("brandId", 0);
        int classId = getParaToInt("classId", 0);
        BrandClass brandClass = brandService.getBrandClassBy2Id(brandId, classId);
        if (brandClass != null) {
            brandService.delBrandClassById(brandClass.getId());
        }
        ok("删除成功");
    }

    /**
     * 删除品牌
     */
    @Tx
    public void delBrand(){
        int brandId = getParaToInt("brandId", 0);
        Boolean result = brandService.delBrand(brandId);
        if (result) {
            ok("删除成功");
        } else {
            error("该品牌旗下有商品，无法删除");
        }
    }

	/**
	 * 置顶设置页
	 */
	public void toplist() {
		setAttr("firstclasslist", goodClassService.getChildren(0));
		// 品牌名称
		String name = getPara("name");

		Integer firstclassid = getParaToInt("firstclassid");
		if (null != firstclassid) {
			setAttr("secondclasslist", goodClassService.getChildren(firstclassid));
		}

		Integer secondclassid = getParaToInt("secondclassid");
		if (null != secondclassid) {
			setAttr("thirdclasslist", goodClassService.getChildren(secondclassid));
		}

		Integer thirdclassid = getParaToInt("thirdclassid");
		if (null != thirdclassid) {
			setAttr("fourthclasslist", goodClassService.getChildren(thirdclassid));
		}

		Integer top = getParaToInt("top");

		PageList<Brand> pager = brandService.findBrands(firstclassid,
														secondclassid,
														thirdclassid,
														name,
														top,
														getPageParam());
		setAttr("pager", pager);

		render("pinpai_shizhi.html");
	}

	/**
	 * 保存置顶
	 */
	@ActionKey("savetop")
	public void savetop() {
		logger.info("保存置顶品牌数量{}", brandService.updateTopStatus(Brand.TOP_STATE_TOP, Brand.TOP_STATE_WAIT_TOP));
		logger.info("保存取消置顶品牌数量{}",
				brandService.updateTopStatus(Brand.TOP_STATE_NO_TOP, Brand.TOP_STATE_WAIT_CANCEL_TOP));
		ok("保存成功");
	}

	/**
	 * 置顶
	 */
	@ActionKey("settop")
	public void settop() {
		Integer id = getParaToInt("id");
		Integer thirdclassid = getParaToInt("thirdclassid");
		BrandClass brandClass = brandService.getBrandClassBy2Id(id, thirdclassid);

		switch (brandClass.getTop()) {
		case Brand.TOP_STATE_TOP:
			break;
		case Brand.TOP_STATE_WAIT_CANCEL_TOP:
			brandService.updateTopStatusByIdAndThirdclassid(id, thirdclassid, Brand.TOP_STATE_TOP);
			break;
		case Brand.TOP_STATE_WAIT_TOP:
			break;
		case Brand.TOP_STATE_NO_TOP:
			brandService.updateTopStatusByIdAndThirdclassid(id, thirdclassid, Brand.TOP_STATE_WAIT_TOP);
			break;
		default:
			break;
		}

		ok("置顶成功");
	}

	/**
	 * 取消置顶
	 */
	@ActionKey("canceltop")
	public void canceltop() {
		Integer id = getParaToInt("id");
		Integer thirdclassid = getParaToInt("thirdclassid");
		BrandClass brandClass = brandService.getBrandClassBy2Id(id, thirdclassid);

		switch (brandClass.getTop()) {
		case Brand.TOP_STATE_TOP:
			brandService.updateTopStatusByIdAndThirdclassid(id, thirdclassid, Brand.TOP_STATE_WAIT_CANCEL_TOP);
			break;
		case Brand.TOP_STATE_WAIT_CANCEL_TOP:
			break;
		case Brand.TOP_STATE_WAIT_TOP:
			brandService.updateTopStatusByIdAndThirdclassid(id, thirdclassid, Brand.TOP_STATE_NO_TOP);
			break;
		case Brand.TOP_STATE_NO_TOP:
			break;
		default:
			break;
		}

		ok("取消置顶成功");
	}
}
