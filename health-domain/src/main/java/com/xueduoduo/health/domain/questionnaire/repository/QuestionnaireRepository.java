package com.xueduoduo.health.domain.questionnaire.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.java.common.base.Page;
import com.github.java.common.utils.DateUtil;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.QuestionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionOptionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionnaireDOMapper;
import com.xueduoduo.health.dal.dao.QuestionnaireLatitudeScoreDOMapper;
import com.xueduoduo.health.dal.dao.UserQuestionnaireDOMapper;
import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDOExample;
import com.xueduoduo.health.dal.dataobject.UserQuestionnaireDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireAnswerStatus;
import com.xueduoduo.health.domain.enums.QuestionnaireStatusType;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.Question;
import com.xueduoduo.health.domain.questionnaire.QuestionOption;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.QuestionnaireLatitudeScore;
import com.xueduoduo.health.domain.utils.UpdateTimeUtils;

/**
 * @author wangzhifeng
 * @date 2018年8月12日 下午10:50:33
 */
@Service
public class QuestionnaireRepository {
    @Autowired
    private QuestionnaireDOMapper              dao;

    @Autowired
    private QuestionDOMapper                   questionDOMapper;

    @Autowired
    private QuestionOptionDOMapper             questionOptionDOMapper;

    @Autowired
    private UserQuestionnaireDOMapper          userQuestionnaireDOMapper;
    @Autowired
    private QuestionnaireLatitudeScoreDOMapper questionnaireLatitudeScoreDOMapper;

    public QuestionOption loadQuestionOptionById(Long id) {
        QuestionOptionDO od = questionOptionDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null != od, ReturnCode.DATA_NOT_EXIST, "题目选项不存在,id=" + id, HealthException.class);
        return convertToQuestionOption(od);
    }

    /**
     * 分页查
     * 
     * @param req
     * @return
     */
    public Page<Questionnaire> loadPage(String schoolYear, String title, int offSet, int length,
                                        String questionnaireType, Integer gradeNo) {
        Page<Questionnaire> page = new Page<Questionnaire>();

        QuestionnaireDOExample example = new QuestionnaireDOExample();
        QuestionnaireDOExample.Criteria cri = example.createCriteria();

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

        if (null != gradeNo && gradeNo > 0) {
            cri.andGradeNoEqualTo(gradeNo);
        }

        if (!StringUtils.isEmpty(title)) {
            title = "%" + title + "%";
            cri.andTitleLike(title);
        }
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        if (StringUtils.isNoneBlank(questionnaireType)) {
            cri.andQuestionnaireTypeEqualTo(questionnaireType);
        }

        long counts = dao.countByExample(example);
        if (0 == counts) {
            page.setTotalCountNum(0);
            return page;
        }

        List<QuestionnaireDO> dos = dao.selectByExample(example);
        List<Questionnaire> list = new ArrayList<Questionnaire>();
        for (QuestionnaireDO src : dos) {
            list.add(convert(src));
        }
        page.setPageData(list);
        page.setTotalCountNum((int) counts);
        return page;
    }

    public Page<Questionnaire> loadPage(String schoolYear, String title, int offSetStr, int lengthStr) {

        return loadPage(schoolYear, title, offSetStr, lengthStr, null, null);
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
        tar.setQuestionnaireType(QuestionnaireType.parse(src.getQuestionnaireType()).getDesc());
        tar.setCreatedTimeStr(DateUtil.format(src.getCreatedTime(), DateUtil.chineseDtFormat));
        tar.setUpdatedTimeStr(UpdateTimeUtils.getUpdateTimeStr(src.getUpdatedTime()));
        tar.setEndedDateStr(DateUtil.format(tar.getEndedDate(), DateUtil.chineseDtFormat));

        if (null != tar.getEndedDate()) {
            if (tar.getEndedDate().before(new Date())) {
                tar.setProcessStatus("已结束");
            } else {
                tar.setProcessStatus("测评中");
            }
        }
        return tar;
    }

    /**
     * 查询问卷
     * 
     * @param schoolYear
     * @param gradeNo
     * @param questionnaireType
     * @param questionnaireStatusType
     */
    public List<Questionnaire> loadQuestionnaire(String schoolYear, int gradeNo, String questionnaireType,
                                                 String questionnaireStatusType) {

        QuestionnaireDOExample example = new QuestionnaireDOExample();
        QuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andSchoolYearEqualTo(schoolYear);
        cri.andGradeNoEqualTo(gradeNo);
        cri.andQuestionnaireTypeEqualTo(questionnaireType);
        cri.andCreateStatusEqualTo(questionnaireStatusType);
        List<QuestionnaireDO> qds = dao.selectByExample(example);

        List<Questionnaire> list = new ArrayList<Questionnaire>();
        if (CollectionUtils.isNotEmpty(qds)) {
            for (QuestionnaireDO src : qds) {
                list.add(convert(src));
            }
        }
        return list;
    }

    /**
     * 保存
     * 
     * @param displayName
     * @param year
     * @param userName
     */
    @Transactional
    public Long save(String userName, String questionnaireType) {
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
        return ld.getId();
    }

    /**
     * 复制问卷并保存
     */
    @Transactional
    public QuestionnaireDO copyAndSave(Long srcId, String userName) {
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
        return ld;
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
        ld.setAddition(ld.getAddition() + ";" + userName + "更新了该问卷");
        int count = dao.updateByPrimaryKeySelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "更新该问卷异常", HealthException.class);
    }

    /**
     * 创建题目后更新问卷题目数量
     */
    @Transactional
    public void updateToSaveQuestionsCount(Long id, int counts, String userName) {
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(id);
        ld.setCount(counts);
        ld.setUpdatedTime(new Date());
        ld.setAddition(ld.getAddition() + ";" + userName + "更新了该问卷");
        int count = dao.updateByPrimaryKeySelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "更新该问卷异常", HealthException.class);
    }

    /**
     * 发布问卷
     */
    @Transactional
    public void updateToPublished(Questionnaire src, String userName) {
        //发布检查
        QuestionnaireDO qn = dao.selectByPrimaryKey(src.getId());
        JavaAssert.isTrue(!qn.getCreateStatus().equals(QuestionnaireStatusType.PUBLISHED.name()),
                ReturnCode.PARAM_ILLEGLE, "问卷已发布", HealthException.class);
        JavaAssert.isTrue(null != src, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + src.getId(), HealthException.class);
        int questionCount = publishedCheck(src.getId(), qn);

        //发布
        QuestionnaireDO ld = new QuestionnaireDO();
        ld.setId(src.getId());
        ld.setGradeNo(src.getGradeNo());
        ld.setStartDate(src.getStartDate());
        ld.setEndedDate(src.getEndedDate());
        ld.setSchoolYear(src.getSchoolYear());
        ld.setUpdatedTime(new Date());
        ld.setCount(questionCount);
        ld.setCreateStatus(QuestionnaireStatusType.PUBLISHED.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "发布该问卷");
        int count = -1;
        try {
            count = dao.updateByPrimaryKeySelective(ld);

        } catch (DuplicateKeyException e) {
            throw new HealthException(ReturnCode.OPERATOR_DATE_ILLEGLE, "同一学年,只能发布一个学生和一个教师问卷");
        }
        if (count != 1) {
            QuestionnaireDO questionnaire = dao.selectByPrimaryKey(src.getId());
            if (!questionnaire.getCreateStatus().equals(QuestionnaireStatusType.PUBLISHED.name())) {
                throw new HealthException(ReturnCode.OPERATOR_DATE_ILLEGLE, "问卷发布异常");
            }
        }
    }

    /**
     * 查询题目纬度描述
     */
    public List<QuestionnaireLatitudeScore> loadQuestionnaireLatitudeScore(Long questionnaireId) {
        //查询纬度设置
        QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
        QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionnaireLatitudeScoreDO> scores = questionnaireLatitudeScoreDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(scores)) {
            List<QuestionnaireLatitudeScore> qls = new ArrayList<QuestionnaireLatitudeScore>();
            for (QuestionnaireLatitudeScoreDO qs : scores) {
                qls.add(convertQuestionnaireLatitudeScore(qs));
            }
            return qls;
        }
        return null;
    }

    private QuestionnaireLatitudeScore convertQuestionnaireLatitudeScore(QuestionnaireLatitudeScoreDO src) {
        QuestionnaireLatitudeScore tar = new QuestionnaireLatitudeScore();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 发布校验
     */
    private int publishedCheck(Long questionnaireId, QuestionnaireDO qn) {
        //1 有题目,题目有纬度 
        List<QuestionDO> qs = loadQuestionsBy(questionnaireId);
        JavaAssert.isTrue(CollectionUtils.isNotEmpty(qs), ReturnCode.PARAM_ILLEGLE,
                "发布问卷对应的题目不能为空,问卷ID=" + questionnaireId, HealthException.class);
        Set<Long> qIds = new HashSet<Long>();
        for (QuestionDO qdo : qs) {
            qIds.add(qdo.getId());
        }
        //2 题目有选项、有分数
        List<QuestionOptionDO> qos = loadQestOptionAndScore(questionnaireId);
        JavaAssert.isTrue(CollectionUtils.isNotEmpty(qos), ReturnCode.PARAM_ILLEGLE,
                "发布问卷对应的题目选项不能为空,问卷ID=" + questionnaireId, HealthException.class);

        JavaAssert.isTrue(qos.size() >= qs.size(), ReturnCode.PARAM_ILLEGLE, "发布问卷部分题目没有选项,问卷ID=" + questionnaireId,
                HealthException.class);
        Set<Long> qoqIds = new HashSet<Long>();
        for (QuestionOptionDO qod : qos) {
            qoqIds.add(qod.getQuestionId());
        }

        //问题ID 去掉 选项里的问题ID
        qIds.removeAll(qoqIds);
        JavaAssert.isTrue(qIds.size() == 0, ReturnCode.PARAM_ILLEGLE, "发布问卷部分题目没有选项,问卷ID=" + questionnaireId,
                HealthException.class);

        //3 学生问卷有纬度分数说明
        if (QuestionnaireType.STUDENT.name().equals(qn.getQuestionnaireType())) {
            //查询纬度设置
            QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
            QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
            cri.andQuestionnaireIdEqualTo(questionnaireId);
            cri.andIsDeletedEqualTo(IsDeleted.N.name());
            List<QuestionnaireLatitudeScoreDO> scores = questionnaireLatitudeScoreDOMapper.selectByExample(example);
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(scores), ReturnCode.PARAM_ILLEGLE,
                    "发布问卷部纬度描述未设置,问卷ID=" + questionnaireId, HealthException.class);

            //问卷纬度描述中的纬度
            Set<Long> qlsIds = new HashSet<Long>();
            for (QuestionnaireLatitudeScoreDO qdo : scores) {
                qlsIds.add(qdo.getLatitudeId());
            }

            //问卷题目中的纬度
            Set<Long> latIds = new HashSet<Long>();
            for (QuestionDO q : qs) {
                latIds.add(q.getLatitudeId());
            }

            latIds.removeAll(qlsIds);
            JavaAssert.isTrue(CollectionUtils.isEmpty(latIds), ReturnCode.PARAM_ILLEGLE,
                    "发布问卷部纬度描述未设置,问卷ID=" + questionnaireId, HealthException.class);
        }
        return qs.size();
    }

    /**
     * 根据问卷ID查询问题
     * 
     * @param questionnaireId
     * @return
     */
    private List<QuestionDO> loadQuestionsBy(Long questionnaireId) {
        QuestionDOExample qe = new QuestionDOExample();
        QuestionDOExample.Criteria cri = qe.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionDO> questions = questionDOMapper.selectByExample(qe);
        return questions;
    }

    /**
     * copy 问卷问题+问卷问题选项+分数
     * 
     * @param questionnaireId
     */
    @Transactional
    public void deepCopyQuestionnaireQuestionAndOptions(Long srcQuestionnaireId, Long newQuestionnaireId) {
        Map<Long, Long> oldAndNewIdMap = new HashMap<Long, Long>();
        List<QuestionDO> qds = loadQuestionsBy(srcQuestionnaireId);
        Date now = new Date();
        for (QuestionDO src : qds) {
            Long oldId = src.getId();
            src.setId(null);
            src.setCreatedTime(now);
            src.setUpdatedTime(now);
            src.setQuestionnaireId(newQuestionnaireId);
            src.setAddition("copy from id=" + oldId);
            questionDOMapper.insertSelective(src);
            Long newId = src.getId();
            oldAndNewIdMap.put(oldId, newId);
        }

        List<QuestionOptionDO> qos = loadQestOptionAndScore(srcQuestionnaireId);
        for (QuestionOptionDO src : qos) {
            Long oId = src.getId();
            src.setId(null);
            src.setCreatedTime(now);
            src.setUpdatedTime(now);
            src.setAddition("copy from id=" + oId);
            src.setQuestionnaireId(newQuestionnaireId);
            src.setQuestionId(oldAndNewIdMap.get(src.getQuestionId()));
            questionOptionDOMapper.insertSelective(src);
        }
    }

    /**
     * 复制问卷纬度
     * 
     * @param srcQuestionnaireId
     * @param newQuestionnaireId
     */
    public void copyQuestionnaireLatitudeScore(Long srcQuestionnaireId, Long newQuestionnaireId) {
        Date now = new Date();
        //查询纬度设置
        QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
        QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(srcQuestionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionnaireLatitudeScoreDO> scores = questionnaireLatitudeScoreDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(scores)) {
            Collections.sort(scores, Comparator.comparing(QuestionnaireLatitudeScoreDO::getLatitudeId)
                    .thenComparing(QuestionnaireLatitudeScoreDO::getScoreMin));

            for (QuestionnaireLatitudeScoreDO src : scores) {
                src.setId(null);
                src.setCreatedTime(now);
                src.setUpdatedTime(now);
                src.setAddition("copy from id=" + srcQuestionnaireId);
                src.setQuestionnaireId(newQuestionnaireId);
                questionnaireLatitudeScoreDOMapper.insertSelective(src);
            }
        }
    }

    /**
     * 查询问卷题目选项和分数
     */
    private List<QuestionOptionDO> loadQestOptionAndScore(Long questionnaireId) {
        QuestionOptionDOExample example = new QuestionOptionDOExample();
        QuestionOptionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionOptionDO> list = questionOptionDOMapper.selectByExample(example);
        return list;
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
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "问卷已被删除或已发布不能删除", HealthException.class);
    }

    /**
     * 查询问卷下所有题目
     */
    public List<Question> loadQuestionnaireQuestions(Long questionnaireId) {
        List<Question> lst = new ArrayList<Question>();
        QuestionDOExample example = new QuestionDOExample();
        QuestionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
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
        return loadQuestionnaireQuestionQuestionOptions(questionnaireId, null);
    }

    /**
     * 查询问卷下所有题目选项
     */
    public List<QuestionOption> loadQuestionnaireQuestionQuestionOptions(Long questionnaireId, Long questionId) {
        List<QuestionOption> lst = new ArrayList<QuestionOption>();
        QuestionOptionDOExample example = new QuestionOptionDOExample();
        QuestionOptionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        if (null != questionId) {
            cri.andQuestionIdEqualTo(questionId);
        }
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

    /**
     * 统计问卷完成情况
     */
    public Questionnaire questionnaireSummary(Questionnaire q) {
        UserQuestionnaireDOExample example = new UserQuestionnaireDOExample();
        UserQuestionnaireDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(q.getId());
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        Long totalCount = userQuestionnaireDOMapper.countByExample(example);

        cri.andAnswerStatusEqualTo(QuestionnaireAnswerStatus.DONE.name());
        Long doneCount = userQuestionnaireDOMapper.countByExample(example);

        BigDecimal rate = null;
        if (0 == totalCount.longValue()) {
            rate = new BigDecimal(0.00d);
        } else {
            BigDecimal d1 = new BigDecimal(doneCount.longValue());
            BigDecimal d2 = new BigDecimal(totalCount.longValue());
            rate = d1.divide(d2, 2, RoundingMode.HALF_UP);
        }
        rate.setScale(2);

        q.setAnsweredRate(rate);
        q.setStudentAnswerCount(doneCount.intValue());
        q.setTotalStudentCount(totalCount.intValue());
        return q;
    }

}
