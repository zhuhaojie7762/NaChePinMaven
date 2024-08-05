package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.AreaDao;
import com.aichebaba.rooftop.dao.CustomerDao;
import com.aichebaba.rooftop.dao.OrderDao;
import com.aichebaba.rooftop.model.Area;
import com.jfinal.aop.Tx;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("prototype")
public class AreaController extends BaseController {

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderDao orderDao;

    public void index() {

        PageList<Area> pager = areaDao.findAreas(getPara("name"), getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    @Tx
    public void setPickUser() {
        int id = getParaToInt("id", 0);
        int pickUserId = getParaToInt("pickUserId", 0);
        if (id == 0 || pickUserId == 0) {
            error("数据错误");
            return;
        }
        Area area = areaDao.getById(id);
        if (area != null) {
            List<Integer> buyerIds = customerDao.findBuyerIdsByAddress(area.getName());
            orderDao.updatePicker(buyerIds, pickUserId);
            area.setPickUserId(pickUserId);
            areaDao.update(area);
        }
        ok("操作成功");
    }
}
