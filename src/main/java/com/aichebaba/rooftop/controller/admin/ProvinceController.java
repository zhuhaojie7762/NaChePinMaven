package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Province;
import com.aichebaba.rooftop.service.ProvinceService;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * @auther huwhy
 * @date 2016/5/5.
 */
@Controller
@Scope("prototype")
public class ProvinceController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    public void index() {
        PageList<Province> pager = provinceService.findProvinces(getPara("name"), getParaToBoolean("display"),
                getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    public void setDisplay() {
        int id = getParaToInt("id", 0);
        provinceService.switchProvince(id);
        ok("");
    }
}
