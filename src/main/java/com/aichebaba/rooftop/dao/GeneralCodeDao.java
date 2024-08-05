package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.plugin.activerecord.dao.Dao;

public abstract class GeneralCodeDao<T, PK> extends Dao<T, PK> {

    private String codePre;

    private String seqName;

    public GeneralCodeDao(String tableName, Class beanClass, String codePre, String seqName) {
        super(tableName, beanClass);
        this.codePre = codePre;
        this.seqName = seqName;
    }

    public String getNextCode() {
        Integer nextVal = getSingle("select nextval(?)", seqName);
        return codePre + StringUtils.digitFormat(nextVal);
    }
}
