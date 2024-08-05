package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.ExpressCompany;
import com.aichebaba.rooftop.model.enums.ExpressType;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

@Repository
public class ExpressCompanyDao extends Dao<ExpressCompany, Integer> {
    public ExpressCompanyDao(){
        super("express_company", ExpressCompany.class);
    }

    public ExpressCompany getFirstExpress(){
        StringBuilder sql = new StringBuilder();
        sql.append("select * from express_company ")
                .append(" where type = ? ")
                .append(" and display = 1 ")
                .append(" limit 1 ");
        return getBySQL(sql.toString(), ExpressType.express.getVal());
    }
}
