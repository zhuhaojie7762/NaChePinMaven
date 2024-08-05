package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.AccountPayeeStatus;
import com.aichebaba.rooftop.model.enums.CustomerState;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.DateUtil;
import com.google.common.base.Joiner;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

@Controller
@Scope("prototype")
public class CustomerController extends BaseController{

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AccountPayeeService accountPayeeService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private BrandService brandService;

    /**
     * 列表页
     */
    public void index() {
        String stateName = getPara("state", "");
        String phone = getPara("phone");
        String name = getPara("name");
        String typeName = getPara("type", "");
        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");
        CustomerState state = null;
        if (StrKit.notBlank(stateName)) {
            state = CustomerState.valueOf(stateName);
        }
        CustomerType type = null;
        if(StrKit.notBlank(typeName)){
            type = CustomerType.valueOf(typeName);
        }
        PageList<Customer> pager = customerService.findCustomers(state, phone, name, type, startTime, endTime, null, getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    /**
     * 详情页
     */
    public void detail() {
        int id = getParaToInt("id", 0);
        Customer customer = customerService.getById(id);
        setAttr("customer", customer);
        List<Brand> brands = brandService.findBrandByCustomerId(id);
        setAttr("brands", brands);
    }

    /**
     * 重置密码
     * @throws Exception
     */
    public void resetPassword() throws Exception {
        int id = getParaToInt("id", 0);
        customerService.resetPassword(id, getCurUserId());
        ok("操作成功");
    }

    /**
     * 停用客户
     */
    public void stop() {
        int id = getParaToInt("id", 0);
        customerService.stopCustomer(id, getCurUserId());
        ok("操作成功");
    }

    /**
     * 启用客户
     */
    public void enable() {
        int id = getParaToInt("id", 0);
        customerService.enableCustomer(id, getCurUserId());
        ok("操作成功");
    }

    public void resetPickCode() {
        int id = getParaToInt("id", 0);
        String code = getPara("code");
        if (StrKit.isBlank(code) || code.length() > 10) {
            error("请输入编码");
            return;
        }
        customerService.resetPickCode(id, code, getCurUserId());
        ok("操作成功");
    }

    /**
     * 编辑客户页
     */
    public void edit() {
        int id = getParaToInt("id");
        Customer customer = customerService.getById(id);
        setAttr("customer", customer);

        List<Province> provinces = provinceService.getAllDisplayProvinces(true);
        setAttr("provinces", provinces);
        List<City> cities = provinceService.getCitiesByProvince(customer.getProvinceId(), true);
        setAttr("cities", cities);
        List<County> counties = provinceService.getCountiesByCity(customer.getCityId(), true);
        setAttr("counties", counties);

//        Set<String> inGoodsTypes = new HashSet<>();
//        if(StrKit.notBlank(customer.getInGoodsTypes())){
//            String[] goodsTypes = customer.getInGoodsTypes().split(";");
//            for (String sign : goodsTypes) {
//                inGoodsTypes.add(sign);
//            }
//        }
//        setAttr("inGoodsTypes", inGoodsTypes);
//
//        Set<String> outGoodsTypes = new HashSet<>();
//        if(StrKit.notBlank(customer.getOutGoodsTypes())){
//            String[] goodsTypes = customer.getOutGoodsTypes().split(";");
//            for (String sign : goodsTypes) {
//                outGoodsTypes.add(sign);
//            }
//        }
//        setAttr("outGoodsTypes", outGoodsTypes);

        List<String> typeNum = new ArrayList<>();
        typeNum.add("淘宝");
        typeNum.add("京东");
        typeNum.add("普通");
        typeNum.add("其他");
        setAttr("typeNum", typeNum);

        Set<String> signs = new HashSet<>();
        if (!StrKit.isBlank(customer.getSign())) {
            String[] remark = customer.getSign().split(";");
            for (String sign : remark) {
                signs.add(sign);
            }
        }
        setAttr("signs", signs);

        List<Brand> brands = brandService.findBrandByCustomerId(id);
        setAttr("brands", brands);
    }

    /**
     * 保存客户信息
     */
    public void save() {
        int id = getParaToInt("id");
        Customer customer = customerService.getById(id);

        //客户手机号码
        String phone = getPara("phone");
        customer.setPhone(phone);

        //省市区
        int provinceId = getParaToInt("provinceId", 0);
        int cityId = getParaToInt("cityId", 0);
        int countyId = getParaToInt("countyId", 0);
        customer.setProvinceId(provinceId);
        customer.setCityId(cityId);
        customer.setCountyId(countyId);
        County county = provinceService.getCountyInfo(countyId);
        if(county != null){
            customer.setProvince(county.getProvinceName());
            customer.setCity(county.getCityName());
            customer.setArea(county.getName());
        }
        //详细地址
        String address = getPara("address");
        customer.setAddress(address);

        //客户邮箱
        String email = getPara("email");
        customer.setEmail(email);

        //客户姓名
        String name = getPara("name");
        customer.setName(name);

        //商家权重
        int weighting = getParaToInt("weighting", 0);
        customer.setWeighting(weighting);

        //淘宝店网址
        String shopUrl = getPara("shopUrl");
        customer.setShopUrl(shopUrl);

        //旺旺(店铺)
        String wangwang = getPara("wangwang");
        customer.setWangwang(wangwang);

        //QQ
        String qq = getPara("qq");
        customer.setQq(qq);

        //进货商品类型
        String[] inGoodsTypes = getParaValues("inGoodsType");
        if (inGoodsTypes != null && inGoodsTypes.length > 0) {
            customer.setInGoodsType(Joiner.on(";").join(inGoodsTypes));
        }else{
            customer.setInGoodsType("");
        }

        //支付宝账号
        String alipayCode = getPara("alipayCode");
        customer.setAlipayCode(alipayCode);

        //财付通账号
        String tenpayCode = getPara("tenpayCode");
        customer.setTenpayCode(tenpayCode);

        //进货商客户标签类型
        String[] signs = getParaValues("sign");
        if (signs != null && signs.length > 0) {
            customer.setSign(Joiner.on(";").join(signs));
        }else{
            customer.setSign("");
        }

        //公司名称
        String supplierCompany = getPara("supplierCompany");
        customer.setSupplierCompany(supplierCompany);

        //供应商法人代表
        String artificialPerson = getPara("artificialPerson");
        customer.setArtificialPerson(artificialPerson);

        //产品品牌
        String brand = getPara("brand");
        customer.setBrand(brand);

        //供货商备注
        String remarks = getPara("remarks", "");
        customer.setRemarks(remarks);

        //供货商营业执照
        UploadFile file = getFile("businessLicence");
        if (file != null) {
            String localFile = file.getSaveDirectory() + File.separator + file.getFileName();
            if (!checkFileType(localFile)) {

            }
            String upFile;
            try {
                upFile = PicUpload.picUpload(localFile);
            }catch(IOException ex){
                redirect("/admin/customer/edit?id=" + id);
                return;
            }
            if (StrKit.isBlank(upFile)) {
                redirect("/admin/customer/edit?id=" + id);
                return;
            }
            customer.setBusinessLicence(upFile);
        }

        //紧急联系人
        String emergencyContact = getPara("emergencyContact");
        customer.setEmergencyContact(emergencyContact);

        //紧急联系人电话
        String emergencyPhone = getPara("emergencyPhone");
        customer.setEmergencyPhone(emergencyPhone);

        //供货商商品类别
        String[] outGoodsTypes = getParaValues("outGoodsType");
        if (outGoodsTypes != null && outGoodsTypes.length > 0) {
            customer.setOutGoodsType(Joiner.on(";").join(outGoodsTypes));
        }else{
            customer.setOutGoodsType("");
        }

        //是否是生产厂商
        Boolean isProducer = getParaToBoolean("isProducer");
        customer.setIsProducer(isProducer);

        //取货地址
        String pickAddress = getPara("pickAddress");
        customer.setPickAddress(pickAddress);

        //修改商品权重
        goodsService.updateGoodsByWeight(id, weighting);

        //修改客户信息
        customerService.updateCustomer(customer);
        redirect("/admin/customer");
    }

    private Boolean checkFileType(String fileName) {
        List<String> postfixs = new ArrayList<String>() {{
            add("jpg");
            add("gif");
            add("jpeg");
            add("bmp");
            add("png");
        }};
        int pos = fileName.lastIndexOf(".");
        String postfix = fileName.substring(pos + 1);
        for (String item : postfixs) {
            if (item.equals(postfix.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    //供货商
    @Tx
    public void exportToExcelSeller() throws UnsupportedEncodingException, ParseException {

        String codes = getPara("codes");
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        String[] code = codes.split(";");
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, code);
        List<Customer> list = customerService.getCustomerByCodes(codeList);

        Date from1 = new Date();
        Date to1 = new Date();
        List<Customer> sellerList = new ArrayList<>();
        List<Date> date = new ArrayList<>();
        for(Customer cus : list){
            if(cus.getType() == CustomerType.seller) {
                sellerList.add(cus);
                date.add(cus.getCreated());
            }
        }
        if(!sellerList.isEmpty() || sellerList.size() != 0){
            Collections.sort(date);
            from1 = date.get(0);
            to1 = date.get(date.size()-1);
        }

        Date createdFrom;
        String createdFromV = getPara("startTime");
        if (StrKit.notBlank(createdFromV)) {
            createdFrom = DateUtils.parseDate(createdFromV, "yyyy/MM/dd HH:mm");
        } else {
            createdFrom = DateUtil.getDayStart(from1);
        }
        Date createdEnd;
        String createdEndV = getPara("endTime");
        if (StrKit.notBlank(createdEndV)) {
            createdEnd = DateUtils.parseDate(createdEndV, "yyyy/MM/dd HH:mm");
        } else {
            createdEnd = DateUtil.getDayEnd(to1);
        }
        String from = DateFormatUtils.format(createdFrom, "yyyy/MM/dd");
        String to = DateFormatUtils.format(createdEnd, "yyyy/MM/dd");

        if (sellerList.isEmpty()) {
            renderText("没有选中数据");
        } else {
            setAttr("from", from);
            setAttr("to", to);
            setAttr("items", sellerList);
            excelRender("provider.xls", "供货商管理报表" + from + "-" + to + ".xls");
        }
    }

    //进货商
    @Tx
    public void exportToExcelBuyer() throws UnsupportedEncodingException, ParseException {

        String codes = getPara("codes");
        String[] code = codes.split(";");
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, code);
        List<Customer> list = customerService.getCustomerByCodes(codeList);

        Date from1 = new Date();
        Date to1 = new Date();
        List<Customer> buyerList = new ArrayList<>();
        List<Date> date = new ArrayList<>();
        for(Customer cus : list){
            if(cus.getType() == CustomerType.buyer){
                buyerList.add(cus);
                date.add(cus.getCreated());
            }
        }

        if(!buyerList.isEmpty() || buyerList.size() != 0){
            Collections.sort(date);
            from1 = date.get(0);
            to1 = date.get(date.size()-1);
        }
        Date createdFrom;
        String createdFromV = getPara("startTime");
        if (StrKit.notBlank(createdFromV)) {
            createdFrom = DateUtils.parseDate(createdFromV, "yyyy/MM/dd HH:mm");
        } else {
            createdFrom = DateUtil.getDayStart(from1);
        }
        Date createdEnd;
        String createdEndV = getPara("endTime");
        if (StrKit.notBlank(createdEndV)) {
            createdEnd = DateUtils.parseDate(createdEndV, "yyyy/MM/dd HH:mm");
        } else {
            createdEnd = DateUtil.getDayEnd(to1);
        }
        String from = DateFormatUtils.format(createdFrom, "yyyy/MM/dd");
        String to = DateFormatUtils.format(createdEnd, "yyyy/MM/dd");

        if (buyerList.isEmpty()) {
            renderText("没有选中数据");
        } else {
            setAttr("from", from);
            setAttr("to", to);
            setAttr("items", buyerList);
            excelRender("buyer.xls", "进货商管理报表" + from + "-" + to + ".xls");
        }
    }

    //客户导出
    @Tx
    public void exportToExcel() throws UnsupportedEncodingException, ParseException {
        String codes = getPara("codes");
        String[] code = codes.split(";");
        List<String> codeList = new ArrayList<>();
        Collections.addAll(codeList, code);
        List<Customer> list = customerService.getCustomerByCodes(codeList);
        List<Date> date = new ArrayList<>();

        for (Customer cus : list) {
            date.add(cus.getCreated());
            List<Brand> brands = brandService.findBrandByCustomerId(cus.getId());
            if (brands.size() > 0) {
                StringBuilder brandName = new StringBuilder();
                for (Brand brand : brands) {
                    brandName.append(brand.getName()).append(";");
                }
                cus.setBrand(brandName.toString());
            } else {
                cus.setBrand("");
            }
        }

        Date from1 = new Date();
        Date to1 = new Date();
        Collections.sort(date);
        from1 = date.get(0);
        to1 = date.get(date.size()-1);

        Date createdFrom;
        String createdFromV = getPara("startTime");
        if (StrKit.notBlank(createdFromV)) {
            createdFrom = DateUtils.parseDate(createdFromV, "yyyy/MM/dd HH:mm");
        } else {
            createdFrom = DateUtil.getDayStart(from1);
        }
        Date createdEnd;
        String createdEndV = getPara("endTime");
        if (StrKit.notBlank(createdEndV)) {
            createdEnd = DateUtils.parseDate(createdEndV, "yyyy/MM/dd HH:mm");
        } else {
            createdEnd = DateUtil.getDayEnd(to1);
        }
        String from = DateFormatUtils.format(createdFrom, "yyyy/MM/dd");
        String to = DateFormatUtils.format(createdEnd, "yyyy/MM/dd");

        if (list.isEmpty()) {
            renderText("没有选中数据");
        } else {
            setAttr("from", from);
            setAttr("to", to);
            setAttr("items", list);
            excelRender("customer.xls", "客户管理报表" + from + "-" + to + ".xls");
        }
    }

    /**
     * 供货商收款帐号审核
     */
    public void goPayeeCheck() {
        AccountPayee ap = new AccountPayee();
        PageList<AccountPayee> pageList = accountPayeeService.findPayeePageList(ap, getPageParam());

        setAttr("pages", pageList);
        render("payeeCheck.html");
    }

    // 审核
    public void doCheck() {
        Integer id = getParaToInt("id");
        Integer statusCode = getParaToInt("statusCode");

        AccountPayee ap = accountPayeeService.findById(id);
        if(statusCode == 1) {
            ap.setRemark("");
        }
        if (statusCode == 2) {
            ap.setRemark(getPara("remark"));
        }
        ap.setStatus(statusCode == 1 ? AccountPayeeStatus.PASS : AccountPayeeStatus.REJECT);
        accountPayeeService.update(ap);

        if (statusCode == 1) {
            ok("审核通过");
        } else {
            ok("审核不通过");
        }
    }

    // 查询
    public void payeeQuery() {
        Integer checkId = getParaToInt("checkId");

        AccountPayee queryAp = new AccountPayee();
        queryAp.setUserName(getPara("userName"));
        queryAp.setName(getPara("name"));
        queryAp.setCompany(getPara("company"));
        if (checkId == 0) {
            queryAp.setStatus(AccountPayeeStatus.PENDING);
        }
        if (checkId == 1) {
            queryAp.setStatus(AccountPayeeStatus.PASS);
        }
        if (checkId == 2) {
            queryAp.setStatus(AccountPayeeStatus.REJECT);
        }
        PageList<AccountPayee> pageList = accountPayeeService.findPayeePageList(queryAp, getPageParam());

        setAttr("queryAp", queryAp);
        setAttr("pages", pageList);
        render("payeeCheck.html");
    }

    public void change(){
        String stateName = getPara("state", "");
        String phone = getPara("phone");
        String name = getPara("name");
//        String typeName = getPara("type", "");
        Date startTime = getParaToDate("startTime");
        Date endTime = getParaToDate("endTime");
        String settlementMethod = getPara("settlementMethod");
        CustomerState state = null;
        if (StrKit.notBlank(stateName)) {
            state = CustomerState.valueOf(stateName);
        }
//        CustomerType type = null;
//        if(StrKit.notBlank(typeName)){
//            type = CustomerType.valueOf(typeName);
//        }
        SettlementMethodType methodType = null;
        if(StrKit.notBlank(settlementMethod)){
            methodType = SettlementMethodType.valOf(settlementMethod);
        }
        PageList<Customer> pager = customerService.findCustomers(state, phone, name, CustomerType.seller, startTime, endTime, methodType, getPageParam());
        setAttr("pager", pager);
        render("change.html");
    }

    @Tx
    public void changeSettlementMethod(){
        int customerId = getParaToInt("customerId");
        String settlementMethod = getPara("settlementMethod");
        SettlementMethodType methodType = null;
        if(StrKit.notBlank(settlementMethod)){
            methodType = SettlementMethodType.valOf(settlementMethod);
        }
        Customer supplier = customerService.getById(customerId);
        //现结改周结月结时，上次结算点设为当天零时
        if (supplier.getSettlementMethod() != null && supplier.getSettlementMethod().equals(SettlementMethodType.daily)) {
            Date today = DateUtil.getDayStart(new Date());
            supplier.setLastSettlementTime(today);
        }
        supplier.setSettlementMethod(methodType);

        customerService.save(supplier);
        ok("修改成功");
    }
}
