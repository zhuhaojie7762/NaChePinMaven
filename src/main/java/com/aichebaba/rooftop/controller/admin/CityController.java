package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.model.Province;
import com.aichebaba.rooftop.service.ProvinceService;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/5.
 */
@Controller
@Scope("prototype")
public class CityController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    public void index() {
        List<Province> provinces = provinceService.getAllDisplayProvinces(null);
        setAttr("provinces", provinces);
        Integer provinceId = getParaToInt("provinceId", 0);
        PageList<City> pager = provinceService.findCities(provinceId, getPara("provinceName"), getPara("name"), getParaToBoolean("display"),
                getPageParam());
        Province province = provinceService.getProvince(provinceId);
        setAttr("province", province);
        setAttr("pager", pager);
        render("list.html");
    }

    public void setDisplay() {
        int id = getParaToInt("id", 0);
        provinceService.switchCity(id);
        ok("");
    }

    public void add() {
        List<Province> provinces = provinceService.getAllDisplayProvinces(null);
        setAttr("provinces", provinces);
    }

    public void edit() {
        int id = getParaToInt("id", 0);
        List<Province> provinces = provinceService.getAllDisplayProvinces(null);
        setAttr("provinces", provinces);
        City city = provinceService.getCity(id);
        Province province = provinceService.getProvince(city.getProvinceId());
        if (province != null) {
            city.setProvinceName(province.getName());
        }
        setAttr("city", city);
        render("add.html");
    }

    public void save() {
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        int provinceId = getParaToInt("provinceId", 0);
        String pinyin = getPara("pinyin");
        if (StrKit.isBlank(name)) {
            error("请填写名称");
            return;
        }
        if (provinceId == 0) {
            error("请选择省份");
            return;
        }
        City city;
        if (id == 0) {
            city = new City();
            city.setDisplay(true);
        } else {
            city = provinceService.getCity(id);
        }
        city.setName(name);
        city.setProvinceId(provinceId);
        city.setPinyin(pinyin);

        provinceService.saveCity(city);
        ok("");
    }

    public void items() {
        int id = getParaToInt("id", 0);
        List<City> cities = provinceService.getCitiesByProvince(id, null);
        setAttr("cities", cities);
        String html = TemplateUtils.html("admin/city/items", getRequest());

        success(html);
    }
}
