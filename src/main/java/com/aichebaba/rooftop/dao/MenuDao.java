package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Menu;
import com.aichebaba.rooftop.model.enums.MenuStatus;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class MenuDao extends Dao<Menu, Integer> {
    public MenuDao() {
        super("menus", Menu.class);
    }

    public List<Menu> getFirstMenus() {
        return findByWhere("grade=? and status=? order by sort", 1, MenuStatus.Display.name());
    }

    public List<Menu> getSecondMenus(int parentId) {
        return findByWhere("grade=? and status=? and parentId=? and leaf=? order by sort", 2, MenuStatus.Display.name(),
                parentId, true);
    }

    public List<Menu> getSecondMenusByAuthIds(Collection<Integer> authIds) {
        if (authIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> para = new ArrayList<>();
        para.addAll(authIds);
        para.add(MenuStatus.Display.name());
        return findByWhere(inSql("authId", authIds.size()) + " and status=? order by sort",  para.toArray());
    }
}
