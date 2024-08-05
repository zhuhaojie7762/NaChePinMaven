package com.aichebaba.rooftop.controller.web.buyerDispute;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.controller.web.dispute.RefundAssist;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.BuyerDispute;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.refund.BuyerDisputeService;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by He DaoYuan on 2016/12/2.
 */
@Controller
@Scope("prototype")
public class BuyerDisputeEditController extends BaseController{
    public void refundEdit() {
        String code = getPara("code");
        refundEditLoad(code);
        render("refundEdit.html");
    }

    public void doRefundEdit() {
        String orderCode = getPara("orderCode");                    // order 编号
        Integer isCargo = getParaToInt("isCargo");                  // 是否已收到货
        String refundCause = getPara("refundCause");                // 退款退货原因
        Double refundAmount = getParaToDouble("refundAmount");      // 退款金额
        String refundExplain = getPara("refundExplain");            // 退款退货说明
        String[] refundImgs = getParaValues("refundImg");           // 退货退款纠纷5张图片url

        if ("请选择退款原因".equals(refundCause)) {
            error("请选择退款原因");
            return;
        }
        if (null == refundAmount || refundAmount < 0) {
            error("请输入退款金额");
            return;
        }
        if (refundExplain.length() > 200) {
            error("说明字数不能超过200个字");
            return;
        }

        Order order = orderService.getOrderByCode(orderCode);
        Trade trade = orderService.getTrade(order.getTradeId());
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        // 订单校验
        String checkOrder = BuyerDisputeAssist.checkOrder(order);
        if (checkOrder != null) {
            error(checkOrder);
            return;
        }

        Long refundFee = (long) (refundAmount * 100);
        Long maxRefund = RefundAssist.getMaxRefund(trade, order, orders);
        if (refundFee > maxRefund) {
            error("不能超过最大退款金额");
            return;
        }

        List<String> imgs = new ArrayList<>();
        uploadImgFile(imgs, refundImgs);

        order.setStatus(OrderStatus.APPLY_REFUND);                      // 已申请退款
        order.setIsCargo(isCargo);                                      // 是否已收到货
        order.setRefundDeal("");                                        // 退货退款处理方式
        order.setDisagreeRefundReason("");                              // 不同意理由
        order.setApplyRefundTime(new Date());                           // 退货退款申请时间
        order.setRefundFee(refundFee);                                  // 退款金额
        order.setRefundReason(refundCause + ":" + refundExplain);       // 退货退款理由
        RefundAssist.setImgGroup(order, imgs);                          // 退货退款纠纷图片，共存5张，以#号分隔
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(orderCode);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(OrderStatus.APPLY_REFUND);
        buyerDispute.setGoodsStatus(isCargo);
        buyerDispute.setReason(refundCause);
        buyerDispute.setMoney((long) (refundAmount * 100));
        buyerDispute.setRemark(refundExplain);
        buyerDisputeService.save(buyerDispute);

        ok("退款申请修改成功");
    }

    public void refundGoodsEdit() {
        String code = getPara("code");
        refundEditLoad(code);
        render("refundGoodsEdit.html");
    }

    public void doRefundGoodsEdit() {
        String orderCode = getPara("orderCode");                    // order 编号
        String refundCause = getPara("refundCause");                // 退款退货原因
        Double refundAmount = getParaToDouble("refundAmount");      // 退款金额
        String refundExplain = getPara("refundExplain");            // 退款退货说明
        String[] refundImgs = getParaValues("refundImg");           // 退货退款纠纷5张图片url

        if ("请选择退款原因".equals(refundCause)) {
            error("请选择退款原因");
            return;
        }
        if (null == refundAmount || refundAmount < 0) {
            error("请输入退款金额");
            return;
        }
        if (refundExplain.length() > 200) {
            error("说明字数不能超过200个字");
            return;
        }

        Order order = orderService.getOrderByCode(orderCode);
        Trade trade = orderService.getTrade(order.getTradeId());
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        // 订单校验
        String checkOrder = BuyerDisputeAssist.checkOrder(order);
        if (checkOrder != null) {
            error(checkOrder);
            return;
        }

        Long refundFee = (long) (refundAmount * 100);
        Long maxRefund = RefundAssist.getMaxRefund(trade, order, orders);
        if (refundFee > maxRefund) {
            error("不能超过最大退款金额");
            return;
        }

        List<String> imgs = new ArrayList<>();
        uploadImgFile(imgs, refundImgs);

        order.setStatus(OrderStatus.APPLY_REFUND_GOODS);                // 已申请退货
        order.setRefundDeal("");                                        // 退货退款处理方式
        order.setDisagreeRefundReason("");                              // 不同意理由
        order.setApplyRefundTime(new Date());                           // 退货退款申请时间
        order.setRefundFee(refundFee);                                  // 退款金额
        order.setRefundReason(refundCause + ":" + refundExplain);       // 退货退款理由
        RefundAssist.setImgGroup(order, imgs);                          // 退货退款纠纷图片，共存5张，以#号分隔
        orderService.update(order);

        BuyerDispute buyerDispute = new BuyerDispute();
        buyerDispute.setOrderId(orderCode);
        buyerDispute.setClientId(getCurCustomer().getId());
        buyerDispute.setClientName(getCurCustomer().getName());
        buyerDispute.setRefundStatusCode(OrderStatus.APPLY_REFUND_GOODS);
        buyerDispute.setGoodsStatus(1);
        buyerDispute.setReason(refundCause);
        buyerDispute.setMoney((long) (refundAmount * 100));
        buyerDispute.setRemark(refundExplain);
        buyerDisputeService.save(buyerDispute);

        ok("退货申请修改成功");
    }

    private void refundEditLoad(String code) {
        Order order = orderService.getOrderByCode(code);
        Trade trade = orderService.getTrade(order.getTradeId());
        Goods goods = goodsService.getByCode(order.getGoodsCode());

        // 退款金额明细
        List<Order> orders = orderService.getOrdersByTradeId(trade.getId());
        setAttr("detailPrice", RefundAssist.getDetailPrice(trade, order, orders));

        setAttr("trade", trade);
        setAttr("goods", goods);
        setAttr("order", order);

        setAttr("reason", BuyerDisputeAssist.splitReason(order));

        // 退款退货图片分离
        String refundImg = order.getRefundDisputeImg();
        setAttr("imgMap", BuyerDisputeAssist.getImgCroupMap(refundImg));
    }

    private void uploadImgFile(List<String> imgs, String[] refundImgs) {
        for (String img : refundImgs) {
            if (StrKit.notBlank(img)) {
                imgs.add(img);
            }
        }
        for (int i = 1; i <= 5; i++) {
            UploadFile file = getFile("refundImgUrl" + i, "");
            if (file != null) {
                String path = file.getSaveDirectory() + File.separator + file.getFileName();
                File temp = new File(path);
                if (temp.length() > Constant.MAX_IMAGE_SIZE){
                    error("图片文件太大,请重新上传！");
                    return;
                }
                String upFile = null;
                try {
                    upFile = PicUpload.picUpload(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (StrKit.isBlank(upFile)) {
                    error("图片上传失败,请重新上传！");
                    return;
                }
                if (upFile != null) {
                    imgs.add(upFile);
                }
            }
        }
    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private BuyerDisputeService buyerDisputeService;
}
