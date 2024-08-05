package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.PickOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import com.aichebaba.rooftop.model.enums.PickOrderStatus;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PickOrderDao extends GeneralCodeDao<PickOrder, String> {
    public PickOrderDao() {
        super("pick_order", PickOrder.class, "PO", "seq_pick");
    }

    public PageList<PickOrder> findUnfinishedPeiOrder(String code, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> para = new ArrayList<>();

        sql.append("select * from pick_order ")
                .append(" where 1 = 1 ")
                .append(" and status = ? ");
        para.add(PickOrderStatus.DISTRIBUTION.getVal());
        if (StrKit.notBlank(code)) {
            sql.append(" and code = ? ");
            para.add(code);
        }
        return findPaging(sql, pageParam, para);
    }

    public PageList<PickOrder> findFinishedPeiOrder(String code, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> para = new ArrayList<>();

        sql.append("select * from pick_order ")
                .append(" where 1 = 1 ")
                .append(" and status = ? ");
        para.add(PickOrderStatus.FINISH.getVal());
        if (StrKit.notBlank(code)) {
            sql.append(" and code = ? ");
            para.add(code);
        }
        pageParam.putSort("code", "desc");
        return findPaging(sql, pageParam,  para);
    }

    public PageList<PickOrder> findPickOrder(PageParam pageParam) {
        String sql = "select * from pick_order where 1 = 1 and status <> 50 order by created desc";
        List<Object> paras = new ArrayList<>();

        return findPaging(sql, pageParam, paras);
    }
}
