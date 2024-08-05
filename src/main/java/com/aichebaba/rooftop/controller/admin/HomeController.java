package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.MyCaptchaRender;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.service.UserService;
import com.aichebaba.rooftop.utils.MD5;
import com.aichebaba.rooftop.vo.Json;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Tx;
import com.jfinal.core.ActionKey;
import com.jfinal.core.RequestMethod;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller("adminHomeController")
@Scope("prototype")
public class HomeController extends BaseController {

    @Autowired
    private UserService userService;

    public void index() {

    }

    @Clear
    @ActionKey("/admin/login.html")
    public void loginHtml() {
        render("login.html");
    }

    @Clear
    @ActionKey(value = "/admin/login", method = RequestMethod.POST)
    public void login() throws Exception {
        String username = getPara("username");
        String password = getPara("password");
        String passcode = getPara("passcode");
        if (!MyCaptchaRender.validate(this, passcode)) {
            setAttr("message", "验证证码错误");
            redirect("/admin/login.html");
            return;
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            user = userService.getByPhone(username);
        }
        if (user != null && user.getPassword().equals(MD5.md5(password, "UTF-8"))) {
            setCurUser(user);
            getSession().setMaxInactiveInterval(24 * 60 * 60);
            redirect("/admin");
        } else {
            setAttr("message", "用户名密码错误");
            redirect("/admin/login.html");
        }
    }

    @ActionKey("/admin/logout")
    public void logout() {
        setCurUser(null);
        removeSessionAttr("topMenus");
        removeSessionAttr("curTopMenu");
        removeSessionAttr("leftMenus");
        removeSessionAttr("curUser");
        redirect("/admin/login.html");
    }

    @Tx
    public void settingPwd() throws Exception {
        String oldPassword = getPara("oldPassword");
        String password = getPara("password");
        String againPassword = getPara("againPassword");

        if (StrKit.isBlank(oldPassword)) {
            renderJson(Json.error("旧密码不能为空"));
            return;
        }
        if (StrKit.isBlank(password)) {
            renderJson(Json.error("新密码不能为空"));
            return;
        }
        if (StrKit.isBlank(againPassword)) {
            renderJson(Json.error("确认密码不能为空"));
            return;
        }
        if (!password.equals(againPassword)) {
            renderJson(Json.error("新密码和确认密码不一致"));
            return;
        }

        User user = getCurUser();
        if (user.getPassword().equals(MD5.md5(oldPassword, "UTF-8"))) {
            user.setPassword(MD5.md5(password, "UTF-8"));
            userService.save(user);
            setCurUser(null);
            removeSessionAttr("topMenus");
            removeSessionAttr("curTopMenu");
            removeSessionAttr("leftMenus");
            removeSessionAttr("curUser");
            renderJson(Json.success("您的密码修改成功,请重新登录!"));
        } else {
            renderJson(Json.error("您的旧密码不正确，请重试！"));
        }
    }

    @Clear
    public void captcha() {
        render(new MyCaptchaRender(60, 22, 4, true));
    }
}
