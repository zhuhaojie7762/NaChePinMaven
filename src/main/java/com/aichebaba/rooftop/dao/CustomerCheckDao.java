package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.CustomerCheck;
import com.aichebaba.rooftop.model.enums.CustomerAuditState;
import com.aichebaba.rooftop.model.enums.CustomerAuditType;
import com.aichebaba.rooftop.model.enums.CustomerState;
import com.aichebaba.rooftop.utils.StringUtils;
import com.aichebaba.rooftop.vo.CustomerApplyAudit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerCheckDao extends Dao<CustomerCheck, Integer> {
    public CustomerCheckDao() {
        super("customer_check", CustomerCheck.class);
    }

    public PageList<CustomerApplyAudit> findWaitCustomerChecks(String phone, String code, String name,
            CustomerAuditType type, CustomerAuditState state, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select ck.id, c.id customerId, c.code, c.`name`, c.username, c.phone, c.type, ck.created, ck.status state from customer_check ck")
                .append(" join customer c on ck.customerId=c.id where c.state=?");
        paras.add(CustomerState.normal.getVal());
        if (StrKit.notBlank(phone)) {
            sql.append(" and c.phone like ?");
            paras.add(StringUtils.likeValue(phone));
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and c.code like ?");
            paras.add(StringUtils.likeValue(code));
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and c.name like ?");
            paras.add(StringUtils.likeValue(name));
        }
        if (type != null) {
            sql.append(" and ck.type = ?");
            paras.add(type.name());
        }
        if (state != null) {
            sql.append(" and ck.status = ?");
            paras.add(state.ordinal());
        }
        pageParam.putSort("ck.created", "desc");
        return findPaging(sql, pageParam, paras, CustomerApplyAudit.class);
    }

    public CustomerCheck getByCustomerId(int customerId, CustomerAuditType type){
        return getByWhere("customerId = ? and type = ? ", customerId, type.name());
    }

}
