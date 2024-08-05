package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Notice;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class NoticeDao extends Dao<Notice, Integer> {
    public NoticeDao(){
        super("notice", Notice.class);
    }

    /**
     * 分页获取公告信息
     * @param params
     *          id
     *          title           标题
     *          createdFrom     创建时间-开始
     *          createdEnd      创建时间-结束
     *          state           状态
     * @param pageParam
     * @return
     */
    public PageList<Notice> findNotices(Map<String, Object> params, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        Integer intTemp;
        String strTemp;
        Date dtTemp;

        sql.append("select * from notice n ")
                .append("where 1 = 1");

        //id
        intTemp = (Integer) params.get("id");
        if (intTemp != null && intTemp > 0) {
            sql.append(" and n.id = ? ");
            paras.add(intTemp);
        }

        //标题
        strTemp = (String) params.get("title");
        if (StrKit.notBlank(strTemp)) {
            sql.append(" and b.title like ? ");
            paras.add(StringUtils.likeAllValue(strTemp));
        }

        //创建时间-开始
        dtTemp = (Date) params.get("createdFrom");
        if (dtTemp != null) {
            sql.append(" and n.created >= ? ");
            paras.add(dtTemp);
        }

        //创建时间-结束
        dtTemp = (Date) params.get("createdEnd");
        if (dtTemp != null) {
            sql.append(" and n.created <= ? ");
            paras.add(dtTemp);
        }

        //状态
        intTemp = (Integer) params.get("state");
        if (intTemp != null) {
            sql.append(" and n.state = ? ");
            paras.add(intTemp);
        }

        return findPaging(sql, pageParam, paras);
    }
}
