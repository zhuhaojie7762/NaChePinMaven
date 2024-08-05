package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.dao.he.persistance.AmosDB;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Banner;
import com.aichebaba.rooftop.model.enums.BannerType;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class BannerDao extends Dao<Banner, Integer> {
    public BannerDao(){
        super("banner", Banner.class);
    }

    public List<Banner> findBanner(AmosQuerier querier) {
        StringBuilder sql = new StringBuilder("select * from banner where 1 = 1");
        List<Object> paramList = new ArrayList<>();
        AmosDB.list(sql, paramList, querier);
        return findSQL(sql.toString(), paramList.toArray());
    }

    /**
     * 查询Banner
     * @param params
     *      type    类型
     * @return
     */
    public List<Banner> findBanner(Map<String, Object> params){
        StringBuilder sql = new StringBuilder();
        List<Object> para = new ArrayList<>();
        sql.append("select * from banner ")
                .append(" where 1 = 1 ");

        if(params.get("type") != null){
            BannerType type = (BannerType) params.get("type");
            sql.append(" and type = ? ");
            para.add(type.name());
        }
        sql.append(" order by type, sort ");
        return findSQL(sql.toString(), para.toArray());
    }

    /**
     * 删除指定ID以外的数据
     * @param ids
     */
    public void delOtherIds(List<Integer> ids){
        if(ids.size() > 0) {
            deleteByWhere(notInSql("id", ids.size()), ids.toArray());
        }else{
            deleteByWhere("1=1");
        }
    }
}
