package com.xueduoduo.health.domain.questionnaire.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.UserQuestionAnswerDOMapper;
import com.xueduoduo.health.dal.dao.UserQuestionnaireDOMapper;
import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDOExample;
import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireAnswerStatus;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.UserQuestionAnswer;
import com.xueduoduo.health.domain.questionnaire.UserQuestionnaire;

/**
 * @author wangzhifeng
 * @date 2018年8月15日 上午10:56:36
 */
@Service
public class UserQuestionnaireRepository {

    @Autowired
    private UserQuestionnaireDOMapper  userQuestionnaireDOMapper;

    @Autowired
    private UserQuestionAnswerDOMapper userQuestionAnswerDOMapper;

    //查询每个学生完成问卷对答题情况
    public List<UserQuestionnaire> summaryStudentAnsweredQuestionnaire(Long questionnaireId) {

        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        cri.andAnswerStatusEqualTo(QuestionnaireAnswerStatus.DONE.name());//完成

        List<UserQuestionnaireDO> uqs = userQuestionnaireDOMapper.selectByExample(example);

        List<UserQuestionnaire> list = new ArrayList<UserQuestionnaire>();
        for (UserQuestionnaireDO src : uqs) {
            list.add(convertUserQuestionnaire(src));
        }
        return list;
    }

    private UserQuestionnaire convertUserQuestionnaire(UserQuestionnaireDO src) {
        UserQuestionnaire tar = new UserQuestionnaire();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    private UserQuestionnaireDO convertUserQuestionnaireDO(UserQuestionnaire src) {
        UserQuestionnaireDO tar = new UserQuestionnaireDO();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 保存用户答题结果
     */
    @Transactional
    public void saveUserQuestionAnswer(UserQuestionnaire uq, List<UserQuestionAnswer> answers) {

        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(uq.getQuestionnaireId());
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        cri.andUserIdEqualTo(uq.getUserId());
        List<UserQuestionnaireDO> uqs = userQuestionnaireDOMapper.selectByExample(example);
        JavaAssert.isTrue(CollectionUtils.isNotEmpty(uqs), ReturnCode.DB_ERROR,
                "学生对问卷Id=" + uq.getQuestionnaireId() + "答题结果不存在", HealthException.class);
        JavaAssert.isTrue(1 == uqs.size(), ReturnCode.DB_ERROR, "学生对问卷Id=" + uq.getQuestionnaireId() + "答题结果不唯一",
                HealthException.class);

        UserQuestionnaireDO uqd = uqs.get(0);

        UserQuestionnaireDO uqdo = convertUserQuestionnaireDO(uq);
        uqdo.setId(uqd.getId());

        int count = userQuestionnaireDOMapper.updateByPrimaryKeySelective(uqdo);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR,
                "更新用户问卷答题结果异常,问卷ID=" + uq.getQuestionnaireId() + ",学生id=" + uq.getUserId(), HealthException.class);

        for (UserQuestionAnswer an : answers) {
            UserQuestionAnswerDO ud = convertUserQuestionAnswerDO(an);
            count = userQuestionAnswerDOMapper.insertSelective(ud);
            JavaAssert.isTrue(
                    1 == count, ReturnCode.DB_ERROR, "保存问题答案异常,问卷ID=" + uq.getQuestionnaireId() + ",问题id="
                            + ud.getQuestionId() + ",答案id=" + ud.getOptionId() + ",学生id=" + uq.getUserId(),
                    HealthException.class);
        }

    }

    private UserQuestionAnswerDO convertUserQuestionAnswerDO(UserQuestionAnswer src) {
        UserQuestionAnswerDO tar = new UserQuestionAnswerDO();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 查询学生问卷
     * 
     * @param studentId
     * @return
     */
    public List<UserQuestionnaire> loadStudentUserQuestionnaires(Long studentId) {
        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        cri.andUserIdEqualTo(studentId);
        List<UserQuestionnaireDO> uqs = userQuestionnaireDOMapper.selectByExample(example);

        List<UserQuestionnaire> list = new ArrayList<UserQuestionnaire>();
        for (UserQuestionnaireDO src : uqs) {
            list.add(convertUserQuestionnaire(src));
        }
        return list;
    }

    /**
     * 查询学习对该题目的选项
     * 
     * @param questionnaireId
     * @param studentId
     * @param questionId
     */
    public Long loadUserQuestionAnswer(Long questionnaireId, Long studentId, Long questionId) {

        UserQuestionAnswerDOExample example = new UserQuestionAnswerDOExample();
        UserQuestionAnswerDOExample.Criteria cri = example.createCriteria();
        cri.andUserIdEqualTo(studentId);
        cri.andQuestionIdEqualTo(questionnaireId);
        cri.andQuestionIdEqualTo(questionId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserQuestionAnswerDO> answer = userQuestionAnswerDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(answer)) {
            return null;
        }
        JavaAssert.isTrue(answer.size() == 1, ReturnCode.PARAM_ILLEGLE,
                "题目ID=" + questionId + ",学生id=" + studentId + ",存在多个选中选项", HealthException.class);
        return answer.get(0).getId();
    }
}
