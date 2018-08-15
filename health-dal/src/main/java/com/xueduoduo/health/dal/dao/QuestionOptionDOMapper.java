package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;

public interface QuestionOptionDOMapper {
    long countByExample(QuestionOptionDOExample example);

    int insertSelective(QuestionOptionDO record);

    List<QuestionOptionDO> selectByExample(QuestionOptionDOExample example);

    QuestionOptionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionOptionDO record);

    int updateToDeleted(QuestionOptionDO record);

    int updateByPrimaryKey(QuestionOptionDO record);
}
