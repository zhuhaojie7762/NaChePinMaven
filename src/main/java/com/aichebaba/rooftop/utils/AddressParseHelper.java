package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.dao.CityDao;
import com.aichebaba.rooftop.dao.CountyDao;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.model.City;
import com.aichebaba.rooftop.model.County;
import com.aichebaba.rooftop.model.Province;
import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 京东、淘宝、1688 订单地址解析
 * @author He Daoyuan
 */
public class AddressParseHelper {
    private static final String regMobileNumber = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(14[0,0-9])|(17[0,0-9]))\\d{8}$";
    //private static final String regPhoneNumber = "([0-9]{3,4}-)?[0-9]{7,8}";^(((0\d{3}[\-])?\d{7}|(0\d{2}[\-])?\d{8}))([\-]\d{2,4})?$
    private static final String regPhoneNumber = "^(((0\\d{3}[\\-])?\\d{7}|(0\\d{2}[\\-])?\\d{8}))([\\-]\\d{2,4})?$";
    private static final String regPostcode = "^[1-9][0-9]{5}$";
    private String addressStr;
    private JSONObject json = new JSONObject();

    public AddressParseHelper (String addressStr) {
        this.addressStr = addressStr;
    }

    public JSONObject getAddressJson1688() {
        parse1688(addressStr);
        return json;
    }

    // 何道元,15258876727,0710-3012189,浙江省 杭州市 余杭区 五常街道 五常西溪软件园金牛座A3001 ,311100    注.地址中的逗号存在中文和英文状态二种情况
    public JSONObject getAddressJsonTaoBao() {
        // 中文逗号
        if (addressStr.contains("，")) {
            parseTaoBao(addressStr, "，");
            return json;
        }
        // 英文逗号
        if (addressStr.contains(",")) {
            parseTaoBao(addressStr, ",");
            return json;
        }
        json.put("check", "fail");
        return json;
    }

    private void parseTaoBao(String str, String sign) {
        if (!str.contains(sign)) {
            json.put("check", "fail");  // 校验json是否解析成功
            return;
        }
        // 解析邮编
        int a = str.lastIndexOf(sign);
        String postCode = str.substring(a + 1);
        json.put("postCode", postCode.trim());

        // 解析省、市、区(编码)和地址
        str = str.substring(0, a);
        int b = str.lastIndexOf(sign);
        String[] addresses = str.substring(b + 1).split(" ");
        String address = "";
        if (getAddresses(address, addresses)) {
            json.put("check", "fail");
            return;
        }

        // 解析姓名、手机号码、电话号码
        str = str.substring(0, b);
        String[] addressInfo = str.split(sign);
        getInfo(addressInfo);
        json.put("check", "success");
    }

    // 刘登, 13871703997, 020-62845373, 湖北省 襄阳市 樊城区 襄阳市白鹤市场 (441000)
    private void parse1688(String str) {
        if (!str.contains(",") || !str.contains(" (")) {
            json.put("check", "fail");
            return;
        }
        String partOne = str.substring(0, str.lastIndexOf(","));
        // 解析姓名、手机号码、电话号码
        String[] partOneS = partOne.replace(" ", "").split(",");
        getInfo(partOneS);

        String partTwo = str.substring(str.lastIndexOf(",") + 2);
        // 解析省、市、区(编码)和地址
        String[] partTwos = partTwo.substring(0, partTwo.lastIndexOf(" (")).split(" ");
        String address = "";
        if (getAddresses(address, partTwos)) {
            json.put("check", "fail");
            return;
        }

        // 解析邮编
        String postCode = partTwo.substring(partTwo.lastIndexOf(" (")).replace("(", "").replace(")", "");
        json.put("postCode", postCode.trim());

        json.put("check", "success");
    }

    private void getInfo(String... addresses) {
        json.put("name", addresses[0]);
        for (int i = 1; i < addresses.length; i++) {
            if (regEx(addresses[i].trim(), regMobileNumber)) {
                json.put("mobileNumber", addresses[i].trim());
                continue;
            }
            if (regEx(addresses[i].trim(), regPhoneNumber)) {
                String[] number = addresses[i].split("-");
                for (int z = 0; z < number.length; z++) {
                    json.put("phoneNumber" + z, number[z].trim());
                }
                continue;
            }
        }
    }

    private boolean getAddresses(String address, String... strs) {
        Province p = null;
        City city = null;
        for (int j = 0; j < strs.length; j++) {
            if (j == 0) {
                String pro = strs[j].substring(0, 2);
                p = new ProvinceDao().findByProvince(pro);
                if (p == null) {
                    return true;
                }
                json.put("province", p.getName());
                json.put("provinceId", p.getId());
                continue;
            }
            if (j == 1) {
                city = new CityDao().findByCity(p.getId(), strs[j]);
                if (city == null) {
                    return true;
                }
                json.put("city", city.getName());
                json.put("cityId", city.getId());
                continue;
            }
            if (j == 2) {
                County reg = new CountyDao().findByCounty(city.getId(), strs[j]);
                if (reg == null) {
                    return true;
                }
                json.put("region", reg.getName());
                json.put("regionId", reg.getId());
                continue;
            }
            address += strs[j];
        }
        json.put("address", address.trim());
        return false;
    }

    private boolean regEx(String str, String regEx) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public void setAddressStr(String addressStr) {
        this.addressStr = addressStr;
    }

    public AddressParseHelper () {}
}
