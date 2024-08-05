package com.aichebaba.rooftop.controller;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.dao.page.PageParamV1;
import com.aichebaba.rooftop.ext.ExcelRender;
import com.aichebaba.rooftop.model.Brand;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.CustomerCheck;
import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.model.Menu;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.enums.CustomerAuditType;
import com.aichebaba.rooftop.model.enums.CustomerPurchaseState;
import com.aichebaba.rooftop.model.enums.CustomerSupplierState;
import com.aichebaba.rooftop.service.BrandService;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.GoodClassService;
import com.aichebaba.rooftop.service.MenuService;
import com.aichebaba.rooftop.service.RoleService;
import com.aichebaba.rooftop.service.ShoppingCartService;
import com.aichebaba.rooftop.vo.Json;
import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageParam;

public class BaseController extends Controller {

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected ShoppingCartService shoppingCartService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    private GoodClassService goodClassService;

    @Autowired
    private BrandService brandService;

    public int getPageSize() {
        return getParaToInt("perSize", 10);
    }

    public int getPageNo() {
        return getParaToInt("curNo", 1);
    }

    public BigDecimal getParaToBigDecimal(String paramName, int defaultValue) {
        String val = getPara(paramName);
        if (StrKit.notBlank(val)) {
            return new BigDecimal(val);
        } else {
            return new BigDecimal(defaultValue);
        }
    }

    public BigDecimal getParaToBigDecimal(String paramName) {
        Double val = getParaToDouble(paramName);
        return new BigDecimal(val);
    }

    public PageParam getPageParam() {
		PageParam pageParam = new PageParamV1(getPageSize(), getPageNo());
        pageParam.setSort(getPara("sort"));
        setAttr("sorts", pageParam.getSortMap());
        setAttr("sortValue", pageParam.getSortValue());
        return pageParam;
    }

    public void initTopMenus() {
        List<Menu> topMenus = getSessionAttr("topMenus");
        if (topMenus == null) {
            topMenus = menuService.getFirstMenus();
            Collection<Integer> authIds = roleService.getUserAuthIds(getCurUserId());
            List<Menu> secondMenus = menuService.getSecondMenusByAuthIds(authIds);
            Multimap<Integer, Menu> map = Multimaps.index(secondMenus, new Function<Menu, Integer>() {
                @Override
                public Integer apply(Menu m) {
                    return m.getParentId();
                }
            });
            for (Menu m : topMenus) {
                m.setChildren(map.get(m.getId()));
            }
            setSessionAttr("topMenus", topMenus);
        }
    }

    public void setCurTopMenu(int topMenuId) {
        List<Menu> topMenus = getSessionAttr("topMenus");
        for (Menu m : topMenus) {
            if (m.getId() == topMenuId) {
                setSessionAttr("curTopMenu", m);
                setSessionAttr("leftMenus", m.getChildren());
                break;
            }
        }
    }

    /**
     * 把所有分类信息及属性存入SESSION
     */
    public void initGoodsClass() {
        //三级分类及属性
        List<GoodsClass> classes = getSessionAttr(Constant.SESSION_GOODS_CLASS);
        if (classes == null) {
            classes = goodClassService.getAllThirdClassInfo();

            setSessionAttr(Constant.SESSION_GOODS_CLASS, classes);
        }

        //所有分类属性MAP
        Map<Integer, GoodsClass> allClassMap = getSessionAttr(Constant.SESSION_MAP_CLASS);
        if (allClassMap == null) {
            List<GoodsClass> allClass = goodClassService.getAll();
            allClassMap = new HashMap<>();
            for(GoodsClass goodsClass : allClass){
                allClassMap.put(goodsClass.getId(), goodsClass);
            }
            setSessionAttr(Constant.SESSION_MAP_CLASS, allClassMap);
        }
    }

    public GoodsClass getThirdClass(int thirdClassId){
        List<GoodsClass> classes = getSessionAttr(Constant.SESSION_GOODS_CLASS);
        for(GoodsClass goodsClass : classes){
            if(goodsClass.getId() == thirdClassId){
                return goodsClass;
            }
        }
        return null;
    }

    /**
     * 获取分类，属性信息
     * @param classId
     * @return
     */
    public GoodsClass getGoodsClassBySession(int classId) {
        Map<Integer, GoodsClass> allClassMap = getSessionAttr(Constant.SESSION_MAP_CLASS);
        return allClassMap.get(classId);
    }

    /**
     * 把所有品牌信息放入SESSION
     */
    public void initBrand(){
        //所有分类属性MAP
        Map<Integer, Brand> allBrandMap = getSessionAttr(Constant.SESSION_BRAND);
        if (allBrandMap == null) {
            List<Brand> allBrands = brandService.findAll();
            allBrandMap = new HashMap<>();
            for(Brand brand : allBrands){
                allBrandMap.put(brand.getId(), brand);
            }
            setSessionAttr(Constant.SESSION_BRAND, allBrandMap);
        }
    }

    /**
     * 获取品牌信息
     * @param brandId   品牌ID
     * @return
     */
    public Brand getBrandBySession(int brandId){
        Map<Integer, Brand> allBrandMap = getSessionAttr(Constant.SESSION_BRAND);
        return allBrandMap.get(brandId);
    }

    public void setCurUser(User user) {
        setSessionAttr("curUser", user);
    }

    /**
     * 获取后台当前登录员工帐号
     * @return
     */
    public User getCurUser() {
        return getSessionAttr("curUser");
    }

    public int getCurUserId() {
        User user = getCurUser();
        return user == null ? 0 : user.getId();
    }

    /**
     * 获取当前登录客户
     *
     * @return
     */
    public Customer getCurCustomer() {
        return getSessionAttr(Constant.SESSION_CUSTOMER);
    }

    /**
     * 获取当前登录客户ID
     *
     * @return
     */
    public int curCustomerId() {
        Customer customer = getCurCustomer();
        return customer == null ? 0 : customer.getId();
    }

    public void setCurCustomer(Customer customer) {
        if (customer != null) {

            CustomerCheck purchaseCheck = customerService.getCustomerCheck(customer.getId(), CustomerAuditType.PURCHASE);
            if (purchaseCheck != null) {
                customer.setPurchaseState(CustomerPurchaseState.valueOf(purchaseCheck.getStatus().name()));
            }

            CustomerCheck supplyCheck = customerService.getCustomerCheck(customer.getId(), CustomerAuditType.SUPPLIER);
            if (supplyCheck != null) {
                customer.setSupplierState(CustomerSupplierState.valueOf(supplyCheck.getStatus().name()));
            }

            initShopCartSize(customer);
            setSessionAttr(Constant.SESSION_CUSTOMER, customer);
            getSession().setMaxInactiveInterval(24 * 60 * 60);
        }
    }

    protected void initShopCartSize(Customer customer) {
        customer.setShoppingCartSize(shoppingCartService.getShoppingCartSize(customer.getId()));
    }

    public void removeCurCustomer() {
        removeSessionAttr(Constant.SESSION_CUSTOMER);
    }

    public String getReferer() {
        return getRequest().getHeader("referer");
    }

    /**
     * AJAX请求
     */
    public boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    /**
     * Json请求
     */
    public boolean isJsonRequest(HttpServletRequest request) {
        String type = request.getHeader("Accept");
        if (StrKit.isBlank(type))
            return false;
        return type.toLowerCase().contains("application/json");
    }

    public boolean isAjax() {
        return isAjaxRequest(getRequest()) || isJsonRequest(getRequest());
    }

    public void success(Object data) {
        ok("", data);
    }

    public void ok(String msg) {
        ok(msg, null);
    }

    public void ok(String msg, Object data) {
        renderJson(Json.success(msg, data));
    }

    public void noLogin() {
        if (isAjax()) {
            renderJson(new Json(Json.NO_LOGIN, "未登录", ""));
        } else {
            redirect("/");
        }
    }

    public void error(String errorMsg) {
        error(0, errorMsg);
    }

    public void error(String errorMsg, Object data) {
        renderJson(Json.error(errorMsg, data));
    }

    public void error(Integer errorSubCode, String errorMsg) {
        if (isAjax()) {
            renderJson(Json.error(errorMsg).setSubCode(errorSubCode));
        } else {
            setAttr("error_msg", errorMsg);
            renderError(errorSubCode);
        }
    }

    /**
     * 获得IP
     *
     * @return
     */
    public String getRemoteIP() {
        HttpServletRequest request = getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            // 根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ip = inet.getHostAddress();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) { // "***.***.***.***".length() = 15
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    public void excelRender(String template, String fileName) {
        render(new ExcelRender(template, fileName));
    }

}
