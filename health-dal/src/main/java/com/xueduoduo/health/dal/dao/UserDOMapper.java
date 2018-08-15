package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.UserDO;
import com.xueduoduo.health.dal.dataobject.UserDOExample;
import java.util.List;

public interface UserDOMapper {
    long countByExample(UserDOExample example);

    int insertSelective(UserDO record);

    List<UserDO> selectByExample(UserDOExample example);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}