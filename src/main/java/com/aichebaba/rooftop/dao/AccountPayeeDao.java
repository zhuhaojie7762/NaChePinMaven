package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.AccountPayee;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 2016/8/15.
 */
@Repository
public class AccountPayeeDao extends Dao<AccountPayee, Integer> {
    public AccountPayeeDao() {
        super("account_payee", AccountPayee.class);
    }

    public List<AccountPayee> findAccountPayeeList(Integer customerId) {
        return findByWhere("customerId = ?", customerId);
    }

    public AccountPayee findById(Integer id) {
        return getById(id);
    }

    public PageList<AccountPayee> findPayeePageList(AccountPayee ap, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM account_payee WHERE 1=1");
        List<Object> args = new ArrayList<>();

        if (StrKit.notBlank(ap.getUserName())) {
            sql.append(" AND userName like ?");
            args.add(StringUtils.likeAllValue(ap.getUserName()));
        }
        if (StrKit.notBlank(ap.getName())) {
            sql.append(" AND name like ?");
            args.add(StringUtils.likeAllValue(ap.getName()));
        }
        if (StrKit.notBlank(ap.getCompany())) {
            sql.append(" AND company like ?");
            args.add(StringUtils.likeAllValue(ap.getCompany()));
        }
        if (ap.getStatus() != null) {
            sql.append(" AND status = ?");
            args.add(ap.getStatus().getVal());
        }
        pageParam.putSort("id", "desc");
        return findPaging(sql, pageParam, args);
    }

    public boolean exists(String byFields, Integer customerId) {
        return findByWhere("payeeAccount = ? and customerId = ?", byFields, customerId).size() > 0;
    }
}
