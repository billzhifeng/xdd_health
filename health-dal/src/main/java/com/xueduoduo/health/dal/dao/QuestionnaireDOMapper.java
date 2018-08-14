package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.QuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDOExample;

public interface QuestionnaireDOMapper {
    long countByExample(QuestionnaireDOExample example);

    int insertSelective(QuestionnaireDO record);

    List<QuestionnaireDO> selectByExample(QuestionnaireDOExample example);

    QuestionnaireDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionnaireDO record);

    int updateByDeleteOneQuestion(QuestionnaireDO record);

    int updateToDeleted(QuestionnaireDO record);

    int updateByPrimaryKey(QuestionnaireDO record);
}
