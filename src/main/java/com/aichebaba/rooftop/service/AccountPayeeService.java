package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.AccountPayeeDao;
import com.aichebaba.rooftop.model.AccountPayee;
import com.aichebaba.rooftop.model.enums.AccountPayeeStatus;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 2016/8/15.
 */
@Service
public class AccountPayeeService {
    @Autowired
    private AccountPayeeDao accountPayeeDao;

    public AccountPayee save(AccountPayee accountPayee) {
        accountPayeeDao.add(accountPayee);
        return accountPayee;
    }

    public List<AccountPayee> findAccountPayeeList(Integer customerId) {
        return accountPayeeDao.findAccountPayeeList(customerId);
    }

    public AccountPayee findById(Integer id) {
        return accountPayeeDao.findById(id);
    }

    public Boolean update(AccountPayee accountPayee) {
        return accountPayeeDao.update(accountPayee);
    }

    public Object delete(Integer id) {
        return accountPayeeDao.delByIds(id);
    }

    public PageList<AccountPayee> findPayeePageList(AccountPayee ap, PageParam pageParam) {
        return accountPayeeDao.findPayeePageList(ap, pageParam);
    }

    public AccountPayee getDefaultAccountPayee(int customerId){
        List<AccountPayee> accountPayeeList = accountPayeeDao.findAccountPayeeList(customerId);
        List<AccountPayee> passList = new ArrayList<>();
        for(AccountPayee accountPayee : accountPayeeList){
            if(accountPayee.getStatus().equals(AccountPayeeStatus.PASS)){
                passList.add(accountPayee);
            }
        }
        if(passList.size() == 0){
            return null;
        }else{
            for(AccountPayee accountPayee : passList){
                if(accountPayee.getDefaultAccount()){
                    return accountPayee;
                }
            }
            return passList.get(0);
        }
    }

    public boolean exists(String byFields, Integer customerId) {
        return accountPayeeDao.exists(byFields, customerId);
    }

}
