package com.aichebaba.rooftop.config;

import com.aichebaba.rooftop.controller.admin.*;
import com.jfinal.config.Routes;

public class AdminRoutes extends Routes {
    @Override
    public void config() {
        add("/admin", HomeController.class, "/admin");
        add("/admin/menu", MenuController.class, "/admin/menu");
        add("/admin/user", UserController.class, "/admin/user");
        add("/admin/customer", CustomerController.class, "/admin/customer");
        add("/admin/area", AreaController.class, "/admin/area");
        add("/admin/province", ProvinceController.class, "/admin/province");
        add("/admin/city", CityController.class, "/admin/city");
        add("/admin/county", CountyController.class, "/admin/county");

        add("/admin/order", OrderController.class, "/admin/order");
        add("/admin/refund", RefundController.class, "/admin/refund");
        add("/admin/applyCancelTrade", OrderApplyCancelController.class, "/admin/applyCancelTrade");

        add("/admin/goods/online", GoodOnlineController.class, "/admin/goods");
        add("/admin/goods/offline", GoodOfflineController.class, "/admin/goods");
        add("/admin/goods/waitAudit", GoodAuditController.class, "/admin/goods");
        add("/admin/goods/customized", CustomizedController.class, "/admin/goods");
        add("/admin/goods/tail", TailController.class, "/admin/goods");
		add("/admin/goods/classes", GoodClassController.class, "/admin/goods");
		add("/admin/goods", GoodController.class, "/admin/goods");

        add("/admin/picklist/today", PickMyTodayController.class, "/admin/picklist");
        add("/admin/picklist/picking", PickMyToday2Controller.class, "/admin/picklist");
//        add("/admin/picklist/before", PickMyBeforeController.class, "/admin/picklist");
        add("/admin/picklist/peiOrder", PickPeiOrderController.class, "/admin/picklist");

        add("/admin/deliver/wait", DeliverWaitController.class, "/admin/deliver");
        add("/admin/deliver/my", DeliverMyController.class, "/admin/deliver");
        add("/admin/deliver/history", DeliverHistoryController.class, "/admin/deliver");
        add("/admin/deliver/print", DeliverPrintController.class, "/admin/deliver");

        add("/admin/customerAudit/purchase", PurchaseAuditController.class, "/admin/customer/purchase");
        add("/admin/customerAudit/supplier", SupplierAuditController.class, "/admin/customer/supplier");

        add("/admin/settlement/cur", SettlementCurController.class, "/admin/settlement");
        add("/admin/settlement/history", SettlementHistoryController.class, "/admin/settlement");
        add("/admin/settlement/operate", SettlementOperateController.class, "/admin/settlement");
        add("/admin/settlement/pay", SettlementPayController.class, "/admin/settlement");
        add("/admin/settlement/check", SettlementCheckController.class, "/admin/settlement");
        add("/admin/settlement/order", SettlementOrderController.class, "/admin/settlement");
        add("/admin/settlement/express", SettlementExpressController.class, "/admin/settlement");

        add("/admin/finance/refund", FinanceRefundController.class, "/admin/finance/refund");
        add("/admin/finance/history", MonthBillHistoryController.class, "/admin/finance/history");

        add("/admin/member/level", MemberLevelController.class, "/admin/member");
        add("/admin/member/manage", adminMemberController.class, "/admin/member");
        add("/admin/coupon/manage", CouponManageController.class, "/admin/coupon");
        add("/admin/coupon/activity", CouponActivityController.class, "/admin/coupon/activity");

        add("/admin/statistics/purchase", StatisticsPurchaseController.class, "/admin/statistics/purchase");
        add("/admin/statistics/supplier", StatisticsSupplierController.class, "/admin/statistics/supplier");

        add("/admin/brand", BrandManagerController.class, "/admin/brand");
        add("/admin/notice", NoticeController.class, "/admin/notice");
        add("/admin/settings", SettingsController.class, "/admin/settings");
    }
}
