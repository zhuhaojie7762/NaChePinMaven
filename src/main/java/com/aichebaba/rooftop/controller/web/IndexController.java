package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.ext.MyCaptchaRender;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.*;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.MD5;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Scope("prototype")
public class IndexController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Value("${sms.type}")
    private int smsType;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeekService seekService;

    @Autowired
    private TailGoodsService tailGoodsService;

    @Autowired
    private ExpressCompanyService expressCompanyService;

    @Autowired
    private FollowService followService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private BannerService bannerService;

    /**
     * new home
     */
    @ActionKey({"/", "/index.html"})
    public void index(){
        //Long start = System.currentTimeMillis();
        AmosQuerier querier;
        String zhuanPai = "%专拍%";

        // banner
        querier = AmosDB.newQuerier().equals("type", BannerType.main.name()).orders("sort", AmosQuerier.ORDER_ASC).limit(10);
        setAttr("homeBanner", bannerService.findBanner(querier));

        // 左边广告
        querier = AmosDB.newQuerier().in("type", new Object[]{BannerType.cushion.name(), BannerType.floorMat.name(), BannerType.wheelCover.name(), BannerType.carTrim.name()});
        setAttr("leftBanner", bannerService.findBanner(querier));

        // 公告
        Map<String, Object> params = new HashMap<>();
        PageList<Notice> notices = noticeService.findNotices(params, new PageParam(3, 1));
        setAttr("notices", notices);

        // 每日上新
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).notLike("name", zhuanPai).orders("onlineTime", AmosQuerier.ORDER_DESC).limit(10);
        setAttr("oneDayGoods", goodsService.findByGoods(querier));

        // S 1F 坐垫 / 座套
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("classid", 52).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("zuoDian", goodsService.findByHomeGoods(querier));
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("classid", 53).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("zuoTao", goodsService.findByHomeGoods(querier));
        // 1F 坐垫 / 座套 热销排行
        querier = AmosDB.newQuerier().equals("g.`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 22).notLike("name", zhuanPai).orders("lastWeek", AmosQuerier.ORDER_DESC).limit(12);
        setAttr("hotZuoDian", goodsService.findByGoodsLeftSales(querier));
        // 热卖推荐
        querier = AmosDB.newQuerier().equals("bc.classId", 22).in("bc.top", new Object[]{3, 2}).orders("bc.top", AmosQuerier.ORDER_DESC).orders("bc.toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("brandZuoDian", brandService.findBrandByChoose(querier));
        // E 1F 坐垫 / 座套

        // S 2F 脚垫 / 后备箱垫
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("classid", 66).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("jiaoDian", goodsService.findByHomeGoods(querier));
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("classid", 67).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("houBXD", goodsService.findByHomeGoods(querier));
        // 2F 脚垫 / 后备箱垫 热销排行
        querier = AmosDB.newQuerier().equals("g.`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 23).notLike("name", zhuanPai).orders("lastWeek", AmosQuerier.ORDER_DESC).limit(12);
        setAttr("hotCheDian", goodsService.findByGoodsLeftSales(querier));
        // 热卖推荐
        querier = AmosDB.newQuerier().equals("bc.classId", 23).in("bc.top", new Object[]{3, 2}).orders("bc.top", AmosQuerier.ORDER_DESC).orders("bc.toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("brandCheDian", brandService.findBrandByChoose(querier));
        // E 2F 脚垫 / 后备箱垫

        // S 3F 方向盘套
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 24).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("fangXP", goodsService.findByGoods(querier));
        // 3F 方向盘套 热销排行
        querier = AmosDB.newQuerier().equals("g.`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 24).notLike("name", zhuanPai).orders("lastWeek", AmosQuerier.ORDER_DESC).limit(12);
        setAttr("hotFandXP", goodsService.findByGoodsLeftSales(querier));
        // 热卖推荐
        querier = AmosDB.newQuerier().equals("bc.classId", 24).in("bc.top", new Object[]{3, 2}).orders("bc.top", AmosQuerier.ORDER_DESC).orders("bc.toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("brandFanXP", brandService.findBrandByChoose(querier));
        // E 3F 方向盘套

        // S 4F 功能小件
        querier = AmosDB.newQuerier().equals("`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 28).in("top", new Object[]{3, 2}).orders("top", AmosQuerier.ORDER_DESC).orders("toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("small", goodsService.findByGoods(querier));
        // 4F 功能小件 热销排行
        querier = AmosDB.newQuerier().equals("g.`status`", GoodsStatus.online.getVal()).equals("thirdclassid", 28).notLike("name", zhuanPai).orders("lastWeek", AmosQuerier.ORDER_DESC).limit(12);
        setAttr("hotSmall", goodsService.findByGoodsLeftSales(querier));
        // 热卖推荐
        querier = AmosDB.newQuerier().equals("bc.classId", 28).in("bc.top", new Object[]{3, 2}).orders("bc.top", AmosQuerier.ORDER_DESC).orders("bc.toptime", AmosQuerier.ORDER_DESC).limit(6);
        setAttr("brandSmall", brandService.findBrandByChoose(querier));
        // E 4F 功能小件

        //System.out.println("首页加载时间：" + (System.currentTimeMillis() - start));

        render("index.html");
    }

    @ActionKey("loseGoods.html")
    public void loseGoods() {
        render("loseGoods.html");
    }

    /**
     * 物流公司
     */
    @ActionKey("/express/{code}.html")
    public void express() {
        int code = getUrlParaToInt("code");
        ExpressCompany company = expressCompanyService.getExpressCompanyById(code);
        setAttr("company", company);
        render("express.html");
    }
    @ActionKey("/logistics/logisticsOne.html")
    public void logisticsOne() {
        render("logistics/logisticsOne.html");
    }
    @ActionKey("/logistics/logisticsTwo.html")
    public void logisticsTwo() {
        render("logistics/logisticsTwo.html");
    }
    @ActionKey("/logistics/logisticsThree.html")
    public void logisticsThree() {
        render("logistics/logisticsThree.html");
    }
    @ActionKey("/logistics/logisticsFour.html")
    public void logisticsFour() {
        render("logistics/logisticsFour.html");
    }
    @ActionKey("/logistics/logisticsFive.html")
    public void logisticsFive() {
        render("logistics/logisticsFive.html");
    }
    @ActionKey("/logistics/logisticsSix.html")
    public void logisticsSix() {
        render("logistics/logisticsSix.html");
    }

    /**
     * 下架日志
     */
    @ActionKey("/offLog.html")
    public void offLog() {
        String goodsName = getPara("goodsName");
        String brand = getPara("brand");
        String itemNo = getPara("itemNo");
        Date startDate = getParaToDate("startDate");
        Date endDate = getParaToDate("endDate");

        if (endDate != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
            String tmp = formatter2.format(endDate);
            ParsePosition pos = new ParsePosition(0);
            endDate = formatter.parse(tmp + " 23:59:59", pos);
        }

        int pageSize = getParaToInt("perSize", 12);

        PageList<Goods> pager = goodsService
                .findGoods(null, GoodsStatus.offline, goodsName, brand, null, itemNo, 0, startDate, endDate, 0, 0,
                        new PageParam(pageSize, getPageNo()));
        setAttr("pager", pager);
        render("offLog.html");
    }

    public void sendExpress() {
        int expressId = getParaToInt("id", 0);
        String code = getPara("code");
        String phone = getPara("phone");

        boolean validateCode = MyCaptchaRender.validate(this, code);
        if (!validateCode) {
            logger.debug("图形验证码错误或验证码已过期 phone:{}, checkCode:{}, ip:{}", phone, code, getRemoteIP());
            error("图形验证码错误或验证码已过期");
            return;
        }

        if (StrKit.isBlank(phone)) {
            error("手机号必填");
            return;
        }

        ExpressCompany expressCompany = expressCompanyService.getExpressCompanyById(expressId);

        StringBuilder msg1 = new StringBuilder();
        StringBuilder msg2 = new StringBuilder();
        msg1.append("有客户（电话号码").append(phone).append("）需要物流，请及时与他联系。");
        msg2.append("有客户（电话号码").append(phone).append("）正联系物流公司").append(expressCompany.getName()).append("（联系人电话")
                .append(expressCompany.getPhone()).append("）。");

        try {
            SMSUtil.sendSmsMsg(expressCompany.getPhone(), msg1.toString());
            SMSUtil.sendSmsMsg(Constant.SMS_WORKER_PHONE, msg2.toString());
        } catch (Exception ex) {
            error("发送失败");
        }

        //发送成功后清空图形验证码
        MyCaptchaRender.clearCode(this);

        ok("发送成功");
    }

    /**
     * 获取图形验证码
     */
    public void captcha() {
        render(new MyCaptchaRender(60, 22, 4, true));
    }

    /**
     * 登录页面
     */
    @ActionKey("login.html")
    public void login() {
        render("login.html");
    }

    /**
     * 注册页面
     */
    @ActionKey("register.html")
    public void register() {
        render("register.html");
    }


    /**
     * 提示
     */
    @ActionKey("zx_tishi.html")
    public void zx_tishi(){render("zx_tishi.html");}

    /**
     * 登录处理
     *
     * @throws Exception
     */
    public void loginByCode() throws Exception {
        String code = getPara("code");
        String password = getPara("password", "");
        String autoLogin = getPara("autoLogin");

        Customer c = customerService.getCustomerByUsername(code);
        if (c == null) {
            c = customerService.getCustomerByPhone(code);
        }
        if (c == null) {
            error("用户名或手机号有误!");
            return;
        }
        if (c.getState().equals(CustomerState.stop)) {
            error("账号已停用!");
            return;
        }

        if (!MD5.md5(password, "UTF-8").equals(c.getPassword())) {
            error("密码有误!");
            return;
        }
        setCurCustomer(c);
        if (StrKit.notBlank(autoLogin)) {
            setCookie(Constant.COOKIE_CUSTOMER, Integer.toBinaryString(c.getId()), 60 * 60 * 24 * 365);
        }
        ok("登录成功!");
    }

    /**
     * 注销处理
     */
    public void logout() {
        removeCurCustomer();
        removeCookie(Constant.COOKIE_CUSTOMER);
        redirect("/");
    }

    /**
     * 协议页面
     */
    @ActionKey("agreement.html")
    public void agreement() {
        render("agreement.html");
    }

    /**
     * 活动页
     */
    @ActionKey("activity.html")
    public void activity(){render("activity.html");}

    /**
     * 忘记密码页面
     */
    @ActionKey("forgetPwd.html")
    public void forgetPwd() {
        render("forgetPwd.html");
    }

    /**
     * 发送短信验证码
     *
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    @ActionKey(method = RequestMethod.POST)
    public void sendCode() throws UnsupportedEncodingException, MalformedURLException {
        String phone = getPara("phone");
        String checkCode = getPara("checkCode", "");
        String code = getRandomCode();

        boolean validateCode = MyCaptchaRender.validate(this, checkCode);
        if (!validateCode) {
            logger.debug("图形验证码错误或验证码已过期 phone:{}, checkCode:{}, ip:{}", phone, checkCode, getRemoteIP());
            error("图形验证码错误或验证码已过期");
            return;
        }

        if (StrKit.isBlank(phone)) {
            error("手机号必填");
            return;
        }

        //        //判断电话号码是否已存在
        //        Customer customer = customerService.findCustomerByPhone(phone);
        //        if(customer != null){
        //            error("该电话号码已注册");
        //            return;
        //        }

        //测试环境 验证码为1234
        if (smsType == 0) {
            code = "1234";
        }
        SMSUtil.sendSMSCode(phone, code);
        setSmsCode(code);

        logger.debug("session:{}, phone:{}, smsCode:{}, ip:{}", getSession().getId(), phone, code, getRemoteIP());

        //发送成功后清空图形验证码
        MyCaptchaRender.clearCode(this);

        ok("短信发送成功");
    }

    /**
     * 获取随即的短信验证码
     *
     * @return
     */
    private String getRandomCode() {
        Random random = new Random();
        String code = String.valueOf(random.nextInt(8999) + 1000);
        return code;
    }

    private void setSmsCode(String code) {
        setSessionAttr(Constant.SESSION_SMS_CODE, code);
    }

    private String getSmsCode() {
        return getSessionAttr(Constant.SESSION_SMS_CODE);
    }

    private void clearSmsCode() {
        removeSessionAttr(Constant.SESSION_SMS_CODE);
    }

    /**
     * 注册
     */
    public void addCustomer() throws Exception {
        String phone = getPara("phone");
        String code = getPara("smsCode");
        String password = getPara("password");
        String username = getPara("username");
        String name = getPara("name");
        String email = getPara("email");

        if (!AppConfig.getValue("sms.pwCode").equals(code)) {
            if (!code.equals(getSmsCode())) {
                error("短信验证码不正确！");
                return;
            }
        }
        Customer customer = customerService.getCustomerByPhone(phone);
        if (customer != null) {
            error("该手机号已被注册！");
            return;
        }
        customer = customerService.getCustomerByUsername(username);
        if (customer != null) {
            error("该用户帐号已被注册！");
            return;
        }
        customer = new Customer();
        customer.setUsername(username);
        customer.setPhone(phone);
        customer.setPassword(MD5.md5(password, "UTF-8"));
        customer.setName(name);
        customer.setType(CustomerType.normal);
        customer.setState(CustomerState.normal);
        customer.setEmail(email);
        customer = customerService.save(customer);

        setCurCustomer(customer);

        ok("注册成功");
    }

    @ActionKey("regSuccess.html")
    public void regSuccess() {
        render("register_success.html");
    }
    /**
     * 验证短信
     */
    public void checkCode() {
        String phone = getPara("phone");
        String smsCode = getPara("smsCode");
        if (!smsCode.equals(getSmsCode())) {
            error("短信验证码不正确");
            return;
        }
        Customer customer = customerService.findCustomerByPhone(phone);
        if (customer == null) {
            error("该号码未注册");
            return;
        }
        clearSmsCode();
        setSessionAttr(Constant.SESSION_LOGIN_CHECK, "TRUE");
        ok("验证成功");
    }

    /**
     * 通过短信验证码修改密码
     */
    public void updatePwd() throws Exception {
        String phone = getPara("phone");
        String password = getPara("password");
        if (StrKit.isBlank(password)) {
            error("密码必填");
            return;
        }
        if (!getSessionAttr(Constant.SESSION_LOGIN_CHECK).equals("TRUE")) {
            error("未通过验证");
            return;
        }
        Customer customer = customerService.findCustomerByPhone(phone);
        customer.setPassword(MD5.md5(password, "UTF-8"));
        customerService.save(customer);
        removeSessionAttr(Constant.SESSION_LOGIN_CHECK);
        ok("修改密码成功");
    }

    public void cities() {
        int id = getParaToInt("id", 0);
        Boolean display = getParaToBoolean("display");
        List<City> cities = provinceService.getCitiesByProvince(id, display);
        setAttr("cities", cities);
        String html = TemplateUtils.html("admin/city/items", getRequest());
        success(html);
    }

    public void counties() {
        Boolean display = getParaToBoolean("display");
        List<County> counties = provinceService.getCountiesByCity(getParaToInt("id", 0), display);
        setAttr("counties", counties);
        String html = TemplateUtils.html("admin/county/items", getRequest());
        success(html);
    }
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private GoodsExtService goodsExtService;
}
