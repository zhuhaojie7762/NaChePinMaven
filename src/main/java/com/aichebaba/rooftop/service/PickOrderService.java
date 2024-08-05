package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.PickOrderDao;
import com.aichebaba.rooftop.dao.TradeDao;
import com.aichebaba.rooftop.model.PickOrder;
import com.aichebaba.rooftop.model.Trade;
import com.aichebaba.rooftop.model.enums.PickOrderStatus;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PickOrderService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PickOrderDao pickOrderDao;

    @Autowired
    private TradeDao tradeDao;

    public PageList<PickOrder> findPickOrder(PageParam pageParam) {
        return pickOrderDao.findPickOrder(pageParam);
    }

    /**
     * 创建配货单
     *
     * @param pickerId 拣货员ID
     * @return
     */
    public PickOrder createPickOrder(int pickerId) {
        PickOrder pickOrder = new PickOrder();
        pickOrder.setCode(pickOrderDao.getNextCode());
        pickOrder.setCreated(new Date());
        pickOrder.setPickerId(pickerId);
        pickOrder.setStatus(PickOrderStatus.PICK_ORDER);
        pickOrder.setLastTime(new Date());
        pickOrderDao.add(pickOrder);
        return pickOrder;
    }

    /**
     * 获取未完成配货单列表
     *
     * @param code 配送单编号
     * @return
     */
    public PageList<PickOrder> findUnfinishedPeiOrder(String code, PageParam pageParam) {
        return pickOrderDao.findUnfinishedPeiOrder(code, pageParam);
    }

    /**
     * 获取已完成配货单列表
     *
     * @param code 配送单编号
     * @return
     */
    public PageList<PickOrder> findFinishedPeiOrder(String code, PageParam pageParam) {
        return pickOrderDao.findFinishedPeiOrder(code, pageParam);
    }

    /**
     * 获取配货单内未发货的总订单
     *
     * @param pickOrderCode 配货单编号
     * @return
     */
    public List<Trade> findTradesByPickOrderCode(String pickOrderCode, String tradeCode) {
        int tradeId = 0;
        if(StrKit.notBlank(tradeCode)){
            tradeId = Trade.code2id(tradeCode);
        }
        return tradeDao.findTradesByPickOrderCode(pickOrderCode, tradeId);
    }

    /**
     * 根据编号获取配货单
     *
     * @param code 配货单编号
     * @return
     */
    public PickOrder getByCode(String code) {
        return pickOrderDao.getByPK(code);
    }

    /**
     * 修改配货单状态
     */
    public void updatePickOrder(PickOrder pickOrder) {
        pickOrderDao.update(pickOrder);
    }
}
