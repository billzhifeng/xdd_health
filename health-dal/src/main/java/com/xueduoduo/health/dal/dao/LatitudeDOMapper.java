package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.LatitudeDO;
import com.xueduoduo.health.dal.dataobject.LatitudeDOExample;
import java.util.List;

public interface LatitudeDOMapper {
    long countByExample(LatitudeDOExample example);

    int insertSelective(LatitudeDO record);

    List<LatitudeDO> selectByExample(LatitudeDOExample example);

    LatitudeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LatitudeDO record);

    int updateByPrimaryKey(LatitudeDO record);
}