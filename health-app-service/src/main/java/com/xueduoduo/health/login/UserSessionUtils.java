package com.xueduoduo.health.login;

import javax.servlet.http.HttpServletRequest;

import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.user.User;

/**
 * 获取用户登录的用户信息
 * 
 * @author wangzhifeng
 * @date 2018年8月11日 上午11:58:15
 */
public class UserSessionUtils {

    public static User getUserFromSession(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute(LoginConstantStr.Token_session_key);
        //MOCK
        user = new User();
        user.setId(1L);
        user.setLoginName("bill");
        user.setUserName("张三");
        user.setRole(UserRoleType.TEACHER.name());
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
