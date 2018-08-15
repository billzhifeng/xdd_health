package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.MenuDO;
import com.xueduoduo.health.dal.dataobject.MenuDOExample;
import java.util.List;

public interface MenuDOMapper {
    long countByExample(MenuDOExample example);

    int insertSelective(MenuDO record);

    List<MenuDO> selectByExample(MenuDOExample example);

    MenuDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MenuDO record);

    int updateByPrimaryKey(MenuDO record);
}