package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.MenuDao;
import com.aichebaba.rooftop.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuDao menuDao;

    public List<Menu> getFirstMenus() {
        return menuDao.getFirstMenus();
    }

    public Menu getMenuById(int id) {
        return menuDao.getById(id);
    }

    public List<Menu> getSecondMenus(int parentId) {
        return menuDao.getSecondMenus(parentId);
    }

    public List<Menu> getSecondMenusByAuthIds(Collection<Integer> autIds) {
        return menuDao.getSecondMenusByAuthIds(autIds);
    }
}
