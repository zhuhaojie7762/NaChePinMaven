package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.MemberLevel;
import com.aichebaba.rooftop.model.enums.CustomerState;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.MemberService;
import com.jfinal.aop.Tx;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
@Scope("prototype")
public class MemberLevelController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CustomerService customerService;

    public void index() {
        List<MemberLevel> memberLevels = memberService.findAllMemberLevel();
        setAttr("memberLevels", memberLevels);
        Long totalCount = 0L;
        for (MemberLevel memberLevel : memberLevels) {
            totalCount += memberLevel.getMemberCount();
        }
        setAttr("totalCount", totalCount);
        render("level.html");
    }

    public void edit() {
        Long buyerCount = customerService.getCustomerCount(CustomerType.buyer, CustomerState.normal, null);
        setAttr("buyerCount", buyerCount);
        List<MemberLevel> memberLevels = memberService.findAllMemberLevel();
        setAttr("memberLevels", memberLevels);
        render("level_edit.html");
    }

    @Tx
    public void save() {
        Integer[] levels = getParaValuesToInt("level");
//        String[] names = getParaValues("name");
        String[] totalMoneys = getParaValues("totalMoney");
        String[] totalQuantitys = getParaValues("totalQuantity");
        String andOr = getPara("andOr");
        if (levels == null) {
            error("请至少添加一个会员等级");
            return;
        }
        for (int i = 0; i < levels.length; i++) {
            if (StrKit.isBlank(totalMoneys[i])) {
                error("请输入采购总额");
                return;
            }
            if (StrKit.isBlank(totalQuantitys[i])) {
                error("请输入采购单数");
                return;
            }
            if (i > 0) {
                if (Double.parseDouble(totalMoneys[i]) < Double.parseDouble(totalMoneys[i - 1])) {
                    error("采购总额大小不正确");
                    return;
                }

                if (Long.parseLong(totalQuantitys[i]) < Long.parseLong(totalQuantitys[i - 1])) {
                    error("采购单数大小不正确");
                    return;
                }
            }
        }
        for (int i = 0; i < levels.length; i++) {
            MemberLevel memberLevel = memberService.getMemberLevelByLevel(levels[i]);
            if(memberLevel == null){
                memberLevel = new MemberLevel();
                memberLevel.setLevel(levels[i]);
                memberLevel.setName("V" + (levels[i] - 1));
                memberLevel.setCreated(new Date());
            }
            memberLevel.setTotalMoney(Math.round(Double.parseDouble(totalMoneys[i]) * 100));
            memberLevel.setTotalQuantity(Long.parseLong(totalQuantitys[i]));
            memberLevel.setAndOr("1".equals(andOr));
            memberService.saveMemberLevel(memberLevel);
        }

        String setAll = getPara("setAll");
        if (setAll != null) {
            List<Customer> customers = customerService.findAllMember();
            for (Customer item : customers) {
                item.setLevel(0);
                customerService.save(item);
                customerService.upLevel(item);
            }
        }

        ok("保存成功");
    }
}
