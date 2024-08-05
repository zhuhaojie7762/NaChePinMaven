package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.service.GoodsService;
import com.aichebaba.rooftop.service.SeekService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class CustomizedController extends BaseController{

    @Autowired
    private SeekService seekService;

    public void index(){
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String code = null;
        String goodsName = null;
        if (StrKit.notBlank(fieldName, value)) {
            if ("code".equals(fieldName)) {
                code = value;
            } else if ("goodsName".equals(fieldName)) {
                goodsName = value;
            }
        }
        PageList<Seek> pager = seekService.findAdminSeeks(code, goodsName, getPageParam());
        setAttr("pager", pager);
        render("customized.html");
    }

    public void delete(){
        String code = getPara("code", "");
        seekService.delete(code);
        ok("删除成功");
    }

    public void customizedDetail() {
        String code = getPara("code");
        Seek seeks = seekService.getSeekByPk(code);
        setAttr("seeks", seeks);

        Customer buyCustomer = customerService.getById(seeks.getCustomerId());
        setAttr("buyCustomer", buyCustomer);
    }
}

