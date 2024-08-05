package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.model.County;
import com.aichebaba.rooftop.model.Province;
import com.aichebaba.rooftop.service.ProvinceService;
import com.jfinal.aop.Tx;
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
public class CountyController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    public void index() {
        Integer provinceId = getParaToInt("provinceId", 0);
        Integer cityId = getParaToInt("cityId", 0);
        PageList<County> pager = provinceService.findCounties(provinceId,
                getPara("provinceName"), cityId, getPara("cityName"),
                getPara("name"), getParaToBoolean("display"),
                getPageParam());
        setAttr("province", provinceService.getProvince(provinceId));
        setAttr("city", provinceService.getCity(cityId));
        List<Province> provinces = provinceService.getAllDisplayProvinces(null);
        setAttr("provinces", provinces);
        List<City> cities = provinceService.getCitiesByProvince(provinceId, null);
        setAttr("cities", cities);
        setAttr("pager", pager);
        render("list.html");
    }


    public void setDisplay() {
        int id = getParaToInt("id", 0);
        provinceService.switchCounty(id);
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
        County county = provinceService.getCounty(id);
        City city = provinceService.getCity(county.getCityId());
        List<City> cities = provinceService.getCitiesByProvince(city.getProvinceId(), null);
        county.setCityName(city.getName());
        Province province = provinceService.getProvince(city.getProvinceId());
        if (province != null) {
            county.setProvinceName(province.getName());
        }
        setAttr("province", province);
        setAttr("cities", cities);
        setAttr("county", county);
        render("add.html");
    }

    @Tx
    public void save() {
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        int cityId = getParaToInt("cityId", 0);
        String pinyin = getPara("pinyin");
        if (StrKit.isBlank(name)) {
            error("请填写名称");
            return;
        }
        if (cityId == 0) {
            error("请选择城市");
            return;
        }
        County county;
        if (id == 0) {
            county = new County();
            county.setDisplay(true);
        } else {
            county = provinceService.getCounty(id);
        }
        county.setName(name);
        county.setCityId(cityId);
        county.setPinyin(pinyin);

        provinceService.saveCounty(county);
        ok("");
    }
}
