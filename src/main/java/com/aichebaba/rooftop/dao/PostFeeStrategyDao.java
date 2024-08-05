package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.PostFeeStrategy;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostFeeStrategyDao extends Dao<PostFeeStrategy, PostFeeStrategy> {

    public PostFeeStrategyDao() {
        super("post_fee_strategy", PostFeeStrategy.class);
    }

    public List<PostFeeStrategy> getPostFeeStrategies(int expressId, int areaId) {
        return findByWhere("expressId=? and areaId=? and cityId=0 order by sn", expressId, areaId);
    }

    public List<PostFeeStrategy> getPostFeeStrategies(int expressId, int provinceId, int cityId) {
        return findByWhere("expressId=? and areaId=? and cityId=? order by sn", expressId, provinceId, cityId);
    }
}
