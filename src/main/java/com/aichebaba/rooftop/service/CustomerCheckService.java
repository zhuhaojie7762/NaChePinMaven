package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.CustomerCheckDao;
import com.aichebaba.rooftop.model.CustomerCheck;
import com.aichebaba.rooftop.model.enums.CustomerAuditType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerCheckService {
    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerCheckDao customerCheckDao;

    public CustomerCheck getByCustomerId(int customerId, CustomerAuditType type){
        return customerCheckDao.getByCustomerId(customerId, type);
    }

    public CustomerCheck save(CustomerCheck check) {
        if(check.getId()> 0 ){
            customerCheckDao.update(check);
        }else {
            check.setCreated(new Date());
            Object o = customerCheckDao.add(check);
            check.setId(Integer.parseInt(o.toString()));
        }
        return check;
    }
}
