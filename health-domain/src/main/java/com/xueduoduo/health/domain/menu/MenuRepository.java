package com.xueduoduo.health.domain.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xueduoduo.health.dal.dao.MenuDOMapper;
import com.xueduoduo.health.dal.dataobject.MenuDO;
import com.xueduoduo.health.dal.dataobject.MenuDOExample;
import com.xueduoduo.health.domain.url.RoleUrlMapCache;
import com.xueduoduo.health.domain.url.UrlCache;
import com.xueduoduo.health.domain.user.User;

/**
 * 未使用
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 下午12:04:22
 */
@Service
public class MenuRepository {
    private static final Logger     logger    = LoggerFactory.getLogger(MenuRepository.class);
    @Autowired
    private MenuDOMapper            menuDOMapper;
    @Autowired
    private UrlCache                urlCache;
    @Autowired
    private RoleUrlMapCache         roleUrlCache;

    private Map<String, List<Menu>> roleMenus = new HashMap<String, List<Menu>>();

    /**
     * 查询用户菜单
     * 
     * @param user
     * @return
     */
    public List<Menu> loadUserAllMenus(User user) {

        //角色
        String userRole = user.getRole();
        if (!CollectionUtils.isEmpty(roleMenus.get(userRole))) {
            return roleMenus.get(userRole);
        }

        MenuDOExample ex = new MenuDOExample();
        MenuDOExample.Criteria cri = ex.createCriteria();
        List<MenuDO> dos = menuDOMapper.selectByExample(ex);

        Collections.sort(dos, Comparator.comparing(MenuDO::getLevel));
        List<Menu> menus = new ArrayList<Menu>();
        if (!CollectionUtils.isEmpty(dos)) {
            Map<Long, Menu> map = new HashMap<Long, Menu>();
            for (MenuDO src : dos) {

                if (0 == src.getLevel()) {
                    //该角色包含该功能
                    boolean flag = roleUrlCache.containUrl(userRole, src.getUrlId());
                    if (flag) {
                        Menu menu = convert(src);
                        map.put(src.getId(), menu);
                        menus.add(menu);
                    }
                } else if (1 == src.getLevel()) {
                    //该角色包含该功能
                    boolean flag = roleUrlCache.containUrl(userRole, src.getUrlId());
                    if (flag && null != map.get(src.getParentId())) {
                        Menu menu = map.get(src.getParentId());
                        menu.addSubMenu(convert(src));
                    }
                }
            }
        }
        roleMenus.put(userRole, menus);
        return menus;
    }

    private Menu convert(MenuDO src) {
        Menu tar = new Menu();
        BeanUtils.copyProperties(src, tar);
        tar.setUrl(urlCache.urlMap.get(src.getId()));
        return tar;
    }
}
