package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.TailGoods;
import com.aichebaba.rooftop.service.TailGoodsService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class TailController extends BaseController{
    @Autowired
    private TailGoodsService tailGoodsService;

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
        PageList<TailGoods> pager = tailGoodsService.findTails(code, goodsName, getPageParam());
        setAttr("pager", pager);
        render("tail.html");
    }

    public void delete(){
        String code = getPara("code", "");
        tailGoodsService.delete(code);
        ok("删除成功");
    }

    public void tailDetail() {
        String code = getPara("code");
        TailGoods tails = tailGoodsService.getTailGoods(code);
        setAttr("tails", tails);

        Customer sellerCustomer = customerService.getById(tails.getSellerId());
        setAttr("sellerCustomer", sellerCustomer);
    }
}
