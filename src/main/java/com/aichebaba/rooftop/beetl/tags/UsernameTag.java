package com.aichebaba.rooftop.beetl.tags;

import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.service.UserService;
import com.jfinal.plugin.spring.SpringUtils;
import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;

public class UsernameTag extends GeneralVarTagBinding {
    @Override
    public void render() {
        int userId = (int) getAttributeValue("id");
        User user = SpringUtils.getBean(UserService.class).getById(userId);
        if (user != null) {
            try {
                ctx.byteWriter.writeString(user.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
