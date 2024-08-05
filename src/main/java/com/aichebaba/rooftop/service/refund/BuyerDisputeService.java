package com.aichebaba.rooftop.service.refund;

import com.aichebaba.rooftop.dao.refund.BuyerDisputeDao;
import com.aichebaba.rooftop.model.BuyerDispute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Andy on 2016/12/7.
 */
@Service
public class BuyerDisputeService {
    public List<BuyerDispute> findBuyerDispute(BuyerDispute buyerDispute) {
        return buyerDisputeDao.findBuyerDispute(buyerDispute);
    }

    public Object save(BuyerDispute buyerDispute) {
        buyerDispute.setCreateTime(new Date());
        return buyerDisputeDao.add(buyerDispute);
    }

    @Autowired
    private BuyerDisputeDao buyerDisputeDao;
}
