package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.SeekDao;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.Seek;
import com.aichebaba.rooftop.model.enums.SeekStatus;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SeekService {
    @Autowired
    private SeekDao seekDao;

    /**
     * 分页查询小批量定制
     * @param status  状态
     * @param pageParam 分页设置
     * @return
     */
    public PageList<Seek> findSeeks(SeekStatus status, int customerId, PageParam pageParam){
        return seekDao.findSeeks(status, customerId, pageParam);
    }

    public PageList<Seek> findAdminSeeks(String code, String goodsName, PageParam pageParam){
        return seekDao.findAdminSeeks(code, goodsName, pageParam);
    }

    public Seek findSeekByCode(String code){
        return seekDao.getByPK(code);
    }

    /**
     * 修改
     * @param seek
     * @return
     */
    public void update(Seek seek){
        seekDao.update(seek);
    }

    /**
     * 添加
     * @param seek
     * @return
     */
    public Seek add(Seek seek){
        seek.setCode(seekDao.getNextCode());
        seek.setCreated(new Date());
        seekDao.add(seek);
        return seek;
    }

    /**
     * 定制完成
     * @param code
     */
    public void end(String code){
        Seek seek = seekDao.getByPK(code);
        seek.setStatus(SeekStatus.offline);
        seek.setClosed(new Date());
        seekDao.update(seek);
    }

    public Seek getById(int id) {
        return seekDao.getById(id);
    }

    public void delete(String code){
        seekDao.deleteByWhere("code=?", code);
    }

    public Seek getSeekByPk(String code){
        return seekDao.getSeekByPk(code);
    }
}
