package com.aichebaba.rooftop.dao.refund;

import com.aichebaba.rooftop.model.BuyerDispute;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created He Daoyuan on 2016/12/7.
 */
@Repository
public class BuyerDisputeDao extends Dao<BuyerDispute, Long> {
    public List<BuyerDispute> findBuyerDispute(BuyerDispute buyerDispute) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from buyer_dispute where 1 = 1");

        List<Object> params = new ArrayList<>();
        if (StrKit.notBlank(buyerDispute.getOrderId())) {
            sql.append(" and orderId = ?");
            params.add(buyerDispute.getOrderId());
        }

        sql.append(" order by sid desc");
        return findList(sql.toString(), params.toArray());
    }

    public BuyerDisputeDao() {
        super("buyer_dispute", BuyerDispute.class);
    }
}
