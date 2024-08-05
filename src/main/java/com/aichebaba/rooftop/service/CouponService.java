package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.CouponActivityDao;
import com.aichebaba.rooftop.dao.CouponDao;
import com.aichebaba.rooftop.dao.CouponTemplateDao;
import com.aichebaba.rooftop.model.Coupon;
import com.aichebaba.rooftop.model.CouponActivity;
import com.aichebaba.rooftop.model.CouponTemplate;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.enums.CouponActivityStatus;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @auther huwhy
 * @date 2016/5/6.
 */
@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    @Autowired
    private CouponActivityDao couponActivityDao;

    public void saveCoupon(Coupon coupon) {
        if (coupon.getId() > 0) {
            couponDao.update(coupon);
        } else {
            coupon.setUsed(false);
            couponDao.add(coupon);
        }
    }

    public void saveCoupons(Collection<Coupon> coupons) {
        for (Coupon coupon : coupons) {
            saveCoupon(coupon);
        }
    }

    public void usedCoupon(int couponId) {
        Coupon coupon = couponDao.getById(couponId);
        if (coupon != null) {
            coupon.setUsed(true);
            coupon.setUsedTime(new Date());
            couponDao.update(coupon);
        }
    }

    /**
     * 查询优惠券
     *
     * @param name         优惠券名称
     * @param activityName 活动名称
     * @param from         有效期（开始）
     * @param end          有效期（结束）
     * @param condition    使用条件
     * @param num          总发行量
     * @param money        面值
     * @param status       状态
     * @param pageParam
     * @return
     */
    public PageList<CouponTemplate> findCouponTemplates(String name, String activityName,
                                                        Date from, Date end, int condition,
                                                        int num, int money, CouponTemplateStatus status,
                                                        PageParam pageParam) {
        return couponTemplateDao.findPager(name, activityName, dateTimeToDate(from), dateTimeToDate(end), condition, num, money, status, pageParam);
    }

    public Boolean templateRepeatCheck(String name, double money, int templateId) {
        Long templateCount = couponTemplateDao.getTemplateCount(name, money, templateId);
        return templateCount > 0;
    }

    /**
     * 获取有效的优惠券
     * @param useType
     * @return
     */
    public List<CouponTemplate> findEnableCouponTemplates(UseType useType, Date start, CouponTemplateStatus status) {
        return couponTemplateDao.findEnableCouponTemplates(useType, dateTimeToDate(start), status);
    }

    public CouponTemplate getTemplate(int templateId) {
        return couponTemplateDao.getById(templateId);
    }

    public void saveTemplate(CouponTemplate template, User curUser) {
        if (template.getId() > 0) {
            couponTemplateDao.update(template);
        } else {
            template.setCreated(new Date());
            template.setCreateUserId(curUser == null ? 0 : curUser.getId());
            couponTemplateDao.add(template);
        }
    }

    public void disableTemplate(int templateId, User curUser) {
        CouponTemplate template = couponTemplateDao.getById(templateId);
        if (template != null) {
            template.setStatus(CouponTemplateStatus.DISABLE);
            couponTemplateDao.update(template);
        }
    }

    public void enableTemplate(int templateId, User curUser) {
        CouponTemplate template = couponTemplateDao.getById(templateId);
        if (template != null) {
            template.setStatus(CouponTemplateStatus.NORMAL);
            couponTemplateDao.update(template);
        }
    }

    public PageList<CouponActivity> findCouponActivities(String name, UseType useType, int money,
                                                         Date from, Date end, int targetType, int memberLevelId,
                                                         CouponActivityStatus status, PageParam pageParam) {
        return couponActivityDao.findPager(name, useType, money, dateTimeToDate(from), dateTimeToDate(end), targetType, memberLevelId, status, pageParam);
    }

    public List<CouponActivity> findCreatedCouponActivities() {
        return couponActivityDao.findCreatedCouponActivities();
    }

    public CouponActivity saveActivity(CouponActivity activity) {
        if (activity.getId() > 0) {
            couponActivityDao.update(activity);
        } else {
            activity.setCreated(new Date());
            activity.setStatus(CouponActivityStatus.CREATED);
            Object id = couponActivityDao.add(activity);
            activity.setId(Integer.valueOf(id.toString()));
        }
        return activity;
    }

    public void finishedActivity(int activityId, User curUser) {
        CouponActivity activity = getActivity(activityId);
        if (activity != null) {
            activity.setStatus(CouponActivityStatus.FINISHED);
        }
    }

    public CouponActivity getActivity(int id) {
        return couponActivityDao.getById(id);
    }

    public void deleteActivity(CouponActivity activity) {
        couponActivityDao.delete(activity);
    }

    public void timesFinishActivity() {
        couponActivityDao.timesFinishActivity();
    }

    public List<Long> getCouponMoney() {
        return couponTemplateDao.getCouponMoney();
    }

    public List<CouponActivity> findActivitysByTemplateId(int templateId) {
        return couponActivityDao.findActivitiesByTemplateId(templateId);
    }

    /**
     * 我的优惠券
     */
    public PageList<Coupon> findCoupon(int customerId, Date overDate, Boolean used, CouponTemplateStatus status, PageParam pageParam) {
        PageList<Coupon> pager = couponDao.findCoupon(customerId, dateTimeToDate(overDate), used, status, pageParam);
        for(Coupon coupon : pager.getData()){
            CouponTemplate template = couponTemplateDao.getById(coupon.getTemplateId());
            coupon.setCouponTemplate(template);
        }
        return pager;
    }

    public List<Coupon> findEnableCoupon(int customerId, Date overDate, long amount) {
        List<Coupon> coupons = couponDao.findEnableCoupon(customerId, dateTimeToDate(overDate), amount);
        for (Coupon coupon : coupons) {
            CouponTemplate template = couponTemplateDao.getById(coupon.getTemplateId());
            coupon.setCouponTemplate(template);
        }
        return coupons;
    }

    /**
     * 过期的优惠券
     */
    public PageList<Coupon> findOldCoupon(int customerId, Date overDate, CouponTemplateStatus status, PageParam pageParam) {
        PageList<Coupon> pager = couponDao.findOldCoupon(customerId, dateTimeToDate(overDate), status, pageParam);
        for(Coupon coupon : pager.getData()){
            CouponTemplate template = couponTemplateDao.getById(coupon.getTemplateId());
            coupon.setCouponTemplate(template);
        }
        return pager;
    }

    public Coupon getCoupon(int couponId) {
        Coupon coupon = couponDao.getById(couponId);
        if (coupon != null) {
            CouponTemplate template = couponTemplateDao.getById(coupon.getTemplateId());
            coupon.setCouponTemplate(template);
        }
        return coupon;
    }

    /**
     * 检查优惠券是否已发
     * @param customerId
     * @param activityId
     * @return
     */
    public Boolean customerActivityRepeatCheck(int customerId, int activityId) {
        if (activityId == 0) {
            return false;
        }
        return couponDao.getCouponCountByActivity(customerId, activityId) > 0;
    }

    public Long getCouponCountByActivity(int customerId, int activityId) {
        return couponDao.getCouponCountByActivity(customerId, activityId);
    }

    public Date dateTimeToDate(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateTime);
        Date date = null;
        try {
            date = formatter.parse(dateString);
        }catch (ParseException ex){}

        return date;
    }
}
