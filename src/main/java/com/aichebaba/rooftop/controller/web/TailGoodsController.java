package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.TailGoods;
import com.aichebaba.rooftop.service.TailGoodsService;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class TailGoodsController extends BaseController {

    @Autowired
    private TailGoodsService tailGoodsService;

    @ActionKey("list.html")
    public void list(){
        PageList<TailGoods> pager = tailGoodsService.findTailGoods(null, curCustomerId(), getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    public void details(){
        String code = getPara("code", "");
        TailGoods tailGoods = tailGoodsService.findTailGoodsByCode(code);
        setAttr("tail", tailGoods);
        setAttr("imgUrl", AppConfig.getImgPath());
        String html = TemplateUtils.html("web/member/tailGoods/details.html", getRequest());
        ok("", html);
    }

    public void endTail(){
        String code = getPara("code");
        tailGoodsService.end(code);
        ok("尾货下架成功");
    }
}
