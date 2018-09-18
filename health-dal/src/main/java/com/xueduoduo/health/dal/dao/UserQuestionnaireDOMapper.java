package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDOExample;

public interface UserQuestionnaireDOMapper {
    long countByExample(UserQuestionnaireDOExample example);

    int insertSelective(UserQuestionnaireDO record);

    List<UserQuestionnaireDO> selectByExample(UserQuestionnaireDOExample example);

    UserQuestionnaireDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserQuestionnaireDO record);

    int updateAnswerCountAndOne(UserQuestionnaireDO record);

    int updateToSubmit(UserQuestionnaireDO record);

    int updateToDelete(UserQuestionnaireDO record);

    int updateToStartAnswer(UserQuestionnaireDO record);

    int updateByPrimaryKey(UserQuestionnaireDO record);
}
