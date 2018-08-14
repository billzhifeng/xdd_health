package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDO;
import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDOExample;
import java.util.List;

public interface RoleUrlMappingScoreDOMapper {
    long countByExample(RoleUrlMappingScoreDOExample example);

    int insertSelective(RoleUrlMappingScoreDO record);

    List<RoleUrlMappingScoreDO> selectByExample(RoleUrlMappingScoreDOExample example);

    RoleUrlMappingScoreDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleUrlMappingScoreDO record);

    int updateByPrimaryKey(RoleUrlMappingScoreDO record);
}