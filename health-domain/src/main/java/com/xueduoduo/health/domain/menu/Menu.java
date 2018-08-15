package com.xueduoduo.health.domain.menu;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 菜单
 * 
 * @author wangzhifeng
 * @date 2018年8月10日 下午12:01:20
 */
@Setter
@Getter
@ToString
public class Menu {
    private Long       id;

    private Integer    level;

    private Long       parentId;

    private String     displayName;

    private Long       urlId;

    private String     viewType;

    private String     url;

    private List<Menu> subMenus = new ArrayList<Menu>();

    public void addSubMenu(Menu m) {
        this.getSubMenus().add(m);
    }
}
