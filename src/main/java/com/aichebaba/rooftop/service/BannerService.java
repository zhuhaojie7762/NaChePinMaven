package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.BannerDao;
import com.aichebaba.rooftop.dao.he.persistance.AmosQuerier;
import com.aichebaba.rooftop.model.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BannerService {

    @Autowired
    private BannerDao bannerDao;

    public List<Banner> findBanner(AmosQuerier querier) {
        return bannerDao.findBanner(querier);
    }

    /**
     * 查询Banner
     * @param params
     *      type    类型
     * @return
     */
    public List<Banner> findBanner(Map<String, Object> params){
        return bannerDao.findBanner(params);
    }

    /**
     * 根据ID查询Banner
     * @param id
     * @return
     */
    public Banner getById(int id){
        return bannerDao.getById(id);
    }

    /**
     * 保存
     * @param banner
     * @return
     */
    public Banner save(Banner banner) {
        if (banner.getId() > 0) {
            bannerDao.update(banner);
        } else {
            Object o = bannerDao.add(banner);
            banner.setId(Integer.parseInt(o.toString()));
        }
        return banner;
    }

    /**
     * 删除指定ID以外的数据
     * @param ids
     */
    public void delOtherIds(List<Integer> ids){
        bannerDao.delOtherIds(ids);
    }
}
