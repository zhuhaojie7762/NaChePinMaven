package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.MemberLevelDao;
import com.aichebaba.rooftop.interceptor.web.RedirectInterceptor;
import com.aichebaba.rooftop.model.MemberLevel;
import com.aichebaba.rooftop.model.enums.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MemberService  {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Autowired
    private CustomerService customerService;

    public List<MemberLevel> findAllMemberLevel() {
        List<MemberLevel> memberLevels = memberLevelDao.findAll();
        for (MemberLevel item : memberLevels) {
            Long mc = customerService.getCustomerCount(null, CustomerState.normal, item.getLevel());
            item.setMemberCount(mc);
        }
        return memberLevels;
    }

    public MemberLevel getMemberLevelByLevel(Integer level) {
        return memberLevelDao.getByWhere("level = ? ", level);
    }

    public MemberLevel saveMemberLevel(MemberLevel memberLevel) {
        return memberLevelDao.save(memberLevel);
    }

    public MemberLevel getMemberLevelById(int memberLevelId) {
        return memberLevelDao.getById(memberLevelId);
    }

    /**
     * 批量获取会员等级信息
     * @param ids
     * @return
     */
    public List<MemberLevel> getMemberLevelByIds(Collection<Integer> ids) {
        return memberLevelDao.getMemberLevelByIds(ids);
    }
}
