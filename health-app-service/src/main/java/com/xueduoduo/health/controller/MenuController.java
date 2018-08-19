package com.xueduoduo.health.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.java.common.base.BaseResp;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.menu.Menu;
import com.xueduoduo.health.domain.menu.MenuRepository;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 菜单 未使用
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 上午11:59:41
 */
@RestController
public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuRepository      menuRepository;

    /**
     * 菜单列表
     */
    @RequestMapping(value = "/admin/menus", method = RequestMethod.GET)
    public BaseResp menus(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            User user = UserSessionUtils.getUserFromSession();
            List<Menu> menus = menuRepository.loadUserAllMenus(user);
            resp.setData(menus);
            logger.info("user Id:{},role:{}", user.getId(), user.getRole(), JSON.toJSONString(menus));
        } catch (HealthException e) {
            logger.error("获取用户菜单异常", e);
            resp = BaseResp.buildFailResp("获取用户菜单异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("获取用户菜单异常", e);
            resp = BaseResp.buildFailResp("获取用户菜单异常", BaseResp.class);
        }
        return resp;
    }
}
