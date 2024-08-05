package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.CityDao;
import com.aichebaba.rooftop.dao.CountyDao;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.model.County;
import com.aichebaba.rooftop.model.Province;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/5.
 */
@Service
public class ProvinceService {

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private CountyDao countyDao;

    public PageList<Province> findProvinces(String name, Boolean display, PageParam pageParam) {
        return provinceDao.findPager(name, display, pageParam);
    }

    public void switchProvince(int id) {
        Province province = provinceDao.getById(id);
        if (province != null) {
            province.setDisplay(!province.isDisplay());
            provinceDao.update(province);
        }
    }

    public PageList<City> findCities(int provinceId, String provinceName, String name, Boolean display, PageParam pageParam) {
        return cityDao.findPager(provinceId, provinceName, name, display, pageParam);
    }

    public void switchCity(int id) {
        City city = cityDao.getById(id);
        if (city != null) {
            city.setDisplay(!city.getDisplay());
            cityDao.update(city);
        }
    }

    public PageList<County> findCounties(int provinceId, String provinceName, int cityId, String cityName, String name, Boolean display,
                                         PageParam pageParam) {
        return countyDao.findPager(provinceId, provinceName, cityId, cityName, name, display, pageParam);
    }

    public void switchCounty(int id) {
        County county = countyDao.getById(id);
        if (county != null) {
            county.setDisplay(!county.getDisplay());
            countyDao.update(county);
        }
    }

    public List<Province> getAllDisplayProvinces(Boolean display) {
        List<Province> provinces;
        if (display != null) {
            provinces = provinceDao.findAllDisplay(display);
        } else {
            provinces = provinceDao.findAll();
        }
        Collections.sort(provinces, new Comparator<Province>() {
            @Override
            public int compare(Province o1, Province o2) {
                return o1.getPinyin().compareTo(o2.getPinyin());
            }
        });
        return provinces;
    }

    public City getCity(int id) {
        return cityDao.getById(id);
    }

    public void saveCity(City city) {
        if (city.getId() > 0) {
            cityDao.update(city);
        } else {
            cityDao.add(city);
        }
    }

    public Province getProvince(int id) {
        return provinceDao.getById(id);
    }

    public List<City> getCitiesByProvince(Integer provinceId, Boolean display) {
        if (display != null) {
            return cityDao.findByProvince(provinceId, display);
        } else {
            return cityDao.findByProvince(provinceId);
        }
    }

    public County getCounty(int id) {
        return countyDao.getById(id);
    }

    public County getCountyInfo(int id) {
        County county = getCounty(id);
        if (county != null) {
            City city = getCity(county.getCityId());
            Province p = getProvince(city.getProvinceId());
            county.setProvinceName(p.getName());
            county.setCityName(city.getName());
        }
        return county;
    }

    public void saveCounty(County county) {
        if (county.getId() > 0) {
            countyDao.update(county);
        } else {
            countyDao.add(county);
        }
    }

    public List<County> getCountiesByCity(Integer id, Boolean display) {
        if (display != null) {
            return countyDao.findByCity(id, display);
        } else {
            return countyDao.findByCity(id);
        }
    }

    /*
    public Province findByName(String name) {
        return provinceDao.findByProvince(name);
    }
    */
}
