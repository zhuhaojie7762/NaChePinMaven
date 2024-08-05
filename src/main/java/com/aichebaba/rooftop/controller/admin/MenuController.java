package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Menu;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Scope("prototype")
public class MenuController extends BaseController {

    public void switchMenu() {
        int menuId = getParaToInt("id");
        setCurTopMenu(menuId);
        List<Menu> leftMenus = getSessionAttr("leftMenus");
        Menu menu = leftMenus.get(0);
        success(menu.getUrl());
    }
}
