package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.DefaultExpressDao;
import com.aichebaba.rooftop.dao.ExpressCompanyDao;
import com.aichebaba.rooftop.model.DefaultExpress;
import com.aichebaba.rooftop.model.ExpressCompany;
import com.aichebaba.rooftop.model.enums.ExpressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpressCompanyService {
    @Autowired
    private ExpressCompanyDao expressCompanyDao;
    @Autowired
    private DefaultExpressDao defaultExpressDao;

    /**
     * 根据ID获取快递公司信息
     * @param id
     * @return
     */
    public ExpressCompany getExpressCompanyById(int id){
        return expressCompanyDao.getByPK(id);
    }

    public List<ExpressCompany> getExpressCompanies(ExpressType type) {
        return expressCompanyDao.findWhere("type = ? and display = 1 ", type.getVal());
    }

    public List<ExpressCompany> getAllExpressCompanies() {
        return expressCompanyDao.findAll();
    }

    public List<ExpressCompany> getAllExpressCompanies(ExpressType type) {
        return expressCompanyDao.findWhere("type = ? ", type.getVal());
    }

    public ExpressCompany getDefaultExpress(int customerId){
        DefaultExpress defaultExpress = defaultExpressDao.getByCustomerId(customerId);
        ExpressCompany expressCompany = null;
        if (defaultExpress != null) {
            expressCompany = expressCompanyDao.getById(defaultExpress.getExpressId());
            if(expressCompany != null && !expressCompany.getDisplay()){
                expressCompany = null;
            }
        }
        if (expressCompany == null) {
            expressCompany = expressCompanyDao.getByWhere("beDefault = '1' and display = '1' ");
        }
        if(expressCompany == null){
            expressCompany = expressCompanyDao.getFirstExpress();
        }
        return expressCompany;
    }

    public void changeDefaultExpress(int expressCompanyId, int customerId){
        DefaultExpress defaultExpress = defaultExpressDao.getByCustomerId(customerId);
        if (defaultExpress == null) {
            defaultExpress = new DefaultExpress();
            defaultExpress.setCustomerId(customerId);
            defaultExpress.setExpressId(expressCompanyId);
            defaultExpressDao.add(defaultExpress);
        }else{
            defaultExpress.setExpressId(expressCompanyId);
            defaultExpressDao.update(defaultExpress);
        }
    }

}
