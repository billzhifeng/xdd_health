package com.xueduoduo.health.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
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
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public BaseResp login(@RequestBody LoginReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("登录请求:loginName:{}", req.getLoginName());
            //MOCK 验证用户名密码
            User user = UserSessionUtils.getUserFromSession();
            //            req.getSession().setAttribute(LoginConstantStr.Token_session_key, user);

            resp.setData(user);
            return resp;
        } catch (Exception e) {
            logger.error("登录异常,req:{}", req, e);
            return BaseResp.buildFailResp("登录失败", BaseResp.class);
        }
    }
}
