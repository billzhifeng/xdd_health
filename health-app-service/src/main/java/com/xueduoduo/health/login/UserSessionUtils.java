package com.xueduoduo.health.login;

import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.user.User;

/**
 * 获取用户登录的用户信息
 * 
 * @author wangzhifeng
 * @date 2018年8月11日 上午11:58:15
 */
public class UserSessionUtils {

    public static User getUserFromSession() {
        User user = null;//(User) req.getSession().getAttribute(LoginConstantStr.Token_session_key);
        //MOCK
        user = new User();
        user.setId(1L);
        user.setAccountNo("LV0001");
        user.setUserName("管理员");
        user.setRole(UserRoleType.MASTER.name());
        user.setGender("MALE");
        user.setGradeNo(1);
        user.setClassNo(2);
        user.setHeaderImg("headImgUrl");
        user.setPhone("18012345678");
        user.setPosition(UserRoleType.TEACHER.name());
        //MOCK
        return user;
    }

}
