package com.xueduoduo.health.login;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.github.java.common.base.BaseResp;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.user.User;

public class MyInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

    public MyInterceptor() {
    }

    public MyInterceptor(Map<String, Set<String>> roleAndUrl, boolean isAuth, String ip) {
        this.roleAndUrl = roleAndUrl;
        this.isAuth = isAuth;
        this.ip = ip;
    }

    private Map<String, Set<String>> roleAndUrl;
    private boolean                  isAuth;
    private String                   ip;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("------preHandle------");
        //获取session
        HttpSession session = request.getSession(true);

        //vue后sessionId 每次不一样
        //        response.setHeader("Access-Control-Allow-Credentials", "true");
        //        response.setHeader("Access-Control-Allow-Origin", Arrays.asList(ip.split(",")));
        //        response.setHeader("Access-Control-Allow-Headers",
        //                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        //判断用户ID是否存在，不存在就跳转到登录界面
        if (session.getAttribute("userId") == null) {
            logger.info("未登录");
            BaseResp resp = BaseResp.buildBaseResp(ReturnCode.NOT_LOGIN.getCode(), "NOT LOGIN", BaseResp.class);
            response.getWriter().println(resp);
            return false;
        } else {
            session.setAttribute("userId", session.getAttribute("userId"));
            //角色权限
            if (isAuth) {
                User u = (User) session.getAttribute("userId");
                String uri = request.getRequestURI();
                uri = uri.substring(1, uri.length());
                String role = u.getRole();
                if ("MASTER".equals(role)) {
                    return true;
                }
                Set<String> urls = roleAndUrl.get(role);
                if (!urls.contains(uri)) {
                    logger.info("auth-->无权限 uri:{},role:{},userName:{},accountNo:{}", uri, u.getRole(), u.getUserName(),
                            u.getAccountNo());
                    BaseResp resp = BaseResp.buildBaseResp(ReturnCode.NO_AUTH.getCode(), "NOT AUTH", BaseResp.class);
                    response.getWriter().println(resp);
                    return false;
                }
                logger.info("auth-->有权限 uri:{},role:{},userName:{},accountNo:{}", uri, u.getRole(), u.getUserName(),
                        u.getAccountNo());
            }
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub

    }

}
