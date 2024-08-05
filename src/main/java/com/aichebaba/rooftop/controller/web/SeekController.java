package com.aichebaba.rooftop.controller.web;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.aichebaba.rooftop.interceptor.web.WebLoginInterceptor;
import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.model.TailGoods;
import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.aichebaba.rooftop.model.enums.TailGoodsStatus;
import com.aichebaba.rooftop.service.SeekService;
import com.aichebaba.rooftop.service.TailGoodsService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;
import org.beetl.ext.format.NumberFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class SeekController extends BaseController {
    @Autowired
    private SeekService seekService;
    @Autowired
    private TailGoodsService tailGoodsService;

    @ActionKey("seek.html")
    public void seek(){
        if(getCurCustomer() == null){
            redirect("/");
            return;
        }
        render("seek.html");
    }

    @ActionKey("tailGoods.html")
    public void tailGoods(){
        if(getCurCustomer() == null){
            redirect("/");
            return;
        }
        render("tailGoods.html");
    }

    public void addSeek() throws Exception{
        String phone = getPara("phone");
        String qq = getPara("qq");
        String wangwang = getPara("wangwang");
        String goodsName = getPara("goodsName");
        String quantity = getPara("quantity");
        String price = getPara("price");
        Date needTime = getParaToDate("needTime");
        String material = getPara("material");
        String imgUrl = "";
        UploadFile file = getFile("imgUrl");
        if(file != null) {
            String localFile = file.getSaveDirectory() + File.separator + file.getFileName();
            if(!checkFileType(localFile)){
                error("文件类型不正确,请重新上传！");
                return;
            }
            String upFile = PicUpload.picUpload(localFile);
            if(StrKit.isBlank(upFile)){
                error("图片上传失败,请重新上传！");
                return;
            }
            imgUrl = upFile;
        }
        String remark = getPara("remark");

        if(StrKit.isBlank(phone)){
            error("联系电话有误!");
            return;
        }
        if(StrKit.isBlank(wangwang)){
            error("我的旺旺号有误!");
            return;
        }
        if(StrKit.isBlank(goodsName)){
            error("定制产品名称有误!");
            return;
        }
        if(StrKit.isBlank(quantity)){
            error("数量为整数!");
            return;
        }
        if(StrKit.isBlank(price)){
            error("报价有误!");
            return;
        }
        if(needTime == null){
            error("要货时间有误!");
            return;
        }
        if(StrKit.isBlank(remark)){
            error("定制商品的详细说明有误!");
            return;
        }

        Seek seek = new Seek();
        seek.setCustomerId(getCurCustomer().getId());
        seek.setContacts(getCurCustomer().getName());
        seek.setPhone(phone);
        seek.setQq(qq);
        seek.setWangwang(wangwang);
        seek.setGoodsName(goodsName);
        seek.setQuantity(quantity);
        seek.setPrice(price);
        seek.setNeedTime(needTime);
        seek.setMaterial(material);
        seek.setRemark(remark);
        seek.setStatus(SeekStatus.online);
        seek.setImgUrl(imgUrl);
        seekService.add(seek);
        ok("定制成功");
    }

    private Boolean checkFileType(String fileName){
        List<String> postfixs = new ArrayList<String>(){{
            add("jpg");
            add("gif");
            add("jpeg");
            add("bmp");
            add("png");
        }};
        int pos = fileName.lastIndexOf(".");
        String postfix = fileName.substring(pos + 1);
        for(String item : postfixs){
            if(item.equals(postfix.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @ActionKey("seekSuccess.html")
    public void seekSuccess(){
        setAttr("message", "恭喜您，定制成功！");
        render("/web/success.html");
    }

    public void addTailGoods() throws Exception{
        String company = getPara("company");
        String contacts = getPara("contacts");
        String phone = getPara("phone");
        String goodsName = getPara("goodsName");
        String quantity = getPara("quantity");
        String money = getPara("money");
        String material = getPara("material");
        String getMethod = getPara("getMethod");
        String comment = getPara("comment");

        if(StrKit.isBlank(company)){
            error("公司名有误!");
            return;
        }
        if(StrKit.isBlank(contacts)){
            error("联系人有误!");
            return;
        }
        if(StrKit.isBlank(phone)){
            error("联系电话有误!");
            return;
        }
        if(StrKit.isBlank(goodsName)){
            error("尾货产品名称有误!");
            return;
        }
        if(StrKit.isBlank(material)){
            error("材质有误!");
            return;
        }
        if(StrKit.isBlank(quantity)){
            error("数量有误!");
            return;
        }
        if(StrKit.isBlank(money)){
            error("总价有误!");
            return;
        }
        if(StrKit.isBlank(getMethod)){
            error("提货方式有误!");
            return;
        }
        if(StrKit.isBlank(comment)){
            error("产品说明!");
            return;
        }

        TailGoods tailGoods = new TailGoods();
        tailGoods.setSellerId(getCurCustomer().getId());
        tailGoods.setCompany(company);
        tailGoods.setContacts(contacts);
        tailGoods.setPhone(phone);
        tailGoods.setGoodsName(goodsName);
        tailGoods.setQuantity(quantity);
        tailGoods.setMoney(money);
        tailGoods.setMaterial(material);
        tailGoods.setComment(comment);
        tailGoods.setGetMethod(getMethod);
        tailGoods.setStatus(TailGoodsStatus.online);
        tailGoodsService.add(tailGoods);
        ok("尾货添加成功");
    }

    @ActionKey("tgSuccess.html")
    public void tgSuccess(){
        setAttr("message", "恭喜您，尾货添加成功！");
        render("/web/success.html");
    }

    @Before(WebLoginInterceptor.class)
    @ActionKey("seekList.html")
    public void seekList(){
        PageList<Seek> seeks = seekService.findSeeks(SeekStatus.online, 0, getPageParam());
        setAttr("pager", seeks);
        setAttr("customer", getCurCustomer());
        render("seekList.html");
    }

    @Before(WebLoginInterceptor.class)
    @ActionKey("tailGoodsList.html")
    public void tailGoodsList(){
        PageList<TailGoods> tailGoods = tailGoodsService.findTailGoods(TailGoodsStatus.online, 0, getPageParam());
        setAttr("pager", tailGoods);
        setAttr("customer", getCurCustomer());
        render("tailGoodsList.html");
    }

    @Before(WebLoginInterceptor.class)
    @ActionKey("seekDetails/{code}.html")
    public void seekDetails(){
        String code = getUrlPara("code");
        Seek seek = seekService.findSeekByCode(code);
        setAttr("seek", seek);
        setAttr("customer", getCurCustomer());
        render("seekDetails.html");
    }

    @Before(WebLoginInterceptor.class)
    @ActionKey("tailGoodsDetails/{code}.html")
    public void tailGoodsDetails(){
        String code = getUrlPara("code");
        TailGoods tailGoods = tailGoodsService.findTailGoodsByCode(code);
        setAttr("tail", tailGoods);
        setAttr("customer", getCurCustomer());
        render("tailGoodsDetails.html");
    }

}
