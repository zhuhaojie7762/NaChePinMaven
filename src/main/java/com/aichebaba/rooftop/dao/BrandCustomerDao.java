package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.BrandCustomer;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017-1-6.
 */
@Repository
public class BrandCustomerDao extends Dao<BrandCustomer, Integer> {
    public BrandCustomerDao() {
        super("brand_customer", BrandCustomer.class);
    }

    public BrandCustomer getBrandCustomerBy2Id(int brandId, int customerId) {
        return getByWhere("brandId = ? and customerId = ?", brandId, customerId);
    }
}
