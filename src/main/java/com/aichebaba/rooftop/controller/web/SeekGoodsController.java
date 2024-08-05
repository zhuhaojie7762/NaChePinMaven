package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SeekService;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class SeekGoodsController extends BaseController {
    @Autowired
    private SeekService seekService;

    @ActionKey("list.html")
    public void list(){
        PageList<Seek> pager = seekService.findSeeks(null, curCustomerId(), getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    public void details(){
        String code = getPara("code", "");
        Seek seek = seekService.findSeekByCode(code);
        setAttr("seek", seek);
        setAttr("imgUrl", AppConfig.getImgPath());
        String html = TemplateUtils.html("web/member/seekGoods/details.html", getRequest());
        ok("", html);
    }

    public void endSeek(){
        String code = getPara("code");
        seekService.end(code);
        ok("定制下架成功");
    }
}
