package com.aichebaba.rooftop.config;

import com.aichebaba.rooftop.controller.web.*;
import com.aichebaba.rooftop.controller.web.buyerDispute.BuyerDisputeController;
import com.aichebaba.rooftop.controller.web.buyerDispute.BuyerDisputeEditController;
import com.aichebaba.rooftop.controller.web.dispute.DisputeController;
import com.aichebaba.rooftop.controller.web.brand.BrandController;
import com.aichebaba.rooftop.controller.web.notice.ShowNoticeController;
import com.jfinal.config.Routes;

public class WebRoutes extends Routes {

    @Override
    public void config() {
        add("/", IndexController.class, "/web");
        add("/brand", BrandController.class, "/web/brand");
        add("/seek", SeekController.class, "/web/seek");
        add("/buy", BuyController.class, "web/goods");
        add("/goods", GoodsController.class, "web/goods");
        add("/goods/class", GoodsClassController.class, "web/goods/class");
        add("/trade", TradeController.class, "web/trade");
        add("/cart", ShoppingCartController.class, "web/goods");
        add("/member", MemberController.class, "/web/member");
        add("/member/sellOrder", SellOrderController.class, "/web/member/sellOrder");
        add("/member/online", OnlineController.class, "/web/member/online");
        add("/member/offline", GoodsOfflineController.class, "/web/member/offline");
        add("/member/waitCheck", WaitCheckController.class, "/web/member/waitCheck");
        add("/member/newGoods", NewGoodsController.class, "/web/member/newGoods");
        add("/member/backGoods", BackGoodsController.class, "/web/member/backGoods");
        add("/member/tailGoods", TailGoodsController.class, "/web/member/tailGoods");
        add("/member/buyOrder", BuyOrderController.class, "/web/member/buyOrder");
        add("/member/seek", SeekGoodsController.class, "/web/member/seekGoods");
        add("/member/coupon", MyCouponController.class, "/web/member/coupon");
        add("/member/bill", BillingController.class, "/web/member/bill");
        add("/member/dispute", DisputeController.class, "/web/member/dispute");
        add("/member/buyerDispute", BuyerDisputeController.class, "/web/member/buyerDispute");
        add("/member/buyerDisputeEdit", BuyerDisputeEditController.class, "/web/member/buyerDispute");
        add("/notice", ShowNoticeController.class, "/web/notice");
        add("/help", HelpController.class, "/web/help");
    }
}
