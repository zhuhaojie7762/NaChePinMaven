package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Follow;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FollowDao extends Dao<Follow, Integer> {
    public FollowDao(){
        super("follow", Follow.class);
    }

    public Follow findFollow(int customerId, String goodsCode){
        return getByWhere(" customerId = ? and goodsCode = ?", customerId, goodsCode);
    }

    public Integer getFollowCnt(String goodsCode){
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(*) followCnt from follow ")
                .append(" where goodsCode = ? ");
        List<Long> vals = findSingleList(sql.toString(), goodsCode);
        long followCnt = vals.get(0);
        return (int) followCnt;
    }

}
