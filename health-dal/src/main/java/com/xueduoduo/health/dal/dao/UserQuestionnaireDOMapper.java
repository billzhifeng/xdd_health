package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDOExample;
import java.util.List;

public interface UserQuestionnaireDOMapper {
    long countByExample(UserQuestionnaireDOExample example);

    int insertSelective(UserQuestionnaireDO record);

    List<UserQuestionnaireDO> selectByExample(UserQuestionnaireDOExample example);

    UserQuestionnaireDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserQuestionnaireDO record);

    int updateByPrimaryKey(UserQuestionnaireDO record);
}