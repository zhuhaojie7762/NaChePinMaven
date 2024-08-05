package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Coupon;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
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
public class CouponDao extends Dao<Coupon, Integer> {

    public CouponDao() {
        super("coupon", Coupon.class);
    }

    /**
     * 获取我的优惠券
     * @param customerId    顾客ID
     * @param overDate      过期时间
     * @param used          是否已使用
     * @param status        优惠券模版状态 NORMAL:正常 DISABLE:停用
     * @param pageParam
     * @return
     */
    public PageList<Coupon> findCoupon(int customerId, Date overDate, Boolean used, CouponTemplateStatus status, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select c.* from coupon c ")
                .append(" join coupon_template ct on c.templateId = ct.id ")
                .append(" where 1 = 1 ");

        if (customerId > 0) {
            sql.append(" and c.customerId = ? ");
            paras.add(customerId);
        }
        if (overDate != null) {
            sql.append(" and ct.endTime >= ? ");
            paras.add(overDate);
        }
        if (used != null) {
            sql.append(" and c.used = ? ");
            paras.add(used);
        }
        if (status != null) {
            sql.append(" and ct.status = ? ");
            paras.add(status.name());
        }

        return findPaging(sql, pageParam, paras);
    }

    /**
     * 获取过期的优惠券
     * @param customerId    顾客ID
     * @param overDate      过期时间
     * @param pageParam
     * @return
     */
    public PageList<Coupon> findOldCoupon(int customerId, Date overDate, CouponTemplateStatus status, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select c.* from coupon c ")
                .append(" join coupon_template ct on c.templateId = ct.id ")
                .append(" where 1 = 1 ");
        sql.append(" and c.used = 0 ");

        if (customerId > 0) {
            sql.append(" and c.customerId = ? ");
            paras.add(customerId);
        }
        if (overDate != null) {
            sql.append(" and ct.endTime < ? ");
            paras.add(overDate);
        }
        if (status != null) {
            sql.append(" and ct.status = ? ");
            paras.add(status.name());
        }

        return findPaging(sql, pageParam, paras);
    }

    public List<Coupon> findEnableCoupon(int customerId, Date overDate, long amount) {
        String sql = "select c.* from coupon c " +
                " join coupon_template ct on ct.id=c.templateId" +
                " WHERE c.customerId=?" +
                " and c.used = '0'" +
                " and ct.startTime <= ?" +
                " and ct.endTime >= ?" +
                " and ct.condition <= ? and ct.status=?";
        return findSQL(sql, customerId, overDate, overDate, amount, CouponTemplateStatus.NORMAL.name());
    }

    public Long getCouponCountByActivity(int customerId, int activityId){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select count(*) from coupon ")
                .append(" where 1 = 1 ");
        if (customerId > 0) {
            sql.append(" and customerId = ? ");
            paras.add(customerId);
        }
        if (activityId > 0) {
            sql.append(" and activityId = ? ");
            paras.add(activityId);
        }
        return getSingle(sql.toString(), paras.toArray());
    }
}
