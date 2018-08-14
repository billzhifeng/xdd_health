package com.xueduoduo.health.domain.questionnaire.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.QuestionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionOptionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionnaireDOMapper;
import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireStatusType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.Question;
import com.xueduoduo.health.domain.questionnaire.QuestionOption;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;

/**
 * @author wangzhifeng
 * @date 2018年8月12日 下午10:50:33
 */
@Service
public class QuestionnaireRepository {
    @Autowired
    private QuestionnaireDOMapper  dao;

    @Autowired
    private QuestionDOMapper       questionDOMapper;

    @Autowired
    private QuestionOptionDOMapper questionOptionDOMapper;

    /**
     * 分页查
     * 
     * @param req
     * @return
     */
    public Page<Questionnaire> loadPage(String schoolYear, String title, String offSetStr, String lengthStr) {

        Page<Questionnaire> page = new Page<Questionnaire>();

        QuestionnaireDOExample example = new QuestionnaireDOExample();
        QuestionnaireDOExample.Criteria cri = example.createCriteria();

        int length = -1;
        int offSet = -1;
        if (StringUtils.isNotBlank(offSetStr)) {
            offSet = Integer.parseInt(offSetStr);
            if (StringUtils.isNotBlank(lengthStr)) {
                length = Integer.parseInt(lengthStr);
            } else {
                length = 10;
            }
        }
        //需要分页
        if (offSet > -1 && length > 0) {
            example.setOffSet(offSet);
            example.setLength(length);

            page.setLength(length);
            page.setOffSet(offSet);
        }

        if (!StringUtils.isEmpty(schoolYear)) {
            cri.andSchoolYearEqualTo(schoolYear);
        }

        if (!StringUtils.isEmpty(title)) {
            title = "%" + title + "%";
            cri.andTitleLike(title);
        }

        long counts = dao.countByExample(example);
        if (0 == counts) {
            page.setTotalCountNum(0);
            return page;
        }

        cri.andIsDeletedEqualTo(IsDeleted.N.name());

        List<QuestionnaireDO> dos = dao.selectByExample(example);
        List<Questionnaire> list = new ArrayList<Questionnaire>();
        for (QuestionnaireDO src : dos) {
            list.add(convert(src));
        }
        page.setPageData(list);
        page.setTotalCountNum((int) counts);
        return page;
    }

    /**
     * 查询某个问卷byid
     */
    public Questionnaire loadById(Long id) {
        QuestionnaireDO src = dao.selectByPrimaryKey(id);
        JavaAssert.isTrue(null != src, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + id, HealthException.class);
        return convert(src);
    }

    private Questionnaire convert(QuestionnaireDO src) {
        Questionnaire tar = new Questionnaire();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 保存
     * 
     * @param displayName
     * @param year
     * @param userName
     */
    @Transactional
    public void save(String userName, String questionnaireType) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setQuestionnaireType(questionnaireType);//TEACHER教师\STUDENT学生
        Date today = new Date();
        ld.setTitle("未命名");
        ld.setCreatedTime(today);
        ld.setUpdatedTime(today);
        ld.setCreateStatus(QuestionnaireStatusType.CREATED.name());
        ld.setCreateor(userName);
        ld.setIsDeleted(IsDeleted.N.name());
        int count = dao.insertSelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "问卷保存异常", HealthException.class);
    }

    /**
     * 复制问卷并保存
     */
    @Transactional
    public void copyAndSave(Long srcId, String userName) {
        QuestionnaireDO src = dao.selectByPrimaryKey(srcId);
        JavaAssert.isTrue(null != src, ReturnCode.DATA_NOT_EXIST, "id=" + srcId + "问卷不存在", HealthException.class);

        QuestionnaireDO ld = new QuestionnaireDO();
        BeanUtils.copyProperties(src, ld);
        ld.setId(null);
        ld.setSchoolYear(null);
        Date today = new Date();
        ld.setTitle("复制-" + src.getTitle());
        ld.setIntroduction("复制-" + src.getIntroduction());
        ld.setGradeNo(null);
        ld.setStartDate(null);
        ld.setEndedDate(null);
        ld.setSchoolYear(null);
        ld.setCreatedTime(today);
        ld.setUpdatedTime(today);
        ld.setCreateStatus(QuestionnaireStatusType.CREATED.name());
        ld.setCreateor(userName);
        ld.setIsDeleted(IsDeleted.N.name());

        int count = dao.insertSelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "问卷复制保存异常", HealthException.class);
    }

    /**
     * 创建题目后保存
     */
    @Transactional
    public void updateToSaveQuestions(Questionnaire src, String userName) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(src.getId());
        ld.setTitle(src.getTitle());
        ld.setIntroduction(src.getIntroduction());
        ld.setCount(src.getCount());
        ld.setUpdatedTime(new Date());
        ld.setCreateStatus(QuestionnaireStatusType.PUBLISHED.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "发布该问卷");
        int count = dao.updateByPrimaryKeySelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "发布问卷异常", HealthException.class);
    }

    /**
     * 未使用:删除问卷下某一个题目后更新
     */
    @Transactional
    public void updateByDeleteOneQuestion(Long id, String userName) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(id);
        ld.setUpdatedTime(new Date());
        int count = dao.updateByDeleteOneQuestion(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "删除问卷题目异常", HealthException.class);
    }

    /**
     * 发布问卷
     */
    @Transactional
    public void updateToPublished(Questionnaire src, String userName) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(src.getId());
        ld.setGradeNo(src.getGradeNo());
        ld.setStartDate(src.getStartDate());
        ld.setEndedDate(src.getEndedDate());
        ld.setSchoolYear(src.getSchoolYear());
        ld.setUpdatedTime(new Date());
        ld.setCreateStatus(QuestionnaireStatusType.PUBLISHED.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "发布该问卷");
        try {
            int count = dao.updateByPrimaryKeySelective(ld);
            JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "发布问卷异常", HealthException.class);
        } catch (DuplicateKeyException e) {
            throw new HealthException(ReturnCode.OPERATOR_DATE_ILLEGLE, "同一学年,只能发布一个学生和一个教师问卷");
        }
    }

    /**
     * 删除问卷
     */
    @Transactional
    public void updateToDeleted(Long id, String userName) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(id);
        ld.setIsDeleted(IsDeleted.Y.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "删除该问卷");
        int count = dao.updateToDeleted(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "问卷删除异常", HealthException.class);
    }

    /**
     * 查询问卷下所有题目
     */
    public List<Question> loadQuestionnaireQuestions(Long questionnaireId) {
        List<Question> lst = new ArrayList<Question>();
        QuestionDOExample example = new QuestionDOExample();
        QuestionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdGreaterThan(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionDO> qs = questionDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(qs)) {
            return lst;
        }
        for (QuestionDO q : qs) {
            lst.add(convertToQuestion(q));
        }
        return lst;
    }

    private Question convertToQuestion(QuestionDO src) {
        Question tar = new Question();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 查询问卷下所有题目选项
     */
    public List<QuestionOption> loadQuestionnaireQuestionQuestionOptions(Long questionnaireId) {
        List<QuestionOption> lst = new ArrayList<QuestionOption>();
        QuestionOptionDOExample example = new QuestionOptionDOExample();
        QuestionOptionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdGreaterThan(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionOptionDO> qs = questionOptionDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(qs)) {
            return lst;
        }
        for (QuestionOptionDO q : qs) {
            lst.add(convertToQuestionOption(q));
        }
        return lst;
    }

    private QuestionOption convertToQuestionOption(QuestionOptionDO src) {
        QuestionOption tar = new QuestionOption();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }
}
