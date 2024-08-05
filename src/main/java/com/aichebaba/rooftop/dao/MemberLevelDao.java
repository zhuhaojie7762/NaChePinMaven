package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.MemberLevel;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class MemberLevelDao extends Dao<MemberLevel, Integer> {
    public MemberLevelDao(){
        super("member_level", MemberLevel.class);
    }

    public MemberLevel save(MemberLevel memberLevel) {
        if (memberLevel.getId() > 0) {
            update(memberLevel);
        } else {
            Object o = add(memberLevel);
            memberLevel.setId(Integer.parseInt(o.toString()));
        }
        return memberLevel;
    }

    public List<MemberLevel> getMemberLevelByIds(Collection<Integer> ids){
        return findByWhere(inSql("id", ids.size()), ids.toArray());
    }

}
