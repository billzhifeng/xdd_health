package com.xueduoduo.health.domain.questionnaire.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.questionnaire.UserQuestionAnswer;
import com.xueduoduo.health.domain.questionnaire.UserQuestionnaire;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * 用户问卷
 * 
 * @author wangzhifeng
 * @date 2018年8月15日 上午10:56:36
 */
@Service
public class UserQuestionnaireRepository {

    @Autowired
    private UserQuestionnaireDOMapper  userQuestionnaireDOMapper;

    @Autowired
    private UserQuestionAnswerDOMapper userQuestionAnswerDOMapper;

    @Autowired
    private UserRepository             userRepository;

    /**
     * 删除问卷对应的学生答案
     */
    @Transactional
    public void deleteQuestionAnswer(Long questionnaireId) {

        //删除答卷
        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        List<UserQuestionnaireDO> uqs = userQuestionnaireDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(uqs)) {
            for (UserQuestionnaireDO src : uqs) {
                userQuestionnaireDOMapper.updateToDelete(src);
            }
        }

        //删除答案
        UserQuestionAnswerDOExample example2 = new UserQuestionAnswerDOExample();
        UserQuestionAnswerDOExample.Criteria cri2 = example2.createCriteria();
        cri2.andQuestionnaireIdEqualTo(questionnaireId);
        cri2.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserQuestionAnswerDO> answer = userQuestionAnswerDOMapper.selectByExample(example2);
        if (CollectionUtils.isNotEmpty(answer)) {
            for (UserQuestionAnswerDO src2 : answer) {
                userQuestionAnswerDOMapper.updateToDeleted(src2);
            }
        }
    }

    /**
     * 查询每个学生完成问卷对答题情况
     * 
     * @param questionnaireId
     * @return
     */
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

        UserQuestionnaireDO uqd = null;
        //如果没有就新增
        if (CollectionUtils.isEmpty(uqs)) {
            Date now = new Date();
            UserQuestionnaireDO create = new UserQuestionnaireDO();
            create.setAnswerStatus(QuestionnaireAnswerStatus.INIT.name());
            create.setCount(0);
            create.setCreatedTime(now);
            create.setUpdatedTime(now);
            create.setCreateor("管理员");
            create.setIsDeleted(IsDeleted.N.name());
            create.setQuestionnaireId(uq.getQuestionnaireId());
            create.setUserId(uq.getUserId());
            userQuestionnaireDOMapper.insertSelective(create);

            uqd = create;
        } else {
            uqd = uqs.get(0);
        }

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

    private UserQuestionAnswer convertUserQuestionAnswerDO(UserQuestionAnswerDO src) {
        UserQuestionAnswer tar = new UserQuestionAnswer();
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
    public UserQuestionAnswerDO loadUserQuestionAnswer(Long questionnaireId, Long studentId, Long questionId) {

        UserQuestionAnswerDOExample example = new UserQuestionAnswerDOExample();
        UserQuestionAnswerDOExample.Criteria cri = example.createCriteria();
        cri.andUserIdEqualTo(studentId);
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andQuestionIdEqualTo(questionId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserQuestionAnswerDO> answer = userQuestionAnswerDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(answer)) {
            return null;
        }
        JavaAssert.isTrue(answer.size() == 1, ReturnCode.PARAM_ILLEGLE,
                "题目ID=" + questionId + ",学生id=" + studentId + ",存在多个选中选项", HealthException.class);
        return answer.get(0);
    }

    /**
     * 查询学生对问卷的所有答案
     * 
     * @param questionnaireId
     * @param studentId
     * @param questionId
     */
    public List<UserQuestionAnswer> loadUserQuestionOptionsAnswers(Long questionnaireId, Long studentId) {

        UserQuestionAnswerDOExample example = new UserQuestionAnswerDOExample();
        UserQuestionAnswerDOExample.Criteria cri = example.createCriteria();
        if (null != studentId) {
            cri.andUserIdEqualTo(studentId);
        }
        if (null != questionnaireId) {
            cri.andQuestionnaireIdEqualTo(questionnaireId);
        }
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UserQuestionAnswerDO> answers = userQuestionAnswerDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(answers)) {
            return null;
        }

        List<UserQuestionAnswer> list = new ArrayList<UserQuestionAnswer>();
        for (UserQuestionAnswerDO ua : answers) {
            list.add(convertUserQuestionAnswerDO(ua));
        }
        return list;
    }

    /**
     * 生成每个学生问卷
     */
    @Transactional
    public void createStudentQuestionAnswer(Long questionnaireId, int gradeNo, String userName) {
        List<UserQuestionnaireDO> uqs = new ArrayList<UserQuestionnaireDO>();
        //查询问卷对应的年级学生用户
        List<User> users = userRepository.loadUser(gradeNo, -1, "STUDENT");
        Date now = new Date();
        for (User u : users) {
            if (StringUtils.isBlank(u.getRole()) || !u.getRole().equals(UserRoleType.STUDENT.name())) {
                continue;
            }
            UserQuestionnaireDO uq = new UserQuestionnaireDO();
            uq.setAnswerStatus(QuestionnaireAnswerStatus.INIT.name());
            uq.setCount(0);
            uq.setCreatedTime(now);
            uq.setUpdatedTime(now);
            uq.setCreateor(userName);
            uq.setIsDeleted(IsDeleted.N.name());
            uq.setQuestionnaireId(questionnaireId);
            uq.setUserId(u.getId());
            userQuestionnaireDOMapper.insertSelective(uq);
        }
    }

    /**
     * 保存每个学生每题答案
     */
    @Transactional
    public void saveStudentQuestionAnswer(UserQuestionAnswer src) {
        UserQuestionAnswerDO tar = new UserQuestionAnswerDO();
        BeanUtils.copyProperties(src, tar);

        int count = userQuestionAnswerDOMapper.insertSelective(tar);
        JavaAssert.isTrue(count == 1, ReturnCode.DB_ERROR, "保存学生问卷题目选项失败", tar, HealthException.class);

    }

    /**
     * 更新每个学生每题答案
     */
    @Transactional
    public void updateStudentQuestionAnswer(UserQuestionAnswer src) {
        UserQuestionAnswerDO tar = new UserQuestionAnswerDO();
        BeanUtils.copyProperties(src, tar);
        int count = userQuestionAnswerDOMapper.updateByPrimaryKeySelective(tar);
        JavaAssert.isTrue(count == 1, ReturnCode.DB_ERROR, "更新学生问卷题目选项失败,id=" + tar.getId(), tar,
                HealthException.class);
    }

    /**
     * 更新开始答卷时间
     */
    @Transactional
    public void updateStudentStartAnswer(Long questionnaireId, Long studentId) {

        UserQuestionnaireDO uq = loadUserQuestionnaireByQuesIdAndStudId(questionnaireId, studentId);
        userQuestionnaireDOMapper.updateToStartAnswer(uq);
    }

    /**
     * 答题完成
     */
    @Transactional
    public void updateToSubmit(UserQuestionnaire src) {

        UserQuestionnaireDO uq = convertUserQuestionnaireDO(src);
        userQuestionnaireDOMapper.updateToSubmit(uq);
    }

    /**
     * 更新学生答题数量+1
     */
    @Transactional
    public void updateStudentAnswerCountAndOne(Long questionnaireId, Long studentId) {

        UserQuestionnaireDO uq = loadUserQuestionnaireByQuesIdAndStudId(questionnaireId, studentId);
        userQuestionnaireDOMapper.updateAnswerCountAndOne(uq);
    }

    public UserQuestionnaireDO loadUserQuestionnaireByQuesIdAndStudId(Long questionnaireId, Long studentId) {
        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        cri.andUserIdEqualTo(studentId);
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        List<UserQuestionnaireDO> uqs = userQuestionnaireDOMapper.selectByExample(example);
        JavaAssert.isTrue(CollectionUtils.isNotEmpty(uqs), ReturnCode.DATA_NOT_EXIST, "用户问卷答题不存在",
                HealthException.class);
        UserQuestionnaireDO uq = uqs.get(0);
        return uq;
    }

    public UserQuestionnaire loadUserQuestionnaireByQuesIdAndStudentId(Long questionnaireId, Long studentId) {
        UserQuestionnaireDO uq = loadUserQuestionnaireByQuesIdAndStudId(questionnaireId, studentId);
        return convertUserQuestionnaire(uq);
    }

}
