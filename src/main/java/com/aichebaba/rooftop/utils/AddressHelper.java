package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.dao.CityDao;
import com.aichebaba.rooftop.dao.CountyDao;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.model.County;
import com.aichebaba.rooftop.model.Province;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andy on 2016/8/8.
 */
public class AddressHelper {
    private String name;
    private String mobileNumber;
    private String phoneNumber0;
    private String phoneNumber1;
    private String phoneNumber2;
    private String province;
    private int provinceId;
    private String city;
    private int cityId;
    private String region;
    private int regionId;
    private String address;
    private String postCode;

    //static final String str = "山东省潍坊市安丘市 兴安街道 南苑路振宇汽车城东门，262200，姜宏杉， 13465361370";
    public String toUserInfo(String str) {
        str = str.trim();
        String temp = str.substring(0, 2);
        Province province = new ProvinceDao().findByProvince(temp);
        if (province == null) {
            return "省格式有误";
        }
        this.province = province.getName();
        this.provinceId = province.getId();

        int provinceSize = getStrSize();
        String town = str.substring(provinceSize, provinceSize + 3);
        City city = new CityDao().findByCity(province.getId(), town);
        if (city == null) {
            return "市格式有误";
        }
        this.city = city.getName();
        this.cityId = city.getId();

        int citySize = provinceSize + 3;
        String county = str.substring(citySize, citySize + 2);
        County region = new CountyDao().findByCounty(city.getId(), county);
        if (region == null) {
            return "区格式有误";
        }
        this.region = region.getName();
        this.regionId = region.getId();

        String address = str.substring(str.indexOf(" ") + 1, str.indexOf("，"));
        this.address = address;

        String[] info = str.substring(str.indexOf("，") + 1).split("，");
        this.postCode = info[0];
        this.name = info[1];
        this.mobileNumber = info[2];

        return "success";
    }

    private int getStrSize() {
        String[] a = "北京,上海,天津,重庆,台湾,广西壮族自治区,黑龙江省,内蒙古自治区,宁夏回族自治区,新疆维吾尔自治区,西藏自治区,香港特别行政区,澳门特别行政区".split(",");
        for (String b : a) {
            if (b.startsWith(province)) {
                return b.length();
            }
        }
        return 3;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPhoneNumber0() {
        return phoneNumber0;
    }

    public void setPhoneNumber0(String phoneNumber0) {
        this.phoneNumber0 = phoneNumber0;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
