package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.NoticeDao;
import com.aichebaba.rooftop.model.Notice;
import com.aichebaba.rooftop.utils.DateUtil;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class NoticeService {
    @Autowired
    private NoticeDao noticeDao;

    /**
     * 分页获取公告信息
     * @param params
     *          id
     *          title           标题
     *          createdFrom     创建时间-开始
     *          createdEnd      创建时间-结束
     * @param pageParam
     * @return
     */
    public PageList<Notice> findNotices(Map<String, Object> params, PageParam pageParam){
        Date createdEnd = (Date) params.get("createdEnd");
        if (createdEnd != null) {
            params.put("createdEnd", DateUtil.getDayEnd(createdEnd));
        }

        //删除数据不显示
        params.put("state", 0);

        pageParam.putSort("sort", "desc");
        return noticeDao.findNotices(params, pageParam);
    }

    /**
     * 通过ID获取公告信息
     * @param noticeId   公告ID
     * @return  公告信息
     */
    public Notice getById(int noticeId) {
        return noticeDao.getByPK(noticeId);
    }

    /**
     * 保存公告信息
     * @param notice
     * @return
     */
    public Notice save(Notice notice) {
        if (notice.getId() > 0) {
            noticeDao.update(notice);
        } else {
            notice.setCreated(new Date());
            notice.setSort(new Date());
            notice.setState(0);
            Object o = noticeDao.add(notice);
            notice.setId(Integer.parseInt(o.toString()));
        }
        return notice;
    }

    /**
     * 置顶处理
     * @param noticeId
     */
    public void moveTop(int noticeId) {
        Notice notice = getById(noticeId);
        if (notice != null) {
            notice.setSort(new Date());
            save(notice);
        }
    }

    /**
     * 删除指定公告
     * @param noticeId
     */
    public void delete(int noticeId, int userId){
        Notice notice = getById(noticeId);
        if (notice != null) {
            notice.setState(1);
            notice.setDelTime(new Date());
            notice.setDelUserId(userId);
            save(notice);
        }
    }
}
