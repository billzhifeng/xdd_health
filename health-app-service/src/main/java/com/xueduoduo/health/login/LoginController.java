package com.xueduoduo.health.login;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.user.User;

/**
 * 登录、退出
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 上午11:59:41
 */
@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp login(HttpServletRequest req) {
        try {
            String loginName = (String) req.getParameter("loginName");
            logger.info("登录请求:loginName:{}", loginName);
            String passwd = (String) req.getParameter("passwd");
            //MOCK 验证用户名密码
            User user = new User();
            user.setUserName(loginName);
            user.setId(1L);
            user.setRole(UserRoleType.TEACHER.name());
            req.getSession().setAttribute(LoginConstant.Token_session_key, user);
            //
            return BaseResp.buildSuccessResp(BaseResp.class);
        } catch (Exception e) {
            logger.error("登录异常,attribute:{}", req.getAttributeNames(), e);
            return BaseResp.buildFailResp("登录失败", BaseResp.class);
        }
    }
}