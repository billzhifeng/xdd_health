package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.UrlDO;
import com.xueduoduo.health.dal.dataobject.UrlDOExample;
import java.util.List;

public interface UrlDOMapper {
    long countByExample(UrlDOExample example);

    int insertSelective(UrlDO record);

    List<UrlDO> selectByExample(UrlDOExample example);

    UrlDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UrlDO record);

    int updateByPrimaryKey(UrlDO record);
}