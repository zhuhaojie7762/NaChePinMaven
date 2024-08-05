package com.aichebaba.rooftop.controller.web.notice;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Notice;
import com.aichebaba.rooftop.service.NoticeService;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by He.. on 2016/12/20.
 */
@Controller
@Scope("prototype")
public class ShowNoticeController extends BaseController {
    @ActionKey("noticeList.html")
    public void index() {
        Map<String, Object> params = new HashMap<>();
        PageList<Notice> notices = noticeService.findNotices(params, getPageParam());
        setAttr("notices", notices);
        render("noticeList.html");
    }

    @ActionKey("{id}.html")
    public void detail() {
        Notice notice = noticeService.getById(Integer.parseInt(getUrlPara("id")));
        setAttr("notice", notice);
        render("noticeDetail.html");
    }

    @Autowired
    private NoticeService noticeService;
}
