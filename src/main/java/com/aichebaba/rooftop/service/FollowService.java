package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.FollowDao;
import com.aichebaba.rooftop.model.Follow;
import com.aichebaba.rooftop.model.Goods;
import com.aichebaba.rooftop.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {
    @Autowired
    private FollowDao followDao;
    @Autowired
    private GoodsService goodsService;

    /**
     * 添加关注
     * @param customerId
     * @param goodsCode
     * @return
     */
    public Integer addFollow(int customerId, String goodsCode){
        Follow follow = new Follow();
        follow.setCustomerId(customerId);
        follow.setGoodsCode(goodsCode);
        Object o = followDao.add(follow);
        follow.setId(Integer.parseInt(o.toString()));

        int followCnt = followDao.getFollowCnt(goodsCode);
        goodsService.updateFollowCnt(goodsCode, followCnt);

        return followCnt;
    }

    /**
     * 取消关注
     * @param id
     */
    public Integer delFollow(int id, String goodsCode){
        followDao.deleteByPk(id);

        int followCnt = followDao.getFollowCnt(goodsCode);
        goodsService.updateFollowCnt(goodsCode, followCnt);
        return followCnt;
    }

    /**
     * 获取关注信息
     * @param customerId
     * @param goodsCode
     * @return
     */
    public Follow findFollow(int customerId, String goodsCode){
        return followDao.findFollow(customerId, goodsCode);
    }

    public void deleteFollow(int id){
        if (id > 0) {
            followDao.delByIds(id);
        }
    }

    public int getFollowCnt(String goodsCode){
        int count = followDao.getFollowCnt(goodsCode);
        return count;
    }

    public int delCnt(String goodeCode){
      int followCnt = followDao.getFollowCnt(goodeCode);
        if(followCnt >= 1){
            followCnt--;
            return followCnt;
        }else{
            return 0;
        }
    }
    /**
     * 获取关注情况
     * @param goodsList
     * @param customerId
     */
    public void setFollowInfo(List<Goods> goodsList, int customerId){
        for(Goods item : goodsList){
            if(customerId > 0) {
                Follow follow = findFollow(customerId, item.getCode());
                item.setFollowed(follow != null);
            }else{
                item.setFollowed(false);
            }
        }
    }

    /**
     * 获取关注情况
     * @param shoppingCarts
     * @param customerId
     */
    public void setFollowInfo2(List<ShoppingCart> shoppingCarts, int customerId){
        for(ShoppingCart item : shoppingCarts){
            if(customerId > 0) {
                Follow follow = findFollow(customerId, item.getGoodsCode());
                item.setFollowed(follow != null);
            }else{
                item.setFollowed(false);
            }
        }
    }
}
