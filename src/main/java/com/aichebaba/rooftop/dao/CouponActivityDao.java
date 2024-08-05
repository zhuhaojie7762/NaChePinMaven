package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.CouponActivity;
import com.aichebaba.rooftop.model.enums.CouponActivityStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.abego.treelayout.internal.util.java.lang.string.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
@Repository
public class CouponActivityDao extends Dao<CouponActivity, Integer> {

    public CouponActivityDao() {
        super("coupon_activity", CouponActivity.class);
    }

    public PageList<CouponActivity> findPager(String name, UseType useType, int money, Date from, Date end, int targetType, int memberLevelId,
                                              CouponActivityStatus status, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>();
        sql.append("select ca.*, ct.name couponName, ct.useType, ct.money from coupon_activity ca")
                .append(" left join coupon_template ct on ca.couponTemplateId=ct.id")
                .append(" where 1=1 ");
        if (StrKit.notBlank(name)) {
            sql.append(" and ca.name like ?");
            args.add(StringUtils.likeAllValue(name));
        }
        if (useType != null) {
            sql.append(" and ct.useType = ?");
            args.add(useType.name());
        }
        if (money > 0) {
            sql.append(" and ct.money = ?");
            args.add(money);
        }
        if (from != null) {
            sql.append(" and ca.startTime <= ?");
            args.add(from);
        }
        if (end != null) {
            sql.append(" and ca.endTime >= ?");
            args.add(end);
        }
        if (targetType > 0){
            sql.append(" and ca.targetType = ?");
            args.add(targetType);
        }
        if (memberLevelId > 0) {
            sql.append(" and FIND_IN_SET(?, ca.memberLevelId)");
            args.add(memberLevelId);
        }
//        else if (memberLevelId == -1) {
//            sql.append(" and ca.memberLevelId=0 and ca.memberRange is null");
//        } else if (memberLevelId == -2) {
//            sql.append(" and ca.memberLevelId=0 and ca.memberRange is not null");
//        }
        if (status != null) {
            sql.append(" and ca.status = ?");
            args.add(status.name());
        }
        return findPaging(sql, pageParam, args);
    }

    public List<CouponActivity> findActivitiesByTemplateId(int templateId) {
        return findWhere("couponTemplateId = ? ", templateId);
    }

    public List<CouponActivity> findCreatedCouponActivities() {
        return findWhere("status=?", CouponActivityStatus.CREATED.name());
    }

    public void timesFinishActivity() {
        update("update coupon_activity set `status`='FINISHED' where date_add(endTime, interval 1 day) < now() and `status` = 'IN';");
    }
}
