package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.*;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.Base64;
import com.aichebaba.rooftop.utils.MD5;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.*;

@Controller
@Scope("prototype")
public class MemberController extends BaseController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MonthlyReportService reportService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CustomerCheckService customerCheckService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private AccountPayeeService accountPayeeService;

    @Autowired
    private BrandService brandService;

    @ActionKey("index.html")
    public void index() {
        setAttr("customer", getCurCustomer());
        setAttr("provinces", provinceService.getAllDisplayProvinces(null));
        County county = provinceService.getCountyInfo(getCurCustomer().getCountyId());
        Province province = provinceService.getProvince(getCurCustomer().getProvinceId());
        if (getCurCustomer().getProvinceId() > 0) {
            setAttr("cities", provinceService.getCitiesByProvince(getCurCustomer().getProvinceId(), null));
        }
        if (getCurCustomer().getCityId() > 0) {
            setAttr("counties", provinceService.getCountiesByCity(getCurCustomer().getCityId(), null));
        }
        setAttr("county", county);
        setAttr("province", province);
        render("index.html");
    }

    /**
     * 通过原密码修改密码
     *
     * @throws Exception
     */
    public void editPwd() throws Exception {
        String oldPwd = getPara("oldPwd");
        String newPwd = getPara("newPwd");
        Customer customer = getCurCustomer();
        if (StrKit.isBlank(oldPwd)) {
            error("原密码必填");
            return;
        }
        if (StrKit.isBlank(newPwd)) {
            error("新密码必填");
            return;
        }
        if (!MD5.md5(oldPwd, "UTF-8").equals(customer.getPassword())) {
            error("原密码不正确");
            return;
        }
        if (StrKit.isBlank(newPwd)) {
            error("密码不正确");
            return;
        }

        customer.setPassword(MD5.md5(newPwd, "UTF-8"));
        customerService.save(customer);
        ok("密码修改成功");
    }

    /**
     * 修改用户信息
     */
    public void editCustomer() {
        String name = getPara("name");
        String phone = getPara("phone");
        String email = getPara("email");
        String address = getPara("address");
//        String fullAddress = getPara("fullAddress");
        String postCode = getPara("postCode", "000000");
        int provinceId = getParaToInt("province", 0);
        int cityId = getParaToInt("city", 0);
        int countyId = getParaToInt("county", 0);
        if (provinceId == 0 || cityId == 0 || countyId == 0) {
            error("请选择省市区");
            return;
        }
        if (StrKit.isBlank(name)) {
            error("用户姓名必填");
            return;
        }
        if (StrKit.isBlank(phone)) {
            error("手机号必填");
            return;
        }
        if (StrKit.isBlank(email)) {
            error("邮箱必填");
            return;
        }
        if (StrKit.isBlank(address)) {
            error("详细地址必填");
            return;
        }

        Customer customer = getCurCustomer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setPostCode(postCode);
        customer.setProvinceId(provinceId);
        customer.setCityId(cityId);
        customer.setCountyId(countyId);
        County county = provinceService.getCountyInfo(countyId);
        if (county != null) {
            customer.setProvince(county.getProvinceName());
            customer.setCity(county.getCityName());
            customer.setArea(county.getName());
        }
        customer.setPickAddress(customer.getCityArea2() + address);
        customerService.save(customer);
        ok("用户信息成功");
    }

    /**
     * 关注列表
     */
    @ActionKey("follow.html")
    public void getFollow() {
        int id = curCustomerId();
        PageList<Goods> pager = goodsService.getFollow(id, getPageParam());
        setAttr("pager", pager);
        render("follow.html");
    }

    /**
     * 资金结算
     */
    @ActionKey("settlement.html")
    public void settlement() {
        PageList<MonthlySettlement> pager = reportService.getReport(curCustomerId(), getPageParam());
        setAttr("pager", pager);
        setAttr("company", getCurCustomer().getSupplierCompany());
        render("settlement.html");
    }

    /**
     * 关注列表取消关注
     */
    public void deleteFollow() {
        int id = getParaToInt("id", 0);
        followService.deleteFollow(id);
        ok("取消关注成功");
    }

    /**
     * 前端--发布货品
     */
    @ActionKey("fabugoods.html")
    public void fabugoods(){render("fabugoods.html");}

    /**
     * 前端--新增商品
     */
    @ActionKey("new_addGoods.html")
    public void new_addGoods(){render("new_addGoods.html");}

    /**
     * 前端--商品详情
     */
    @ActionKey("new_goodDetails.html")
    public void new_goodDetails(){render("new_goodDetails.html");}

    /**
     * 申请进货商
     */
    @ActionKey("purchase.html")
    public void purchase() {
        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.PURCHASE);
        if (check != null && check.getStatus().equals(CustomerAuditState.NO_PASS)) {
            redirect("/member/purchaseResult.html");
            return;
        }
        String showMsg = getPara("showMsg");
        if (StrKit.notBlank(showMsg)) {
            setAttr("showMsg", true);
        } else {
            setAttr("showMsg", false);
        }
        setAttr("provinces", provinceService.getAllDisplayProvinces(null));
        County county = provinceService.getCountyInfo(getCurCustomer().getCountyId());
        Province province = provinceService.getProvince(getCurCustomer().getProvinceId());
        if (getCurCustomer().getProvinceId() > 0) {
            setAttr("cities", provinceService.getCitiesByProvince(getCurCustomer().getProvinceId(), null));
        }
        if (getCurCustomer().getCityId() > 0) {
            setAttr("counties", provinceService.getCountiesByCity(getCurCustomer().getCityId(), null));
        }
        setAttr("county", county);
        setAttr("province", province);
        setAttr("customer", getCurCustomer());
        render("purchase.html");
    }

    /**
     * 进货商申请结果
     */
    @ActionKey("purchaseResult.html")
    public void purchaseResult() {
        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.PURCHASE);
        if (check == null || check.getStatus().equals(CustomerAuditState.NO_AUDIT)) {
            redirect("/member/purchase.html");
            return;
        }
        setAttr("check", check);
        setAttr("provinces", provinceService.getAllDisplayProvinces(null));
        County county = provinceService.getCountyInfo(check.getCountyId());
        Province province = provinceService.getProvince(check.getProvinceId());
        if (check.getProvinceId() > 0) {
            setAttr("cities", provinceService.getCitiesByProvince(check.getProvinceId(), null));
        }
        if (check.getCityId() > 0) {
            setAttr("counties", provinceService.getCountiesByCity(check.getCityId(), null));
        }
        setAttr("county", county);
        setAttr("province", province);
        render("purchaseResult.html");
    }

    /**
     * 申请进货商处理
     */
    public void addPurchase() {

        String shopUrl = getPara("shopUrl", "");
        String qq = getPara("qq", "");
        String wangwang = getPara("wangwang", "");
        String alipayCode = getPara("alipayCode", "");
        String tenpayCode = getPara("tenpayCode", "");
        String alipayName = getPara("alipayName", "");
        String weiXin = getPara("weiXin", "");
        String address = getPara("address", "");
        String postCode = getPara("postCode", "000000");
        String[] goodsType = getParaValues("type");
        if (goodsType == null) {
            error("请选择公司商品类型");
            return;
        }

        int provinceId = getParaToInt("province", 0);
        int cityId = getParaToInt("city", 0);
        int countyId = getParaToInt("county", 0);
        if (provinceId == 0 || cityId == 0 || countyId == 0) {
            error("请选择省市区");
            return;
        }

        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.PURCHASE);
        if (check == null) {
            check = new CustomerCheck();
        }

        check.setType(CustomerAuditType.PURCHASE);
        check.setShopUrl(shopUrl);
        check.setQq(qq);
        check.setWangwang(wangwang);
        check.setAlipayCode(alipayCode);
        check.setTenpayCode(tenpayCode);
        check.setCustomerId(curCustomerId());
        check.setAddress(address);
//        check.setFullAddress(fullAddress);
        check.setStatus(CustomerAuditState.NO_AUDIT);
        check.setAlipayName(alipayName);
        check.setWeiXin(weiXin);
        check.setPurchaseTime(new Date());
        if (goodsType != null) {
            StringBuilder type = new StringBuilder();
            for (String tp : goodsType) {
                type.append(tp).append(";");
            }
            if (type.lastIndexOf(";") == (type.length() - 1)) {
                type.deleteCharAt(type.length() - 1);
            }
            check.setInGoodsType(type.toString());
        }
        check.setPostCode(postCode);

        check.setProvinceId(provinceId);
        County county = provinceService.getCountyInfo(countyId);
        if (county != null) {
            check.setProvince(county.getProvinceName());
            check.setCityId(county.getCityId());
            check.setCity(county.getCityName());
            check.setCountyId(countyId);
            check.setArea(county.getName());
        }
        customerCheckService.save(check);

        Customer customer = getCurCustomer();
        customer.setPurchaseState(CustomerPurchaseState.NO_AUDIT);
        customer.setPurchaseTime(new Date());
        setCurCustomer(customer);
        customerService.save(customer);
        ok("ok");
    }

    /**
     * 申请供货商
     */
    @ActionKey("supplier.html")
    public void supplier() {
        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.SUPPLIER);
        if (check != null && check.getStatus().equals(CustomerAuditState.NO_PASS)) {
            redirect("/member/supplierResult.html");
            return;
        }
        String showMsg = getPara("showMsg");
        if (StrKit.notBlank(showMsg)) {
            setAttr("showMsg", true);
        } else {
            setAttr("showMsg", false);
        }
        setAttr("provinces", provinceService.getAllDisplayProvinces(null));
        County county = provinceService.getCountyInfo(getCurCustomer().getCountyId());
        Province province = provinceService.getProvince(getCurCustomer().getProvinceId());
        if (getCurCustomer().getProvinceId() > 0) {
            setAttr("cities", provinceService.getCitiesByProvince(getCurCustomer().getProvinceId(), null));
        }
        if (getCurCustomer().getCityId() > 0) {
            setAttr("counties", provinceService.getCountiesByCity(getCurCustomer().getCityId(), null));
        }
        setAttr("county", county);
        setAttr("province", province);
        setAttr("customer", getCurCustomer());
        render("supplier.html");
    }

    /**
     * 申请供货商处理
     *
     * @throws Exception
     */
    @Tx
    public void saveSupplier() throws Exception {
        String supplierCompany = getPara("supplierCompany", "");
        String artificialPerson = getPara("artificialPerson", "");
        String emergencyContact = getPara("emergencyContact", "");
        String emergencyPhone = getPara("emergencyPhone", "");
//        String brand = getPara("brand", "");
        String[] brands = getParaValues("brand");
        boolean isProducer = getParaToBoolean("isProducer");
        String alipayCode = getPara("alipayCode", "");
        String tenpayCode = getPara("tenpayCode", "");
        String alipayName = getPara("alipayName", "");
        String weiXin = getPara("weiXin", "");
        String qq = getPara("qq", "");
//        String pickAddress = getPara("pickAddress");
        String address = getPara("address");
        String city = getPara("city");
        String postCode = getPara("postCode", "000000");
        String[] goodsType = getParaValues("outGoodsType");

        int provinceId = getParaToInt("province", 0);
        int cityId = getParaToInt("city", 0);
        int countyId = getParaToInt("county", 0);
        if (provinceId == 0 || cityId == 0 || countyId == 0) {
            error("请选择省市区");
            return;
        }
        if (goodsType == null) {
            error("请选择公司商品类型");
            return;
        }

        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.SUPPLIER);
        if (check == null) {
            check = new CustomerCheck();
        }
        if (customerService.checkRepeatCompanyName(curCustomerId(), supplierCompany)) {
            error("你申请的公司名已被注册，如非本人操作请联系纳车品网络科技有限公司0576-81300105");
            return;
        }

        UploadFile file = getFile("businessLicence", "");
        if (file != null) {
            String localFile = file.getSaveDirectory() + File.separator + file.getFileName();
            if (!checkFileType(localFile)) {
                error("文件类型不正确,请重新上传！");
                return;
            }
            String upFile = PicUpload.picUpload(localFile);
            if (StrKit.isBlank(upFile)) {
                error("图片上传失败,请重新上传！");
                return;
            }
            check.setBusinessLicence(upFile);
        }

        check.setSupplierCompany(supplierCompany);
        check.setArtificialPerson(artificialPerson);
        check.setEmergencyContact(emergencyContact);
        check.setEmergencyPhone(emergencyPhone);

        check.setBrand(Joiner.on(Constant.SPLIT_MARK).join(brands));
        check.setIsProducer(isProducer);
        check.setType(CustomerAuditType.SUPPLIER);
        check.setAlipayCode(alipayCode);
        check.setTenpayCode(tenpayCode);
        check.setAddress(address);
        check.setCustomerId(curCustomerId());
        check.setStatus(CustomerAuditState.NO_AUDIT);
        check.setWeiXin(weiXin);
        check.setQq(qq);
        check.setAlipayName(alipayName);
        check.setSupplierTime(new Date());
        if (goodsType != null) {
            StringBuilder type = new StringBuilder();
            for (String tp : goodsType) {
                type.append(tp).append(Constant.SPLIT_MARK);
            }
            if (type.lastIndexOf(Constant.SPLIT_MARK) == (type.length() - 1)) {
                type.deleteCharAt(type.length() - 1);
            }
            check.setOutGoodsType(type.toString());
        }

        check.setPostCode(postCode);
        check.setProvinceId(provinceId);
        County county = provinceService.getCountyInfo(countyId);
        if (county != null) {
            check.setProvince(county.getProvinceName());
            check.setCityId(county.getCityId());
            check.setCity(county.getCityName());
            check.setCountyId(countyId);
            check.setArea(county.getName());
        }
        check.setPickAddress(check.getCityArea2() + address);
        customerCheckService.save(check);

        Customer customer = getCurCustomer();
        customer.setSupplierState(CustomerSupplierState.NO_AUDIT);
        customer.setSupplierTime(new Date());
        setCurCustomer(customer);
        customerService.save(customer);

        ok("ok");
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

    /**
     * 供货商申请结果
     */
    @ActionKey("supplierResult.html")
    public void supplierResult() {
        CustomerCheck check = customerCheckService.getByCustomerId(curCustomerId(), CustomerAuditType.SUPPLIER);
        if (check == null || check.getStatus().equals(CustomerAuditState.NO_AUDIT)) {
            redirect("/member/supplier.html");
            return;
        }
        setAttr("check", check);
        setAttr("provinces", provinceService.getAllDisplayProvinces(null));
        County county = provinceService.getCountyInfo(check.getCountyId());
        Province province = provinceService.getProvince(check.getProvinceId());
        if (check.getProvinceId() > 0) {
            setAttr("cities", provinceService.getCitiesByProvince(check.getProvinceId(), null));
        }
        if (check.getCityId() > 0) {
            setAttr("counties", provinceService.getCountiesByCity(check.getCityId(), null));
        }
        setAttr("county", county);
        setAttr("province", province);
        render("supplierResult.html");
    }

    /**
     * 收款帐户设置
     */
    @ActionKey("accountPayee.html")
    public void goPayee()  {
        try {
            setAttr("filePolicy", policy("file"));
            setAttr("imagePolicy", policy("image"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<AccountPayee> list = getAccountPayeeList();

        setAttr("lists", list);
        render("accountPayee.html");
    }

    // 获取客户名下的收款账号
    private List<AccountPayee> getAccountPayeeList() {
        return accountPayeeService.findAccountPayeeList(getCurCustomer().getId());
    }

    // 撤消默认账号
    private void doUpdate(List<AccountPayee> list) {
        if (list.size() > 0) {
            for (AccountPayee ap : list) {
                if (ap.getDefaultAccount() == true) {
                    ap.setDefaultAccount(false);
                    accountPayeeService.update(ap);
                }
            }
        }
    }

    /**
     * 添加收款人账户
     */
    public void doAddPayee() {
        String payeeAccount = getPara("payeeAccount");
        String payeeName = getPara("payeeName");
        String certificate = getPara("fileUrl");
        Integer defaultAccount = getParaToInt("defaultAccount", 0);

        if (accountPayeeService.exists(payeeAccount, getCurCustomer().getId())) {
            error("此收款账号已经存在！");
            return;
        }
        if (StrKit.isBlank(payeeAccount)) {
            error("收款人账号不能为空！");
            return;
        }
        if (StrKit.isBlank(payeeName)) {
            error("收款人姓名不能为空！");
            return;
        }
        if (StrKit.isBlank(certificate)) {
            error("授权书不能为空！");
            return;
        }
        List<AccountPayee> list = getAccountPayeeList();
        if (defaultAccount == 0) {
            if (list.size() == 0) {
                error("收款账号必需要有一个默认账号！");
                return;
            }
        }

        AccountPayee accountPayee = new AccountPayee();
        accountPayee.setCustomerId(getCurCustomer().getId());
        accountPayee.setUserName(getCurCustomer().getUsername());
        accountPayee.setName(getCurCustomer().getName());
        accountPayee.setCompany(getCurCustomer().getSupplierCompany());
        accountPayee.setPayeeAccount(payeeAccount);
        accountPayee.setPayeeName(payeeName);
        accountPayee.setCertificate(certificate);
        if (defaultAccount == 1) {
            doUpdate(list);
            accountPayee.setDefaultAccount(true);
        } else {
            accountPayee.setDefaultAccount(false);
        }
        accountPayee.setStatus(AccountPayeeStatus.PENDING);
        accountPayee.setAddTime(new Date());
        accountPayeeService.save(accountPayee);

        ok("提交成功！");
    }

    // 删除账号
    public void doDeletePayee() {
        Integer id = getParaToInt("id");

        AccountPayee ap = accountPayeeService.findById(id);
        if (ap.getDefaultAccount() == true) {
            error("此账号为默认收款账号不能进行删除，请先设置其他账号为收款账号后在进行删除！");
            return;
        }

        accountPayeeService.delete(id);

        ok("删除成功！");
    }

    // 设置默认收款账号
    public void doDefaultPayee() {
        Integer id = getParaToInt("id");

        List<AccountPayee> list = getAccountPayeeList();
        doUpdate(list);

        AccountPayee ap = accountPayeeService.findById(id);
        ap.setDefaultAccount(true);
        accountPayeeService.update(ap);

        ok("收款账号设置成功！");
    }

    public void goUpdatePayee() {
        Integer id = getParaToInt("id");

        AccountPayee ap = accountPayeeService.findById(id);

        try {
            setAttr("filePolicy", policy("file"));
            setAttr("imagePolicy", policy("image"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAttr("payee", ap);
        render("updateAccountPayee.html");
    }

    // 修改保存
    public void doUpdatePayeeSave() {
        Integer id = getParaToInt("id");
        //Integer defaultAccount = getParaToInt("defaultAccount", 0);

        AccountPayee ap = accountPayeeService.findById(id);
        /*
        if (ap.getDefaultAccount() == true && defaultAccount != 1) {
            error("您修改的账号为默认账号，如需更改请到收款账户设置页进行默认账户设置！");
            return;
        } else {
            if (defaultAccount == 1) {
                List<AccountPayee> list = getAccountPayeeList();
                doUpdate(list);
            }
            ap.setDefaultAccount(defaultAccount == 1);
        }
        */
        ap.setPayeeAccount(getPara("payeeAccount"));
        ap.setPayeeName(getPara("payeeName"));
        ap.setStatus(AccountPayeeStatus.PENDING);
        String fileUrl = getPara("fileUrl");
        if (StrKit.notBlank(fileUrl)) {
            ap.setCertificate(fileUrl);
        }
        accountPayeeService.update(ap);

        ok("保存成功！");
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
}
