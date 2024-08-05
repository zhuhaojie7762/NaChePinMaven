package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Notice;
import com.aichebaba.rooftop.model.enums.NoticeType;
import com.aichebaba.rooftop.service.NoticeService;
import com.aichebaba.rooftop.utils.Base64;
import com.aichebaba.rooftop.utils.MD5;
import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@Scope("prototype")
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;
    /**
     * 列表
     */
    public void index(){
        Map<String, Object> params = new HashMap<>();
        PageList<Notice> pager = noticeService.findNotices(params, getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    /**
     * 置顶
     */
    public void moveTop() {
        int id = getParaToInt("id");
        noticeService.moveTop(id);
        ok("置顶成功");
    }

    /**
     * 删除
     */
    public void del() {
        int id = getParaToInt("id");
        noticeService.delete(id, getCurUserId());
        ok("删除成功");
    }

    /**
     * 添加
     */
    public void add() {
        render("edit.html");
    }

    /**
     * 修改
     */
    public void edit() throws Exception {
        int id = getParaToInt("id", 0);
        Notice notice = noticeService.getById(id);
        setAttr("notice", notice);
        setAttr("imagePolicy", policy("image"));
        setAttr("filePolicy", policy("file"));
        render("edit.html");
    }

    /**
     * 预览
     */
    public void preview() {
        String title = getPara("title");
        String content = getPara("content");
        Notice notice;
        notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreated(new Date());
        setAttr("notice", notice);
        render("detail.html");
    }

    /**
     * 详情
     */
    public void detail(){
        int id = getParaToInt("id", 0);
        Notice notice = noticeService.getById(id);
        setAttr("notice", notice);
        render("detail.html");
    }

    /**
     * 保存
     */
    public void save(){
        int id = getParaToInt("id", 0);
        String title = getPara("title");
        String content = getPara("content");
        String type = getPara("type");
        String attachment = getPara("attachment");
        Notice notice;
        if (id == 0) {
            //新添
            notice = new Notice();
        } else {
            //修改
            notice = noticeService.getById(id);
        }
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(NoticeType.valueOf(type));
        notice.setAttachment(attachment);
        noticeService.save(notice);
        redirect("/admin/notice");
    }

    private Map<String, String> policy(String type) throws Exception {
        Map<String, String> param = new HashMap<>();
        Map<String, String> info = new HashMap<>();
        info.put("bucket", "nachepin-storage");
        info.put("expiration", String.valueOf(System.currentTimeMillis() + 60 * 60 - 8 * 60 * 60));
        if ("file".equals(type)) {
            info.put("save-key", "/file/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        } else {
            info.put("save-key", "/images/{year}/{mon}/{day}/{hour}/{min}/{random}{.suffix}");
        }
        String policy = JSON.toJSONString(info);
        policy = Base64.encode(policy, "UTF-8");
        String signature = policy + "&" + Constant.UPYUN_SIGNATURE;
        signature = MD5.md5(signature, "UTF-8");
        param.put("policy", policy);
        param.put("signature", signature);
        return param;
    }
}
