package com.xueduoduo.health.dal.dao;

import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDOExample;
import java.util.List;

public interface QuestionnaireLatitudeScoreDOMapper {
    long countByExample(QuestionnaireLatitudeScoreDOExample example);

    int insertSelective(QuestionnaireLatitudeScoreDO record);

    List<QuestionnaireLatitudeScoreDO> selectByExample(QuestionnaireLatitudeScoreDOExample example);

    QuestionnaireLatitudeScoreDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionnaireLatitudeScoreDO record);

    int updateByPrimaryKey(QuestionnaireLatitudeScoreDO record);
}