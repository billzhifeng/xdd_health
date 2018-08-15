package com.xueduoduo.health.dal.dao;

import java.util.List;

import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDOExample;

public interface QuestionnaireLatitudeScoreDOMapper {
    long countByExample(QuestionnaireLatitudeScoreDOExample example);

    int insertSelective(QuestionnaireLatitudeScoreDO record);

    List<QuestionnaireLatitudeScoreDO> selectByExample(QuestionnaireLatitudeScoreDOExample example);

    QuestionnaireLatitudeScoreDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionnaireLatitudeScoreDO record);

    int updateToDeleted(QuestionnaireLatitudeScoreDO record);

    int updateByPrimaryKey(QuestionnaireLatitudeScoreDO record);
}
