package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SeekDao extends GeneralCodeDao<Seek, String> {
    public SeekDao(){
        super("seek", Seek.class, "SK", "seq_seek");
    }

    /**
     * 分页查询小批量定制
     * @param status  状态
     * @param pageParam 分页设置
     * @return
     */
    public PageList<Seek> findSeeks(SeekStatus status, int customerId, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select s.* , c.username from seek s ")
                .append(" left join customer c on s.customerId = c.id ")
                .append(" where 1 = 1 ");
        if(status != null) {
            sql.append(" and s.status = ? ");
            paras.add(status.getVal());
        }
        if(customerId > 0){
            sql.append(" and s.customerId = ? ");
            paras.add(customerId);
        }
        sql.append(" order by s.created desc ");

        return findPaging(sql, pageParam, paras);
    }

    public PageList<Seek> findAdminSeeks(String code, String goodsName, PageParam pageParam){
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from seek where 1=1 ");
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

    public Seek getSeekByPk(String code){
        return getByPK(code);
    }
}
