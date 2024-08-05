package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.RoleAuthDao;
import com.aichebaba.rooftop.dao.RoleDao;
import com.aichebaba.rooftop.dao.UserRoleDao;
import com.aichebaba.rooftop.model.Role;
import com.aichebaba.rooftop.model.RoleAuth;
import com.aichebaba.rooftop.model.User;
import com.aichebaba.rooftop.model.UserRole;
import com.aichebaba.rooftop.model.enums.RoleStatus;
import com.google.common.collect.Collections2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleAuthDao roleAuthDao;

    public List<Role> getDisplayRoles() {
        return roleDao.findSQL("select * from roles where status=?", RoleStatus.Display.ordinal());
    }

    public List<UserRole> getUserRoles(int userId) {
        return userRoleDao.getUserRoles(userId);
    }

    public void saveUserRoles(User user, List<UserRole> userRoles) throws SQLException {
        List<UserRole> dbUserRoles = userRoleDao.getUserRoles(user.getId());
        List<UserRole> delList = new ArrayList<>();
        for (UserRole dbUserRole : dbUserRoles) {
            boolean exists = false;
            for (UserRole userRole : userRoles) {
                if (userRole.equals(dbUserRole)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                delList.add(dbUserRole);
            }
        }
        if (delList.size() > 0) {
            userRoleDao.deletes(delList.toArray(new UserRole[delList.size()]));
        }
        userRoleDao.adds(userRoles);
    }

    public Collection<Integer> getUserAuthIds(int userId) {
        List<UserRole> userRoles = getUserRoles(userId);
        Collection<Integer> roleIds = Collections2.transform(userRoles, UserRole.ROLE_ID_VALUE);
        List<RoleAuth> roleAuthList = roleAuthDao.getRoleAuth(roleIds);

        return Collections2.transform(roleAuthList, RoleAuth.AUTH_ID_VALUE);
    }
}
