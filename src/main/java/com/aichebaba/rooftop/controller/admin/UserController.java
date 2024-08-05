package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.model.Role;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.UserRole;
import com.aichebaba.rooftop.model.enums.UserState;
import com.aichebaba.rooftop.service.RoleService;
import com.aichebaba.rooftop.service.UserService;
import com.aichebaba.rooftop.utils.MD5;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.aichebaba.rooftop.vo.Json;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@Scope("prototype")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public void index() {
        String stateName = getPara("state");
        String name = getPara("name");
        UserState state = null;
        if (StrKit.notBlank(stateName)) {
            state = UserState.valueOf(stateName);
        }
        PageList<User> pager = userService.findUsers(state, name, getPageParam());
        setAttr("pager", pager);
        setAttr("roles", roleService.getDisplayRoles());
        render("list.html");
    }

    public void edit() {
        int id = getParaToInt("id", 0);
        User user = userService.getById(id);
        setAttr("user", user);
        List<UserRole> userRoles = roleService.getUserRoles(user.getId());
        Set<Integer> userRoleIds = new HashSet<>();
        for (UserRole role : userRoles) {
            userRoleIds.add(role.getRoleId());
        }
        List<Role> roles = roleService.getDisplayRoles();
        setAttr("roles", roles);
        setAttr("roleIds", userRoleIds);
        String html = TemplateUtils.html("/admin/user/edit", getRequest());
        renderJson(Json.success2(html));
    }

    @Tx
    public void save() throws Exception {
        int id = getParaToInt("id", 0);
        String username = getPara("username");
        if (StrKit.isBlank(username)) {
            renderJson(Json.error("帐号不能为空"));
            return;
        }
        String name = getPara("name");
        if (StrKit.isBlank(name)) {
            renderJson(Json.error("姓名不能为空"));
            return;
        }
        String password = getPara("password");
        if (id == 0 && StrKit.isBlank(password)) {
            renderJson(Json.error("密码不能为这空"));
            return;
        }
        String phone = getPara("phone");
        if (StrKit.isBlank(phone)) {
            renderJson(Json.error("手机号不能空"));
            return;
        }
        String emergencyPhone = getPara("emergencyPhone");
        String stateName = getPara("state", "");
        UserState state = UserState.valueOf(stateName);
        Integer[] roleIds = getParaValuesToInt("role");
        String comment = getPara("comment");
        User user;
        if (id == 0) {
            user = userService.getByUsername(username);
            if(user != null){
                error("该账号已被注册");
                return;
            }
            user = new User();
            user.setUsername(username);
        } else {
            user = userService.getById(id);
        }
        user.setName(name);
        if (StrKit.notBlank(password)) {
            user.setPassword(MD5.md5(password, "UTF-8"));
        }
        user.setPhone(phone);
        user.setEmergencyPhone(emergencyPhone);
        user.setState(state);
        user.setComment(comment);
        user = userService.save(user);
        if (roleIds != null) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Integer roleId : roleIds) {
                userRoles.add(new UserRole(user.getId(), roleId));
            }
            roleService.saveUserRoles(user, userRoles);
        }
        renderJson(Json.success("保存成功！"));
    }

    public void selUser() {
        String name = getPara("name");
        PageList<User> pager = userService.findUsers(UserState.normal, name, getPageParam());
        setAttr("pager", pager);
        String html = TemplateUtils.html("/admin/user/sel.html", getRequest());
        renderText(html);
    }

    /**
     * 首页货品设置
     */
    @ActionKey("huopin_shizhi.html")
    public void huopin_shizhi(){render("huopin_shizhi.html");}

    /**
     * 首页品牌设置
     */
    @ActionKey("pinpai_shizhi.html")
    public void pinpai_shizhi(){render("pinpai_shizhi.html");}

    public void selList() {
        String name = getPara("name");
        PageList<User> pager = userService.findUsers(UserState.normal, name, getPageParam());
        setAttr("pager", pager);
        String html = TemplateUtils.html("/admin/user/sel-list.html", getRequest());
        success(html);
    }
}