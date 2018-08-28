package com.xueduoduo.health.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * 登录、退出
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 上午11:59:41
 */
@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserRepository      userRepository;

    /**
     * 登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public BaseResp login(@RequestBody LoginReq req, HttpServletRequest request) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("登录请求:loginName:{}", req.getLoginName());
            User user = userRepository.loadByLoginName(req.getLoginName());
            if (null == user) {
                resp.setReturnCode(ReturnCode.DATA_NOT_EXIST.getCode());
                resp.setReturnMsg("登录名为 " + req.getLoginName() + " 用户不存在");
                return resp;
            }
            if (user.getPassword().equals(req.getPasswd())) {
                user.setPassword(null);
                HttpSession session = request.getSession();
                session.setAttribute("userId", user);
            } else {
                resp.setReturnCode(ReturnCode.DATA_NOT_EXIST.getCode());
                resp.setReturnMsg("用户名或密码不正确");
            }
            resp.setData(user);
            return resp;
        } catch (Exception e) {
            logger.error("登录异常,req:{}", req, e);
            return BaseResp.buildFailResp("登录失败", BaseResp.class);
        }
    }

    @RequestMapping("admin/other")
    public String other() {

        System.out.println("other");
        return "OTHER";
    }
}
