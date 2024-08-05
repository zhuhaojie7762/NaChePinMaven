package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.job.ActivityJob;
import com.aichebaba.rooftop.model.CouponActivity;
import com.aichebaba.rooftop.model.CouponTemplate;
import com.aichebaba.rooftop.model.MemberLevel;
import com.aichebaba.rooftop.model.enums.CouponActivityStatus;
import com.aichebaba.rooftop.model.enums.CouponTemplateStatus;
import com.aichebaba.rooftop.model.enums.UseType;
import com.aichebaba.rooftop.service.CouponService;
import com.aichebaba.rooftop.service.MemberService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.utils.ExcelUtils;
import com.aichebaba.rooftop.vo.CouponActivityCondition;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.upload.UploadFile;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @auther huwhy
 * @date 2016/5/10.
 */
@Controller
@Scope("prototype")
public class CouponActivityController extends BaseController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ThreadPoolTaskExecutor poolTaskExecutor;

    @Autowired
    private OrderService orderService;

    public void index() {
        String name = getPara("name");
        String useTypeName = getPara("useType", "");
        int money = getParaToInt("money", 0);
        Date from = getParaToDate("from");
        Date end = getParaToDate("end");
        int memberLevelId = getParaToInt("memberLevelId", 0);
        String statusName = getPara("status", "");

        UseType useType = null;
        if (StrKit.notBlank(useTypeName)) {
            useType = UseType.valueOf(useTypeName);
        }
        CouponActivityStatus status = null;
        if (StrKit.notBlank(statusName)) {
            status = CouponActivityStatus.valueOf(statusName);
        }
        int targetType = 0;
        switch (memberLevelId){
            case -1:
                targetType = 2;
                memberLevelId = 0;
                break;
            case -2:
                targetType = 3;
                memberLevelId = 0;
                break;
            case 0:
                break;
            default:
                targetType = 1;
        }

        PageList<CouponActivity> pager = couponService.findCouponActivities(name, useType, money * 100, from, end,
                targetType, memberLevelId, status, getPageParam());

        List<MemberLevel> levels = memberService.findAllMemberLevel();
        Map<Integer, Long> levelCounts = new HashMap<>();
        Map<Integer, String> mapLevelNames = new HashMap<>();
        MemberLevel curLevel = null;
        for (MemberLevel l : levels) {
            levelCounts.put(l.getId(), l.getMemberCount());
            if (l.getId() == memberLevelId) {
                curLevel = l;
            }
            mapLevelNames.put(l.getId(), l.getName());
        }
        for (CouponActivity ca : pager.getData()) {
            Long count = couponService.getCouponCountByActivity(0, ca.getId());
            ca.setMemberNum(count == null ? 0 : count.intValue());
            if(ca.getTargetType() == 1){
                String[] levelList = ca.getMemberLevelId().split(",");
                List<String> levelNames = new ArrayList<>();
                for(String levelId : levelList){
                    levelNames.add(mapLevelNames.get(Integer.parseInt(levelId)));
                }
                ca.setMemberLevelNames(levelNames);
            }else{
                ca.setMemberLevelNames(new ArrayList<String>());
            }
        }
        setAttr("levels", levels);
        setAttr("curLevel", curLevel);
        setAttr("pager", pager);
        setAttr("useType", useType);
        setAttr("useTypes", UseType.values());
        setAttr("statusList", CouponActivityStatus.values());
        setAttr("status", status);
        render("list.html");
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        CouponActivity ac = couponService.getActivity(id);
        CouponTemplate ct = couponService.getTemplate(ac.getCouponTemplateId());
        setAttr("ct", ct);
        setAttr("ac", ac);
        List<CouponTemplate> templates = couponService.findEnableCouponTemplates(ct.getUseType(), new Date(), CouponTemplateStatus.NORMAL);
        setAttr("templates", templates);
        setAttr("useTypes", UseType.values());
        List<MemberLevel> levels = memberService.findAllMemberLevel();
        setAttr("levels", levels);
        if(StrKit.notBlank(ac.getMemberLevelId())){
            String[] levelIds = ac.getMemberLevelId().split(",");
            setAttr("levelIds", Arrays.asList(levelIds));
        }else{
            setAttr("levelIds", new ArrayList<String>());
        }
    }

    public void add() {
        setAttr("useTypes", UseType.values());
        List<MemberLevel> levels = memberService.findAllMemberLevel();
        setAttr("levels", levels);
    }

    public void edit() {
        int id = getParaToInt("id", 0);
        CouponActivity ac = couponService.getActivity(id);
        CouponTemplate ct = couponService.getTemplate(ac.getCouponTemplateId());
        setAttr("ct", ct);
        setAttr("ac", ac);
        List<CouponTemplate> templates = couponService.findEnableCouponTemplates(ct.getUseType(), new Date(), CouponTemplateStatus.NORMAL);
        setAttr("templates", templates);
        setAttr("useTypes", UseType.values());
        List<MemberLevel> levels = memberService.findAllMemberLevel();
        setAttr("levels", levels);
        if(StrKit.notBlank(ac.getMemberLevelId())){
            String[] levelIds = ac.getMemberLevelId().split(",");
            setAttr("levelIds", Arrays.asList(levelIds));
        }else{
            setAttr("levelIds", new ArrayList<String>());
        }
        render("edit.html");
    }

    public void save() throws IOException {
        int id = getParaToInt("id", 0);
        CouponActivity activity;
        CouponTemplate couponTemplate;
        if (id > 0) {
            activity = couponService.getActivity(id);
            couponTemplate = couponService.getTemplate(activity.getCouponTemplateId());
        } else {
            activity = new CouponActivity();
            String name = getPara("name");
            if (StrKit.isBlank(name)) {
                error("请填写名称");
                return;
            }
            activity.setName(name);
            String useTypeName = getPara("useType", "");
            int couponTemplateId = getParaToInt("couponTemplateId", 0);
            if (StrKit.isBlank(useTypeName) || couponTemplateId == 0) {
                error("请选择优惠券");
                return;
            }
            couponTemplate = couponService.getTemplate(couponTemplateId);
            if (couponTemplate == null) {
                error("请选择优惠券");
                return;
            }
            activity.setUseType(UseType.valueOf(useTypeName));
            activity.setCouponTemplateId(couponTemplateId);
            Date from = getParaToDate("from");
            Date end = getParaToDate("end");
            if (from == null || end == null) {
                error("请选择活动");
                return;
            }
            activity.setStartTime(from);
            activity.setEndTime(end);
            Boolean chkPreMonthMoney = getParaToBoolean("chkPreMonthMoney");
            BigDecimal preMonthMoney = getParaToBigDecimal("preMonthMoney", 0);
            Boolean chkPreMonthNum = getParaToBoolean("chkPreMonthNum");
            int preMonthNum = getParaToInt("preMonthNum", 0);
            Boolean chkPreSeasonMoney = getParaToBoolean("chkPreSeasonMoney");
            BigDecimal preSeasonMoney = getParaToBigDecimal("preSeasonMoney", 0);
            Boolean chkPreSeasonNum = getParaToBoolean("chkPreSeasonNum");
            int preSeasonNum = getParaToInt("preSeasonNum", 0);
            Boolean andOr = getParaToBoolean("andOr");
            if (chkPreMonthMoney == null && chkPreMonthNum == null && chkPreSeasonMoney == null && chkPreSeasonNum == null) {
                error("请选择参与条件");
                return;
            }
            if (andOr == null) {
                error("请选择多个参与条件");
                return;
            }
            CouponActivityCondition condition = new CouponActivityCondition();
            condition.setChkPreMonthMoney(chkPreMonthMoney);
            condition.setPreMonthMoney(preMonthMoney.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)).longValue());
            condition.setChkPreMonthNum(chkPreMonthNum);
            condition.setPreMonthNum(preMonthNum);
            condition.setChkPreSeasonMoney(chkPreSeasonMoney);
            condition.setPreSeasonMoney(preSeasonMoney.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)).longValue());
            condition.setChkPreSeasonNum(chkPreSeasonNum);
            condition.setPreSeasonNum(preSeasonNum);
            activity.setAndOr(andOr);
            activity.setCondition(JSON.toJSONString(condition));
        }
        int targetType = getParaToInt("targetType", 1);
        activity.setTargetType(targetType);
        Integer[] memberLevelIds = getParaValuesToInt("memberLevelId");

        UploadFile customerFile = getFile("members");
        boolean noNums = getParaToBoolean("noNums", false);
        long sum = 0;
        switch (targetType){
            //按会员等级
            case 1:
                List<MemberLevel> memberLevels = memberService.findAllMemberLevel();
                for (MemberLevel level : memberLevels) {
                    for(Integer levelId : memberLevelIds) {
                        if (level.getId() == levelId){
                            sum += level.getMemberCount();
                            break;
                        }
                    }
                }
                if (!noNums && (couponTemplate.getNum() - couponTemplate.getSendNum()) < sum) {
                    error(505, "所选择的优惠券余额不足，确定发放!");
                    return;
                }
                activity.setMemberLevelId(Joiner.on(',').join(memberLevelIds));
                activity.setMemberRange("");
                break;
            //所有会员
            case 2:
                memberLevels = memberService.findAllMemberLevel();
                for (MemberLevel level : memberLevels) {
                    sum += level.getMemberCount();
                }
                activity.setMemberLevelId("");
                activity.setMemberRange("");
                if (!noNums && (couponTemplate.getNum() - couponTemplate.getSendNum()) < sum) {
                    error(505, "所选择的优惠券余额不足，确定发放!");
                    return;
                }
                break;
            //自主添加进货商
            case 3:
                if (customerFile == null) {
                    error("请上传自主添加进货商文件");
                    return;
                }
                if (!customerFile.getFileName().endsWith("xls")) {
                    error("请上传excel文件");
                    return;
                }
                List<String> members = ExcelUtils.parseExcel(customerFile.getFile(), row -> {
                    if(row.getCell(0).getCellType() == 0){
                        Double cell = row.getCell(0).getNumericCellValue();
                        DecimalFormat decimalFormat = new DecimalFormat("#0");
                        return decimalFormat.format(cell) + "";
                    }else {
                        return row.getCell(0).getStringCellValue();
                    }
                });
                activity.setMemberLevelId("");
                activity.setMemberRange(Joiner.on(',').join(members));
                if (!noNums && (couponTemplate.getNum() - couponTemplate.getSendNum()) < members.size()) {
                    error(505, "所选择的优惠券余额不足，确定发放!");
                    return;
                }
                break;
            default:
                error("请选择参与对象");
                return;
        }

        activity = couponService.saveActivity(activity);
        startActivity(activity);
        ok("");
    }

    private void startActivity(final CouponActivity ca) {
        ca.setStatus(CouponActivityStatus.IN);
        couponService.saveActivity(ca);
        poolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Db.tx(() -> {
                    new ActivityJob(couponService, memberService, customerService, orderService).execute(ca);
                    return true;
                });
            }
        });
    }

    @ActionKey(method = RequestMethod.POST)
    public void finished() {
        int id = getParaToInt("id", 0);
        CouponActivity ac = couponService.getActivity(id);
        if (ac == null || ac.getStatus().equals(CouponActivityStatus.FINISHED)) {
            error("活动已结束");
            return;
        }
        ac.setStatus(CouponActivityStatus.FINISHED);
        couponService.saveActivity(ac);
        ok("");
    }

    @ActionKey(method = RequestMethod.POST)
    public void deleted() {
        int id = getParaToInt("id", 0);
        CouponActivity ac = couponService.getActivity(id);
        if (ac == null || !ac.getStatus().equals(CouponActivityStatus.FINISHED)) {
            error("活动未结束");
            return;
        }
        couponService.deleteActivity(ac);
        ok("");
    }
}
