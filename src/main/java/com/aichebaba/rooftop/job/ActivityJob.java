package com.aichebaba.rooftop.job;

import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.CouponActivityStatus;
import com.aichebaba.rooftop.service.CouponService;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.MemberService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.vo.ActivityJobCustomerST;
import com.aichebaba.rooftop.vo.CouponActivityCondition;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.jfinal.kit.StrKit;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.aichebaba.rooftop.model.enums.CustomerState.normal;
import static com.aichebaba.rooftop.model.enums.CustomerType.buyer;
import static com.aichebaba.rooftop.model.enums.CustomerType.seller;

/**
 * @auther huwhy
 * @date 2016/5/11.
 */
public class ActivityJob {

    private CouponService couponService;
    private MemberService memberService;
    private CustomerService customerService;
    private OrderService orderService;

    private static Logger logger = LoggerFactory.getLogger(ActivityJob.class);

    public ActivityJob(CouponService couponService, MemberService memberService,
                       CustomerService customerService, OrderService orderService) {
        this.couponService = couponService;
        this.memberService = memberService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    public void execute(CouponActivity ca) {
        logger.info("coupon activity id-{} start", ca.getId());
        try {
            CouponActivityCondition condition = ca.getConditionJson();
//            int levelId = ca.getMemberLevelId();
            String[] levelIds = StrKit.notBlank(ca.getMemberLevelId()) ? ca.getMemberLevelId().split(",") : new String[0];
            String[] members = StrKit.notBlank(ca.getMemberRange()) ? ca.getMemberRange().split(",") : new String[0];
            CouponTemplate ct = couponService.getTemplate(ca.getCouponTemplateId());
            List<Customer> customers = new ArrayList<>();
            switch (ca.getTargetType()){
                case 1: //等级会员
                    List<Integer> ids = new ArrayList<>();
                    for(String sId : levelIds){
                        ids.add(Integer.parseInt(sId));
                    }
                    List<MemberLevel> levels = memberService.getMemberLevelByIds(ids);
                    Collection<Integer> levelList = Collections2.transform(levels, MemberLevel.levelValue);

                    customers.addAll(customerService.getCustomersByLevel(buyer, normal, levelList));
                    customers.addAll(customerService.getCustomersByLevel(seller, normal, levelList));
                    break;
                case 2: //所有会员
                    levels = memberService.findAllMemberLevel();
                    Collection<Integer> ls = Collections2.transform(levels, MemberLevel.levelValue);
                    customers.addAll(customerService.getCustomersByLevel(buyer, normal, ls));
                    customers.addAll(customerService.getCustomersByLevel(seller, normal, ls));
                    break;
                case 3://自主导入进货商
//                List<Customer> list = customerService.getCustomerByCodes(Arrays.asList(members));
                    List<Customer> list = customerService.findCustomerByUsernames(Arrays.asList(members));
                    customers.addAll(list);
                    break;
            }

            LocalDate from = null;
            LocalDate end = null;
            LocalDate now = LocalDate.now();
            Collection<Integer> customerIds = Collections2.transform(customers, Customer.idValue);
            from = getPreMonthStart(now);
            end = from.plusMonths(1);
            Map<Integer, ActivityJobCustomerST> monthMap = orderService.getActivityJobCustomerST(customerIds, from.toDate(),
                    end.toDate());
            from = getPreSeasonStart(now);
            end = from.plusMonths(3);
            Map<Integer, ActivityJobCustomerST> seasonMap = orderService.getActivityJobCustomerST(customerIds,
                    from.toDate(), end.toDate());
            List<Coupon> coupons = new ArrayList<>();
            for (Customer customer : customers) {
                //如果优惠券超出可发放数量，则不发放
                if(ct.getNum() < ct.getSendNum() + coupons.size() + 1){
                    break;
                }

                ActivityJobCustomerST monthSt = monthMap.containsKey(customer.getId()) ? monthMap.get(customer.getId()) : new ActivityJobCustomerST();
                ActivityJobCustomerST seasonSt = seasonMap.containsKey(customer.getId()) ? seasonMap.get(customer.getId()) : new ActivityJobCustomerST();

                if (ca.getAndOr()) {
                    if ((!chk(condition.getChkPreMonthMoney()) || condition.getPreMonthMoney() <= monthSt.getMoney().longValue())
                            && (!chk(condition.getChkPreMonthNum()) || condition.getPreMonthNum() <= monthSt.getNum())
                            && (!chk(condition.getChkPreSeasonMoney()) || condition.getPreSeasonMoney() <= seasonSt.getMoney().longValue())
                            && (!chk(condition.getChkPreSeasonNum()) || condition.getPreSeasonNum() <= seasonSt.getNum())
                            ) {
                        if(!couponService.customerActivityRepeatCheck(customer.getId(), ca.getId())) {
                            coupons.add(new Coupon(ct.getId(), customer.getId(), new Date(), ca.getId()));
                        }
                    }
                } else {
                    if ((chk(condition.getChkPreMonthMoney()) && condition.getPreMonthMoney() <= monthSt.getMoney().longValue())
                            || (chk(condition.getChkPreMonthNum()) && condition.getPreMonthNum() <= monthSt.getNum())
                            || (chk(condition.getChkPreSeasonMoney()) && condition.getPreSeasonMoney() <= seasonSt.getMoney().longValue())
                            || (chk(condition.getChkPreSeasonNum()) && condition.getPreSeasonNum() <= seasonSt.getNum())
                            ) {
                        if(!couponService.customerActivityRepeatCheck(customer.getId(), ca.getId())) {
                            coupons.add(new Coupon(ct.getId(), customer.getId(), new Date(), ca.getId()));
                        }
                    }
                }
            }
            couponService.saveCoupons(coupons);
            ct.setSendNum(ct.getSendNum() + coupons.size());
            couponService.saveTemplate(ct, null);
            ca.setStatus(CouponActivityStatus.IN);
            couponService.saveActivity(ca);
            logger.info("coupon activity id-{} end, size-{}", ca.getId(), coupons.size());
        } catch (Exception e) {
            logger.error("error in execute activity job", e);
            ca.setStatus(CouponActivityStatus.CREATED);
            couponService.saveActivity(ca);
        }
    }

    private boolean chk(Boolean chk) {
        return chk != null && chk;
    }

    private LocalDate getPreMonthStart(LocalDate now) {
        return now.minusMonths(1).withDayOfMonth(1);
    }

    private LocalDate getPreSeasonStart(LocalDate now) {
        return now.minusMonths(seasonMap.get(now.getMonthOfYear()) + 3).withDayOfMonth(1);
    }

    private Map<Integer, Integer> seasonMap = new HashMap<Integer, Integer>() {
        {
            //春
            put(3, 0);
            put(4, 1);
            put(5, 2);

            //夏
            put(6, 0);
            put(7, 1);
            put(8, 2);

            //秋
            put(9, 0);
            put(10, 1);
            put(11, 2);

            //冬
            put(12, 0);
            put(1, 1);
            put(2, 2);
        }
    };
}
