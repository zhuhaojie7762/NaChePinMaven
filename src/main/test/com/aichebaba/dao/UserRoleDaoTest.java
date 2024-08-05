package com.aichebaba.dao;

import com.aichebaba.rooftop.model.UserRole;
import junit.framework.TestCase;

import java.lang.annotation.Annotation;

public class UserRoleDaoTest extends TestCase {
    public void testSql() {
        Annotation[] ans = UserRole.class.getAnnotations();
        System.out.println(ans);
    }
}
