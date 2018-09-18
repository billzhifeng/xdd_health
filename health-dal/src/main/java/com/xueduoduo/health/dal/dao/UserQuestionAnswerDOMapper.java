package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDOExample;

public interface UserQuestionAnswerDOMapper {
    long countByExample(UserQuestionAnswerDOExample example);

    int insertSelective(UserQuestionAnswerDO record);

    List<UserQuestionAnswerDO> selectByExample(UserQuestionAnswerDOExample example);

    UserQuestionAnswerDO selectByPrimaryKey(Long id);

    int updateToDeleted(UserQuestionAnswerDO record);

    int updateByPrimaryKeySelective(UserQuestionAnswerDO record);

    int updateByPrimaryKey(UserQuestionAnswerDO record);
}
