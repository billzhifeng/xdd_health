package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDOExample;
import java.util.List;

public interface UserQuestionAnswerDOMapper {
    long countByExample(UserQuestionAnswerDOExample example);

    int insertSelective(UserQuestionAnswerDO record);

    List<UserQuestionAnswerDO> selectByExample(UserQuestionAnswerDOExample example);

    UserQuestionAnswerDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserQuestionAnswerDO record);

    int updateByPrimaryKey(UserQuestionAnswerDO record);
}