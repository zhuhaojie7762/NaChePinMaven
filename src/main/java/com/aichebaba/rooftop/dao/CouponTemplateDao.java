package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.CouponTemplate;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.aichebaba.rooftop.model.enums.UseType;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
@Repository
public class CouponTemplateDao extends Dao<CouponTemplate, Integer> {
    public CouponTemplateDao() {
        super("coupon_template", CouponTemplate.class);
    }

    /**
     * 查询优惠券
     * @param name      优惠券名称
     * @param activityName   活动名称
     * @param from      有效期（开始）
     * @param end       有效期（结束）
     * @param condition 使用条件
     * @param num       总发行量
     * @param money     面值
     * @param status    状态
     * @param pageParam
     * @return
     */
    public PageList<CouponTemplate> findPager(String name, String activityName, Date from, Date end, int condition,
                                              int num, int money, CouponTemplateStatus status, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();

        sql.append("select * from coupon_template ct ")
                .append(" where 1 = 1 ");
        if (StrKit.notBlank(name)) {
            sql.append(" and ct.name like ? ");
            param.add(StringUtils.likeAllValue(name));
        }
        if (StrKit.notBlank(activityName)) {
            sql.append(" and ct.id in( ")
                    .append(" select couponTemplateId from coupon_activity ")
                    .append(" where name like ? ) ");
            param.add(StringUtils.likeAllValue(activityName));
        }
        if (from != null) {
            sql.append(" and (ct.startTime >= ? or (ct.startTime <= ? and ct.endTime >= ?)) ");
            param.add(from);
            param.add(from);
            param.add(from);
        }
        if (end != null) {
            sql.append(" and (ct.endTime <= ? or (ct.startTime <= ? and ct.endTime >= ?)) ");
            param.add(end);
            param.add(end);
            param.add(end);
        }
        if (condition > 0) {
            sql.append(" and ct.condition >= ? ");
            param.add(condition);
        }
        if(num > 0){
            sql.append(" and ct.num = ? ");
            param.add(num);
        }
        if(money > 0){
            sql.append(" and ct.money = ? ");
            param.add(money);
        }
        if(status != null){
            sql.append(" and ct.status = ? ");
            param.add(status.name());
        }

        return findPaging(sql, pageParam, param);
    }

    public List<Long> getCouponMoney(){
        StringBuilder sql = new StringBuilder();

        sql.append("select money from coupon_template ")
                .append(" group by money ")
                .append(" order by money ");

        return findSingleList(sql.toString());
    }

    public List<CouponTemplate> findEnableCouponTemplates(UseType useType, Date start, CouponTemplateStatus status) {
        return findByWhere(" useType = ? and endTime >= ? and status = ? ", useType.name(), start, status.name());
    }

    public Long getTemplateCount(String name, double money, int templateId){
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) cnt from coupon_template ")
                .append(" where name = ? ")
                .append(" and money = ? ")
                .append(" and id <> ? ");
        return getSingle(sql.toString(), name, (int) money * 100, templateId);
    }


}
