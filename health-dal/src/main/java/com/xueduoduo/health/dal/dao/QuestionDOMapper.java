package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionDOExample;

public interface QuestionDOMapper {
    long countByExample(QuestionDOExample example);

    int insertSelective(QuestionDO record);

    List<QuestionDO> selectByExample(QuestionDOExample example);

    QuestionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionDO record);

    int updateAddLatitude(QuestionDO record);

    int updateToDeleted(QuestionDO record);

    int updateByPrimaryKey(QuestionDO record);
}
