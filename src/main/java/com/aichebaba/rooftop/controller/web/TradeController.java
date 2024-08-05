package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.AreaDao;
import com.aichebaba.rooftop.dao.PostFeeStrategyDao;
import com.aichebaba.rooftop.dao.ProvinceDao;
import com.aichebaba.rooftop.interceptor.web.WebLoginInterceptor;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.*;
import com.aichebaba.rooftop.service.*;
import com.aichebaba.rooftop.utils.*;
import com.aichebaba.rooftop.vo.Json;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.jfinal.aop.Before;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

import static com.google.common.collect.Collections2.transform;

@Controller
@Scope("prototype")
public class TradeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired

    private GoodsService goodsService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private PostFeeStrategyDao postFeeStrategyDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private SkuService skuService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private ExpressCompanyService expressService;

    @Autowired
    private GoodClassService goodClassService;

    @Value("${trade.prefix:T}")
    private String tradePrefix;

    @Value("${pay.type:0}")
    private int payType;

    //打包袋价格
    @Value("${pack.price:0}")
    private int packPrice;

    private DecimalFormat format = new DecimalFormat("#0.00");

    @Before(WebLoginInterceptor.class)
    public void sendItems() {
        Customer buyer = getCurCustomer();
        if (buyer == null || buyer.getType() == CustomerType.normal) {
            error(5001, "请先申请进货商资格");
            return;
        }
//        Area area = areaDao.getByName(buyer.getAddress());
//        if (area == null) {
//            error("请完成进货商资料");
//            return;
//        }
        String data = getPara("data");
        if (StrKit.isBlank(data)) {
            error("数据错误");
            return;
        }
        List<Order> items = JSON.parseArray(data, Order.class);
        if (items == null || items.isEmpty()) {
            error("数据错误");
            return;
        }

        for (Order o : items) {
            Goods goods = goodsService.getByCode(o.getGoodsCode());
            Sku sku = skuService.getSkuById(o.getSkuId());

            if (sku == null) {
                new ErrorInfoHelper().remind("30001");
                logger.error(o.getSkuId() + "");
                return;
            }

            if (goods == null || !goods.getStatus().equals(GoodsStatus.online) || sku == null) {
                error("商品已下架：" + o.getGoodsCode());
                return;
            }
            if (sku.getAvailableStock() < o.getQuantity()) {
                error(o.getGoodsCode() + " " + o.getSpecPropValue() + " 库存不足");
                return;
            }
            o.setProperties(sku.getProperties());
            o.setSpecPropValue(skuService.getSkuPropValues(o.getGoodsCode(), sku.getProperties()));
        }

        setSessionAttr("items", items);
        ok("");
    }

    public void sendCartItems() {
        Customer buyer = getCurCustomer();
        if (buyer == null || buyer.getType() == CustomerType.normal) {
            error("请先申请进货商资格");
            return;
        }
//        Area area = areaDao.getByName(buyer.getAddress());
//        if (area == null) {
//            error("请完成进货商资料");
//            return;
//        }
        String data = getPara("data");
        if (StrKit.isBlank(data)) {
            error("数据错误");
            return;
        }
        String[] values = data.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String v : values) {
            if (StrKit.notBlank(v)) {
                ids.add(Integer.valueOf(v));
            }
        }
        List<ShoppingCart> carts = shoppingCartService.getShoppingCarts(ids);
        List<Order> items = new ArrayList<>();
        for (ShoppingCart c : carts) {
            Order order = new Order();
            order.setGoodsCode(c.getGoodsCode());
            order.setSkuId(c.getSkuId());
            order.setColor(c.getColor());
            Goods goods = goodsService.getByCode(c.getGoodsCode());
            Sku sku = skuService.getSkuById(c.getSkuId());
            if (goods == null || !goods.getStatus().equals(GoodsStatus.online) || sku == null) {
                error("商品已下架：" + c.getGoodsName());
                return;
            }
            if (sku.getAvailableStock() < c.getQuantity()) {
                error(c.getGoodsCode() + "库存不足");
                return;
            }
            order.setSkuId(sku.getId());
            order.setProperties(sku.getProperties());
            order.setSpecPropValue(skuService.getSkuPropValues(order.getGoodsCode(), sku.getProperties()));
            order.setSize(c.getSize());
            order.setQuantity(c.getQuantity());
            order.setShoppingCartId(c.getId());
            items.add(order);
        }
        setSessionAttr("items", items);
        ok("");
    }

    @ActionKey("confirmOrder.html")
    public void confirmOrder() {
        List<Order> items = getSessionAttr("items");
        if (items == null || items.isEmpty()) {
            redirect("/page404.html");
            return;
        }
        Map<String, Goods> goodsMap = goodsService.getGoodsByCodes(transform(items, Order.GOODS_CODE_VALUE));
        long tradeTotalPrice = 0;
        int totalWeight = 0;
        int productNum = 0;
        int packCount = 0;
        for (Order order : items) {
            Goods g = goodsMap.get(order.getGoodsCode());
            Sku sku = skuService.getSkuById(order.getSkuId());
            if (g == null || !g.getStatus().equals(GoodsStatus.online) || sku == null) {
                order.setOffline(true);
            }
//            if (sku != null) {
//                order.setStock(sku.getAvailableStock());
//            }
            if (g != null) {
                order.setGoodsCode(g.getCode());
                order.setGoodsItemNo(g.getItemNo());
                order.setGoodsName(g.getName());
                order.setBuyerId(getCurCustomer().getId());
                order.setBuyerPhone(getCurCustomer().getPhone());
                order.setSellerId(g.getSellerId());
                order.setSellerPhone(customerService.getCustomerPhoneById(g.getSellerId()));
                if (sku != null) {
                    order.setStock(sku.getAvailableStock());
                    order.setPrice(sku.getPrice());
                    order.setWeight(sku.getWeight());
//                } else {
//                    order.setPrice(g.getWholesalePrice());
//                    order.setWeight(g.getWeight());
                }
                order.setMoney(order.getPrice() * order.getQuantity());
                order.setIsSpecial(g.getIsSpecial());
                order.setStatus(OrderStatus.CREATED);

                order.setBrand(g.getBrand());
                order.setSupplierCompany(customerService.getById(g.getSellerId()).getSupplierCompany());
                order.setHeadImg(g.getHeadImgUrl1());

                tradeTotalPrice += order.getMoney();
                totalWeight += order.getWeight() * order.getQuantity();
                productNum += order.getQuantity();

//                //计算打包袋数量
//                if (g.getThirdclassid() != null) {
//                    GoodsClass goodsClass = goodClassService.getById(g.getThirdclassid());
//                    if (goodsClass != null && goodsClass.getPackFlag()) {
//                        packCount += Math.ceil(order.getQuantity() * 1.0d / goodsClass.getPackNum());
//                    }
//                }
            }
        }

        List<Coupon> coupons = couponService.findEnableCoupon(curCustomerId(), LocalDate.now().toDate(), tradeTotalPrice);
        setAttr("coupons", coupons);
        setAttr("tradeTotalPrice", tradeTotalPrice);
        setAttr("totalWeight", totalWeight);
        setAttr("productNum", productNum);
        setAttr("provinces", provinceDao.findAllDisplay(true));
        setSessionAttr("items", items);
        List<ExpressCompany> expressCompanyList = expressService.getExpressCompanies(ExpressType.express);
        setAttr("expressList", expressCompanyList);
        ExpressCompany defaultExpress = expressService.getDefaultExpress(curCustomerId());
        setAttr("defaultExpress", defaultExpress);

        //计算打包费
        int packFee;
        if(totalWeight <= 1000){
            packFee = 0;
        }else if(totalWeight < 7000){
            packFee = packPrice;
        }else{
            packFee = (int) Math.ceil(totalWeight * 1.0d / 7000) * packPrice;
        }
        setAttr("packFee", packFee);

        render("order.html");
    }

    @Tx
    public void order() throws SQLException {
        List<Order> items = getSessionAttr("items");
        if (items == null || items.isEmpty()) {
            redirect("/page404.html");
            return;
        }
        String buyerMessage = getPara("buyerMessage", "");
        if (buyerMessage.length() > 200) {
            buyerMessage = buyerMessage.substring(0, 200);
        }
        String receiveName = getPara("receiveName");
        String receivePhone = getPara("receivePhone");
        int provinceId = getParaToInt("province", 0);
        int cityId = getParaToInt("city", 0);
        int county = getParaToInt("county", 0);
        String receiveAddress = getPara("receiveAddress");
        String postCode = getPara("postCode");
        int couponId = getParaToInt("couponId", 0);
        String couponFlag = getPara("couponFlag");
        int expressCompanyId = getParaToInt("defaultExpress", 0);

        String tel0 = getPara("phoneNumber0", "");
        String tel1 = getPara("phoneNumber1", "");
        String tel2 = getPara("phoneNumber2", "");
        String tel = "";
        if (StrKit.notBlank(tel0)) {
            tel += tel0;
        }
        if (StrKit.notBlank(tel1)) {
            tel +=  "-" + tel1;
        }
        if (StrKit.notBlank(tel2)) {
            tel += "-" + tel2;
        }

        long tradeTotalPrice = 0;
        int totalWeight = 0;
        Map<Integer, Integer> packMap = new HashMap<>();
        for (Order order : items) {
            Goods goods = goodsService.getByCode(order.getGoodsCode());
            Sku sku = skuService.getSkuById(order.getSkuId());

            if (sku == null) {
                new ErrorInfoHelper().remind("30001");
                logger.error(order.getSkuId() + "");
                return;
            }

            if (goods == null || !goods.getStatus().equals(GoodsStatus.online) || sku == null) {
                redirect("/trade/confirmOrder.html");
                return;
            }
            if (sku.getAvailableStock() < order.getQuantity()) {
                redirect("/trade/confirmOrder.html");
                return;
            }
            order.setBuyerMessage(buyerMessage);
            tradeTotalPrice += order.getMoney();
            totalWeight += order.getWeight() * order.getQuantity();

//            //按分类统计商品数量
//            if (goods.getThirdclassid() != null) {
//                if(packMap.get(goods.getThirdclassid()) == null){
//                    packMap.put(goods.getThirdclassid(), 0);
//                }
//                packMap.put(goods.getThirdclassid(), packMap.get(goods.getThirdclassid()) + order.getQuantity());
//            }
        }

//        //计算打包袋数量
//        int packCount = 0;
//        for (Integer key : packMap.keySet()) {
//            GoodsClass goodsClass = goodClassService.getById(key);
//            if (goodsClass != null && goodsClass.getPackFlag()) {
//                packCount += Math.ceil(packMap.get(key) * 1.0d / goodsClass.getPackNum());
//            }
//        }

        Double postFee = getPostFee(provinceId, cityId, totalWeight, expressCompanyId);
        //验证快递是否到达该地区
        if (postFee == -1) {
            redirect("/trade/confirmOrder.html?errMsg=NO_POST");
            return;
        } else if (postFee == -2) {
            redirect("/trade/confirmOrder.html?errMsg=OVER_WEIGHT");
            return;
        }
        ExpressCompany expressCompany = expressService.getExpressCompanyById(expressCompanyId);
        if (expressCompany == null) {
            redirect("/trade/confirmOrder.html");
            return;
        }
        long postSubsidy;
        if (expressCompany.getDiscount() > 0){
            postSubsidy = (long)(postFee * 100 - postFee * expressCompany.getDiscount());
            postFee = postFee * expressCompany.getDiscount() / 100d;
        }else{
            postSubsidy = expressCompany.getCutMoney();
            if(postSubsidy /100d > postFee){
                postSubsidy = Math.round(postFee * 100);
                postFee = 0d;
            }else {
                postFee = postFee - expressCompany.getCutMoney() / 100d;
            }
        }

        //计算打包费
        int packFee;
        if(totalWeight <= 1000){
            packFee = 0;
        }else if(totalWeight < 7000){
            packFee = packPrice;
        }else{
            packFee = (int) Math.ceil(totalWeight * 1.0d / 7000) * packPrice;
        }

        Trade trade = new Trade();
        trade.setBuyerId(getCurCustomer().getId());
        trade.setReceiveName(receiveName);
        trade.setReceiveAddress(receiveAddress);
        trade.setProvinceId(provinceId);
        trade.setCityId(cityId);
        trade.setCountyId(county);
        trade.setReceivePhone(receivePhone);
        trade.setReceiveTel(tel);
        trade.setZip(postCode);
        trade.setOrderCnt(items.size());
        trade.setWeight(totalWeight);
        trade.setTotalPayment(tradeTotalPrice);
        trade.setExpressId(expressCompany.getId());
        trade.setExpressName(expressCompany.getName());
        trade.setPostFee((long) (postFee * 100));
        trade.setPostSubsidy(postSubsidy);
        trade.setPayment(trade.getTotalPayment() + trade.getPostFee());
//        trade.setPackFee(packCount * packPrice);
        trade.setPackFee(packFee);
        trade.setPackSubsidy(trade.getPackFee());

        if(StrKit.notBlank(couponFlag)) {
            Coupon coupon = couponService.getCoupon(couponId);
            if (coupon != null) {
                if (coupon.getUsed()) {
                    redirect("/trade/confirmOrder.html");
                    return;
                }
                long couponFee = coupon.getCouponTemplate().getMoney();
                if (coupon.getCouponTemplate().getUseType().equals(UseType.PAYMENT)) {
                    couponFee = couponFee > trade.getTotalPayment() ? trade.getTotalPayment() : couponFee;
                } else {
                    couponFee = couponFee > trade.getPostFee() ? trade.getPostFee() : couponFee;
                }
                trade.setCouponId(couponId);        // he#
                trade.setUseType(coupon.getCouponTemplate().getUseType());
                trade.setCouponFee(couponFee);      // he#
                trade.setPayment(trade.getTotalPayment() + trade.getPostFee() - couponFee);
            }
        }
        trade.setStatus(OrderStatus.CREATED);
        trade.setCreated(new Date());

        boolean ok = orderService.addTrade(trade, items);
        if (ok) {
            couponService.usedCoupon(couponId);
            double payment = trade.getPayment() / 100.00d;
            removeSessionAttr("items");
            clearShoppingCart(items);
            redirect("/trade/toPay.html?t=" + tradePrefix + trade.getId() + "&payment=" + payment);
        } else {
            redirect("/trade/confirmOrder.html");
        }
    }

    public void toPay2(){
        int tradeId = getParaToInt("tradeId", 0);
        Trade trade = orderService.getTrade(tradeId);
        if(trade == null){
            new ErrorInfoHelper().remind("30004");
            logger.error(tradeId + "");
            return;
        }
        redirect("/trade/toPay.html?t=" + tradePrefix + trade.getId() + "&payment=" + (trade.getPayment()/100d));
    }

    @ActionKey("toPay.html")
    public void toPay() {

        //测试模式
        if (payType == 1) {
            render("toPay.html");
            return;
        }
        //正常模式
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", alipayConfig.getService());
        sParaTemp.put("partner", alipayConfig.getPartner());
        sParaTemp.put("seller_id", alipayConfig.getSellerId());
        sParaTemp.put("_input_charset", alipayConfig.getInputCharset());
        sParaTemp.put("payment_type", alipayConfig.getPaymentType());
        sParaTemp.put("notify_url", alipayConfig.getNotifyUrl());
        sParaTemp.put("return_url", alipayConfig.getReturnUrl());
        sParaTemp.put("anti_phishing_key", alipayConfig.getAntiPhishingKey());
        sParaTemp.put("exter_invoke_ip", alipayConfig.getExterInvokeIp());
        sParaTemp.put("it_b_pay", alipayConfig.getItBPay());
        sParaTemp.put("out_trade_no", getPara("t"));
        sParaTemp.put("subject", "纳车品交易");
        sParaTemp.put("total_fee", getPara("payment"));
        sParaTemp.put("body", "纳车品交易");
        // 其他业务参数根据在线开发文档，
        // 添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
        // 如sParaTemp.put("参数名","参数值");

        // 建立请求
        String html = AlipaySubmit.buildRequest(sParaTemp, alipayConfig, "get", "确认");
        renderHtml(html);
    }

    @ActionKey("return_url.html")
    @Tx
    public void returnCheck() {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParaMap().entrySet()) {
            String value = Joiner.on(",").join(entry.getValue());
            params.put(entry.getKey(), value);
            logger.debug("param key  {} - value {}", entry.getKey(), value);
        }
        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        String tradeNo = getPara("trade_no"); // 支付宝交易号
        String orderNo = getPara("out_trade_no"); // 获取订单号
        String totalFee = getPara("total_fee"); // 获取总金额
        String buyerEmail = getPara("buyer_email"); // 买家支付宝账号
        String tradeStatus = getPara("trade_status"); // 交易状态

        boolean dealResult = dealAlipayReturn(params, orderNo, totalFee, tradeStatus, tradeNo, buyerEmail);

        if (dealResult) {
            redirect("/trade/paySuccess.html?t=" + orderNo);
        } else {
            redirect("/trade/payFail.html?t=" + orderNo);
        }
    }

    @ActionKey("notify_url.html")
    @Tx
    public void notifyCheck() {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParaMap().entrySet()) {
            String value = Joiner.on(",").join(entry.getValue());
            params.put(entry.getKey(), value);
            logger.debug("param key{} - value {}", entry.getKey(), value);
        }
        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        String tradeNo = getPara("trade_no"); // 支付宝交易号
        String orderNo = getPara("out_trade_no"); // 获取订单号
        String totalFee = getPara("total_fee"); // 获取总金额
        String buyerEmail = getPara("buyer_email"); // 买家支付宝账号
        String tradeStatus = getPara("trade_status"); // 交易状态

        boolean dealResult = dealAlipayReturn(params, orderNo, totalFee, tradeStatus, tradeNo, buyerEmail);

        if (dealResult) {
            renderText("success");
        }
    }

    /**
     * 支付宝回调处理 (请不要修改)
     *
     * @param params        支付宝回调参数
     * @param orderNo       业务订单号
     * @param totalFee      金额
     * @param tradeStatus   支付宝交易状态
     * @param alipayTradeNo 支付宝交易号
     * @param buyerEmail    付款的支付宝帐号
     * @return 处理结果
     */
    private boolean dealAlipayReturn(Map<String, String> params, String orderNo, String totalFee, String tradeStatus,
                                     String alipayTradeNo, String buyerEmail) {
        boolean verifyResult = AlipayNotify.verify(params, alipayConfig);
        if (!verifyResult) {
            return false;
        }
        int tid = Integer.parseInt(orderNo.replace(tradePrefix, ""));
        Trade trade = orderService.getTrade(tid);

        // 计算得出通知验证结果
        if (("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus))) {// 验证成功
            if (StrKit.isBlank(trade.getAlipayNo())) {
                orderService.paid(tid, alipayTradeNo, buyerEmail);
            }
            return true;
        } else {
            return false;
        }
    }

//    public void paid() {
//        int tid = Integer.parseInt(getPara("ttt").replace(tradePrefix, ""));
//        orderService.paid(tid, "", "");
//        redirect("/trade/paySuccess.html?t=" + tid);
//    }

    private void clearShoppingCart(List<Order> items) {
        Collection<Order> cartItems = Collections2.filter(items, o -> o.getShoppingCartId() > 0);
        Collection<Integer> shoppingCartIds = Collections2.transform(cartItems, o -> o.getShoppingCartId());
        if (!shoppingCartIds.isEmpty()) {
            shoppingCartService.moveItems(shoppingCartIds);
            initShopCartSize(getCurCustomer());
        }
    }

    @ActionKey
    @Tx
    public void payCall() {
        int tradeId = Integer.parseInt(getPara("t").replace(tradePrefix, ""));
        String alipayNo = getPara("alipayNo", "");
        Boolean result = getParaToBoolean("result", false);
        String paidAlipayCode = getPara("paidAlipayCode", "");
        orderService.paid(tradeId, alipayNo, paidAlipayCode);
        if (result) {
            ok("付款成功");
        } else {
            error("付款失败");
        }
    }

    @ActionKey("paySuccess.html")
    public void paySuccess() {
        render("paySuccess.html");
    }

    @ActionKey("payFail.html")
    public void payFail() {
        render("payFail.html");
    }

    public void calcPostFee() {
        int provinceId = getParaToInt("provinceId", 0);
        int areaId = getParaToInt("areaId", 0);
        int weight = getParaToInt("weight", 0);
        int expressCompanyId = getParaToInt("expressCompanyId", 0);

        Double postFee = getPostFee(provinceId, areaId, weight, expressCompanyId);

        success(format.format(postFee));
    }

    /**
     * 计算运费
     * @param provinceId        省ID
     * @param areaId            市ID
     * @param weight            重量(克)
     * @param expressCompanyId  快递公司ID
     * @return -1:物流不到达 -2:邮包过重 >0:运费
     */
    private Double getPostFee(int provinceId, int areaId, int weight, int expressCompanyId){
        if (provinceId == 0 || weight == 0) {
            return 0d;
        }

        //邦德快递不接受30KG以上邮包
        if(expressCompanyId == 9 && weight > 30000){
            return -2d;
        }

        ExpressCompany expressCompany = expressService.getExpressCompanyById(expressCompanyId);

        if (expressCompany == null) {
            new ErrorInfoHelper().remind("30002");
            logger.error("未知物流公司：" + expressCompanyId);
            throw new RuntimeException("未知物流公司：" + expressCompanyId);
        }

        //先根据快递公司、省、市查询，如果查不到根据快递公司、省来查询
        List<PostFeeStrategy> strategies = postFeeStrategyDao.getPostFeeStrategies(expressCompanyId, provinceId, areaId);
        if (strategies.size() == 0) {
            strategies = postFeeStrategyDao.getPostFeeStrategies(expressCompanyId, provinceId);
        }
        //找不到物流费计算公式是，返回-1
        if (strategies.size() == 0) {
            return -1d;
        }
        double dPostFee = 0d;
        for (PostFeeStrategy strategy : strategies) {
            //精确重量
            int newWeight = weight;
            if (newWeight % strategy.getUnitWeight() != 0) {
                if(newWeight < 100){
                    newWeight = 1000;
                }
                newWeight = (newWeight / 100) * 100;
                newWeight = (int) Math.ceil(newWeight * 1.0 / strategy.getUnitWeight()) * strategy.getUnitWeight();
            }

            if ((strategy.getMinWeight() < newWeight && strategy.getMaxWeight() >= newWeight) || (strategy.getMinWeight() < newWeight && strategy.getMaxWeight() == 0)) {
                long postFee = 0;
                if (strategy.getFixed()) {
                    postFee = strategy.getUnitPrice() * 1000;
                } else {
                    postFee = (newWeight + strategy.getPlusWeight()) * strategy.getUnitPrice() + strategy.getPlusMoney() * 1000;
                }
                if ((strategy.getMinMoney() * 1000) > postFee) {
                    postFee = strategy.getMinMoney() * 1000;
                }
                dPostFee = postFee / 100000.00d;
                break;
            }
        }
        dPostFee = dPostFee * expressCompany.getMultiply() / 100d;
        if (expressCompany.getTrunc().equals(TruncType.ceil)) {
            dPostFee = Math.ceil(dPostFee);
        } else if (expressCompany.getTrunc().equals(TruncType.round)) {
            dPostFee = Math.round(dPostFee) * 1.0;
        }
        dPostFee = dPostFee + expressCompany.getPlusMoney() / 100d;

//        if (expressCompany.getDiscount() > 0) {
//            dPostFee = dPostFee * expressCompany.getDiscount() / 100;
//        } else if (expressCompany.getPlusMoney() > 0) {                           // he#
//            dPostFee = dPostFee + expressCompany.getPlusMoney() / 100;
//        }

        if (dPostFee < 3) {
            new ErrorInfoHelper().remind("30003");
            logger.error("异常快递费用：" + dPostFee);
            throw new RuntimeException("异常快递费用：" + dPostFee);
        }

        return dPostFee;
    }

    public void allPostFee(){
        int provinceId = getParaToInt("provinceId", 0);
        int areaId = getParaToInt("areaId", 0);
        int weight = getParaToInt("weight", 0);

        List<ExpressCompany> expressCompanyList = expressService.getExpressCompanies(ExpressType.express);
        for(ExpressCompany expressCompany : expressCompanyList) {
            Double postFee = getPostFee(provinceId, areaId, weight, expressCompany.getId());

            expressCompany.setPostFee(postFee);
        }
        setAttr("expressList", expressCompanyList);
        ExpressCompany defaultExpress = expressService.getDefaultExpress(curCustomerId());
        setAttr("defaultExpress", defaultExpress);
        String html = TemplateUtils.html("/web/trade/postFee.html", getRequest());
        ok("",html);
    }

    /**
     * 修改默认快递公司
     */
    public void changeDefaultExpress(){
        int expressCompanyId = getParaToInt("expressCompanyId", 0);
        int customerId = curCustomerId();
        expressService.changeDefaultExpress(expressCompanyId, customerId);
        ok("修改成功");
    }

    /**
     *  地址解析 add 2016-06-17
     *  He Daoyuan
     */
    public void parse1688() {
        String addressStr = getPara("parseAddr");
        JSONObject json = new AddressParseHelper(addressStr).getAddressJson1688();

        if ("success".equals(json.get("check"))) {
            ok("", json);
        }
        if ("fail".equals(json.get("check"))) {
            error("抱歉，解析没有成功，或许你的地址格式有误，请检查后重试或手动填写！");
        }
    }

    public void parseTaoBao() {
        String addressStr = getPara("parseAddr");
        JSONObject json = new AddressParseHelper(addressStr).getAddressJsonTaoBao();

        if ("success".equals(json.get("check"))) {
            ok("", json);
        } else if ("fail".equals(json.get("check"))) {
            error("抱歉，解析没有成功，或许你的地址格式有误，请检查后重试或手动填写！");
        }
    }

    public void parseTaoBao2() {
        String addressStr = getPara("parseAddr");
        AddressHelper userInfo = new AddressHelper();
        String infoCode = userInfo.toUserInfo(addressStr);
        if ("success".equals(infoCode)) {
            JSONObject json = (JSONObject) JSON.toJSON(userInfo);
            ok("", json);
        } else {
            error(infoCode + "，请检查后重试或手动填写！");
        }
    }
}
