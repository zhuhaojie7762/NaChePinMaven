package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Follow;
import com.aichebaba.rooftop.model.ShoppingCart;
import com.aichebaba.rooftop.service.FollowService;
import com.aichebaba.rooftop.service.ShoppingCartService;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.awt.print.PrinterGraphics;
import java.util.List;

@Controller
@Scope("prototype")
public class BuyController extends BaseController {

    @Autowired
    private FollowService followService;

    /**
     * 关注
     */
    public void follow() {
        int customerId = curCustomerId();
        if (customerId == 0) {
            error("请先登录!");
            return;
        }
        String goodsCode = getPara("goodsCode", "");
        Follow follow = followService.findFollow(customerId, goodsCode);
        if (follow != null) {
            error("已关注");
            return;
        }
        int followCnt = followService.addFollow(customerId, goodsCode);
        ok("成功", followCnt);
    }

    /**
     * 批量关注
     */
    @Tx
    public void followAll() {
        int customerId = curCustomerId();
        if (customerId == 0) {
            error("请先登录!");
            return;
        }
        String tempCode = getPara("codes");
        if(StrKit.notBlank(tempCode)) {
            String[] codes = tempCode.split(",");
            for(String code : codes) {
                Follow follow = followService.findFollow(customerId, code);
                if (follow == null) {
                    followService.addFollow(customerId, code);
                }
            }
        }else{
            error("请先选择商品");
        }
        ok("成功");
    }

    public void delFollow(){
        int customerId = curCustomerId();
        String goodsCode = getPara("goodsCode", "");
        if (customerId == 0) {
            error("请先登录!");
            return;
        }
        Follow follow = followService.findFollow(customerId, goodsCode);
        int id = follow.getId();
        int followCnt = 0;
        if (follow != null ){
            followService.deleteFollow(id);
            followCnt = followService.getFollowCnt(goodsCode);
        }
        ok("成功", followCnt);
    }
}
