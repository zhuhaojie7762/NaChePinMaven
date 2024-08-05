package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.TailGoods;
import com.aichebaba.rooftop.model.enums.TailGoodsStatus;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TailGoodsDao extends GeneralCodeDao<TailGoods, String> {
    public TailGoodsDao(){
        super("tail_goods", TailGoods.class, "TG", "seq_tail");
    }

    /**
     * 分页查询尾货处理
     * @param status  状态
     * @param pageParam 分页设置
     * @return
     */
    public PageList<TailGoods> findTailGoods(TailGoodsStatus status, int sellerId, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select t.*, c.username from tail_goods t ")
                .append(" left join customer c on t.sellerId = c.id ")
                .append(" where 1 = 1 ");
        if(status != null) {
            sql.append(" and t.status = ? ");
            paras.add(status.getVal());
        }
        if(sellerId > 0){
            sql.append(" and t.sellerId = ? ");
            paras.add(sellerId);
        }
        sql.append(" order by t.created desc ");

        return findPaging(sql, pageParam, paras);
    }

    public PageList<TailGoods> findTails(String code, String goodsName, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from tail_goods where 1=1 ");
        if(code != null) {
            sql.append(" and code = ? ");
            paras.add(code);
        }
        if(goodsName != null){
            sql.append(" and goodsName = ? ");
            paras.add(goodsName);
        }
        sql.append(" order by code desc ");

        return findPaging(sql, pageParam, paras);
    }

    public TailGoods getTailGoods(String code){
        return getByPK(code);
    }
}
