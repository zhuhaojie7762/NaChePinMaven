package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.Banner;
import com.aichebaba.rooftop.model.enums.BannerType;
import com.aichebaba.rooftop.service.BannerService;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class SettingsController extends BaseController {

    @Autowired
    private BannerService bannerService;

    /**
     * 首页设置
     */
    public void homePage(){
        Map<String, Object> params = new HashMap<>();

        //首页主Banner
        params.put("type", BannerType.main);
        List<Banner> mainBanners = bannerService.findBanner(params);
        setAttr("mainBanners", mainBanners);

        //坐垫/座套
        params.clear();
        params.put("type", BannerType.cushion);
        List<Banner> cushionBanners = bannerService.findBanner(params);
        if (cushionBanners.size() > 0) {
            setAttr("cushionBanners", cushionBanners.get(0));
        }

        //脚垫/后备箱垫
        params.clear();
        params.put("type", BannerType.floorMat);
        List<Banner> floorMatBanners = bannerService.findBanner(params);
        if (floorMatBanners.size() > 0) {
            setAttr("floorMatBanners", floorMatBanners.get(0));
        }

        //方向盘套
        params.clear();
        params.put("type", BannerType.wheelCover);
        List<Banner> wheelCoverBanners = bannerService.findBanner(params);
        if (wheelCoverBanners.size() > 0) {
            setAttr("wheelCoverBanners", wheelCoverBanners.get(0));
        }

        //功能小件
        params.clear();
        params.put("type", BannerType.carTrim);
        List<Banner> carTrimBanners = bannerService.findBanner(params);
        if (carTrimBanners.size() > 0) {
            setAttr("carTrimBanners", carTrimBanners.get(0));
        }

        render("homePage.html");
    }

    /**
     * 保存主Banner
     */
    @Tx
    public void saveBanner() throws IOException{
        Integer[] ids = getParaValuesToInt("id");
        String[] urls = getParaValues("url");
        String[] types = getParaValues("type");
        String[] imageName = getParaValues("imageName");
        List<Integer> notInIds = new ArrayList<>();

        List<Banner> bannerList = new ArrayList<>();
        int sort = 0;
        for(int i = 0; i < imageName.length; i++){
            Banner banner;
            if(ids[i] != null && ids[i] > 0){
                banner = bannerService.getById(ids[i]);
                notInIds.add(ids[i]);
            }else{
                banner = new Banner();
            }
            banner.setUrl(urls[i]);
            BannerType type = BannerType.valueOf(types[i]);
            banner.setType(type);
            if(type.equals(BannerType.main)){
                banner.setSort(sort);
                sort++;
            }else{
                banner.setSort(0);
            }
            UploadFile file = getFile(imageName[i]);
            if (file != null) {
                String tempFileUrls = file.getSaveDirectory() + File.separator + file.getFileName();
                String upFile = PicUpload.picUpload(tempFileUrls);
                if (StrKit.isBlank(upFile)) {
                    error("图片上传失败,请重新上传！");
                    return;
                }
                banner.setImage(upFile);
            }else if(banner.getId() == 0){
                error("请上传图片");
                return;
            }
            bannerList.add(banner);
        }
        //删除无用数据
        bannerService.delOtherIds(notInIds);

        //保存页面数据
        for(Banner item : bannerList){
            bannerService.save(item);
        }
        ok("保存成功");
    }
}
