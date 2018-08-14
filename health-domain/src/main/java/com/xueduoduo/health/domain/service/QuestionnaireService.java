package com.xueduoduo.health.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.QuestionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionOptionDOMapper;
import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;
import com.xueduoduo.health.domain.questionnaire.Question;
import com.xueduoduo.health.domain.questionnaire.QuestionOption;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;

/**
 * @author wangzhifeng
 * @date 2018年8月13日 下午10:28:55
 */
@Service
public class QuestionnaireService {
    private static final Logger     logger = LoggerFactory.getLogger(QuestionnaireService.class);

    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private QuestionDOMapper        questionDOMapper;
    @Autowired
    private QuestionOptionDOMapper  questionOptionDOMapper;
    @Autowired
    private LatitudeRepository      latitudeRepository;

    /**
     * 复制问卷
     * 
     * @param id
     * @param userName
     */
    @Transactional
    public void copyQuestionnaire(Long id, String userName) {
        questionnaireRepository.copyAndSave(id, userName);
        //TODO 复制题目 + 分数
        //TODO 学生问卷复制分数说明
    }

    /**
     * 查询问卷题目、选项
     * 
     * @param id
     * @param userName
     */
    public BaseResp showAddQuestionnaireQuestions(Long questionnaireId) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        JSONObject json = new JSONObject();
        //查询问卷
        Questionnaire qe = questionnaireRepository.loadById(questionnaireId);
        json.put("questionnaire", qe);

        //问卷步骤、按钮是否展示
        JSONArray btns = new JSONArray();
        JSONArray steps = new JSONArray();
        btns.add("next_btn");
        steps.add(1, "question_set_step");
        steps.add(2, "score_set_step");
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())) {//学生问卷
            steps.add(3, "latitude_set_step");
        }
        json.put("btns", btns);
        json.put("steps", steps);

        //查询问卷下所有问题
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        if (CollectionUtils.isEmpty(questions)) {
            json.put("questions", null);
            return resp.setData(json);
        }

        //查询问题所有选项
        List<QuestionOption> options = questionnaireRepository
                .loadQuestionnaireQuestionQuestionOptions(questionnaireId);

        //问卷问题组装
        questionsMatchQuestionOption(questions, options, false);
        json.put("questions", questions);
        return resp.setData(json);
    }

    /**
     * 问卷问题组装
     */
    private Map<String, BigDecimal> questionsMatchQuestionOption(List<Question> questions, List<QuestionOption> options,
                                                                 boolean withScore) {
        Map<String, BigDecimal> scoresMap = new HashMap<String, BigDecimal>();
        BigDecimal qMinScore = new BigDecimal(0.0D);
        BigDecimal qMaxScore = new BigDecimal(0.0D);
        scoresMap.put("minScore", qMinScore);
        scoresMap.put("maxScore", qMaxScore);

        Collections.sort(questions, Comparator.comparing(Question::getQuestionNo));

        if (CollectionUtils.isEmpty(options)) {
            return null;
        }

        for (Question q : questions) {
            List<QuestionOption> qos = new ArrayList<QuestionOption>();
            BigDecimal minScore = new BigDecimal(0.0D);
            BigDecimal maxScore = new BigDecimal(0.0D);
            for (QuestionOption qo : options) {
                if (qo.getQuestionId().longValue() == q.getId().longValue()) {
                    qos.add(qo);
                    if (withScore) {
                        BigDecimal score = qo.getScore();
                        if (score.doubleValue() == 0 || minScore.doubleValue() > score.doubleValue()) {
                            minScore = score;
                        }
                        maxScore = maxScore.add(qo.getScore());
                    }
                }
            }
            Collections.sort(qos, Comparator.comparing(QuestionOption::getOptionNo));
            q.setOptions(qos);
            if (withScore) {
                q.setMaxScore(maxScore);
                q.setMinScore(minScore);

                qMinScore = qMinScore.add(minScore);
                qMaxScore = qMaxScore.add(maxScore);
            }

        }

        scoresMap.put("minScore", qMinScore);
        scoresMap.put("maxScore", qMaxScore);
        return scoresMap;

    }

    /**
     * 更新保存问卷题目+选项
     * 
     * @param id
     * @param questionsJsonStr
     */
    @Transactional
    public void addQuestionnaireQuestions(Long id, String title, String introduction, String questionsJsonStr,
                                          String userName) {
        //构建问卷内容
        Questionnaire ld = new Questionnaire();
        ld.setId(id);
        ld.setTitle(title);
        ld.setIntroduction(introduction);

        //如果没有题目直接保存
        if (StringUtils.isBlank(questionsJsonStr)) {
            ld.setCount(0);
            questionnaireRepository.updateToSaveQuestions(ld, userName);
            return;
        }

        //问卷下有问题列表
        JSONArray ja = JSONArray.parseArray(questionsJsonStr);
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目为空", HealthException.class);

        //被更新的问题ID列表
        List<Long> updateQuestionIds = new ArrayList<Long>();
        //循环保存问题 + 问题选项
        int size = ja.size();
        for (int i = 0; i < size; i++) {
            JSONObject q = ja.getJSONObject(i);
            Long updateQuestionId = saveOrUpdateQuestionAndOptions(q, userName);
            if (null != updateQuestionId) {
                updateQuestionIds.add(updateQuestionId);
            }
        }

        //原问卷问题列表
        List<Long> originQuestionIds = new ArrayList<Long>();
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(id);
        for (Question qn : questions) {
            originQuestionIds.add(qn.getId());
        }
        //删除此次被更新的问题
        originQuestionIds.removeAll(updateQuestionIds);
        //未被更新的问题需要被删除
        for (Long toDeleteId : originQuestionIds) {
            new Thread(() -> updateQuestionToDeleted(toDeleteId, userName));
        }

        //更新后问卷题目数量
        ld.setCount(size);
        questionnaireRepository.updateToSaveQuestions(ld, userName);
    }

    /**
     * 保存或更新题目选项
     */
    @Transactional
    private Long saveOrUpdateQuestionAndOptions(JSONObject q, String userName) {

        String questionName = (String) q.get("questionName");
        int questionNo = Integer.parseInt((String) q.get("questionNo"));
        String audioUrl = (String) q.get("audioUrl");
        String questionnaireIdStr = (String) q.get("questionnaireId");
        Long questionnaireId = Long.parseLong(questionnaireIdStr);

        String questionIdStr = (String) q.get("questionId");
        if (StringUtils.isNoneBlank(questionIdStr)) {
            //更新题目
            Long questionId = Long.parseLong(questionIdStr);

            Date today = new Date();
            QuestionDO qst = new QuestionDO();
            qst.setAudioUrl(audioUrl);
            qst.setUpdatedTime(today);
            qst.setQuestionName(questionName);
            qst.setQuestionNo(questionNo);
            qst.setId(questionId);
            int count = questionDOMapper.updateByPrimaryKeySelective(qst);
            JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目更新异常", HealthException.class);

            String optionsStr = (String) q.get("optionsStr");
            JavaAssert.isTrue(StringUtils.isNoneBlank(optionsStr), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);

            //题目选项会被完全覆盖
            //查询原来所有选项
            QuestionOptionDOExample example = new QuestionOptionDOExample();
            QuestionOptionDOExample.Criteria cri = example.createCriteria();
            cri.andQuestionIdEqualTo(questionId);
            cri.andIsDeletedEqualTo(IsDeleted.N.name());
            List<QuestionOptionDO> list = questionOptionDOMapper.selectByExample(example);
            Map<Long, QuestionOptionDO> map = new HashMap<Long, QuestionOptionDO>();
            if (!CollectionUtils.isEmpty(list)) {
                for (QuestionOptionDO record : list) {
                    //原来所有选项
                    map.put(record.getId(), record);
                }
            }

            //更新 或 添加选项
            //选项
            JSONArray ja = JSONArray.parseArray(optionsStr);
            JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = ja.size();
            for (int i = 0; i < size; i++) {

                JSONObject option = ja.getJSONObject(i);
                String optionIdStr = (String) option.get("id");
                String optionNoStr = (String) option.get("optionNo");
                int optionNo = Integer.parseInt(optionNoStr);
                String displayName = (String) option.get("displayName");

                if (StringUtils.isNoneBlank(optionIdStr)) {
                    Long optionId = Long.parseLong(optionIdStr);
                    //更新选项
                    QuestionOptionDO o = new QuestionOptionDO();
                    o.setDisplayName(displayName);
                    o.setOptionNo(optionNo);
                    o.setUpdatedTime(today);
                    o.setId(optionId);
                    count = questionOptionDOMapper.updateByPrimaryKeySelective(o);
                    JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项更新异常", HealthException.class);
                    map.remove(optionId);
                } else {
                    //添加选项
                    QuestionOptionDO o = new QuestionOptionDO();
                    o.setDisplayName(displayName);
                    o.setCreateor(userName);
                    o.setOptionNo(optionNo);
                    o.setQuestionId(questionId);
                    o.setQuestionnaireId(questionnaireId);
                    o.setCreateor(userName);
                    o.setCreatedTime(today);
                    o.setUpdatedTime(today);
                    o.setIsDeleted(IsDeleted.N.name());

                    count = questionOptionDOMapper.insertSelective(o);
                    JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项保存异常", HealthException.class);
                }
            }

            //未被更新选项直接删除
            for (Entry<Long, QuestionOptionDO> opt : map.entrySet()) {
                questionOptionDOMapper.updateToDeleted(opt.getValue());
            }

            //被更新的问题
            return questionId;
        } else {
            //新增
            Date today = new Date();
            QuestionDO qst = new QuestionDO();
            qst.setAudioUrl(audioUrl);
            qst.setCreateor(userName);
            qst.setCreatedTime(today);
            qst.setUpdatedTime(today);
            qst.setIsDeleted(IsDeleted.N.name());
            qst.setQuestionnaireId(questionnaireId);
            qst.setQuestionName(questionName);
            qst.setQuestionNo(questionNo);

            int count = questionDOMapper.insertSelective(qst);
            JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目保存异常", HealthException.class);
            Long questionId = qst.getId();

            String optionsStr = (String) q.get("optionsStr");
            JavaAssert.isTrue(StringUtils.isNoneBlank(optionsStr), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);

            //选项
            JSONArray ja = JSONArray.parseArray(optionsStr);
            JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = ja.size();
            for (int i = 0; i < size; i++) {

                JSONObject option = ja.getJSONObject(i);
                String optionNoStr = (String) option.get("optionNo");
                int optionNo = Integer.parseInt(optionNoStr);
                String displayName = (String) option.get("displayName");

                QuestionOptionDO o = new QuestionOptionDO();
                o.setDisplayName(displayName);
                o.setCreateor(userName);
                o.setOptionNo(optionNo);
                o.setQuestionId(questionId);
                o.setQuestionnaireId(questionnaireId);
                o.setCreateor(userName);
                o.setCreatedTime(today);
                o.setUpdatedTime(today);
                o.setIsDeleted(IsDeleted.N.name());

                count = questionOptionDOMapper.insertSelective(o);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项保存异常", HealthException.class);
            }
            //没有被更新的问题
            return null;
        }
    }

    /**
     * 删除题目
     */
    @Transactional
    public void updateQuestionToDeleted(Long id, String userName) {
        //1删除题目
        QuestionDO ld = questionDOMapper.selectByPrimaryKey(id);
        JavaAssert.isTrue(null != ld, ReturnCode.DATA_NOT_EXIST, "题目不存在异常,id=" + id, HealthException.class);

        ld.setIsDeleted(IsDeleted.Y.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "删除该题目");
        int count = questionDOMapper.updateToDeleted(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "题目删除异常,id=" + id, HealthException.class);

        //2.删除题目下所有选项
        QuestionOptionDOExample example = new QuestionOptionDOExample();
        QuestionOptionDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionIdEqualTo(id);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionOptionDO> list = questionOptionDOMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            for (QuestionOptionDO record : list) {
                questionOptionDOMapper.updateToDeleted(record);
            }
        }
        //3.更新问卷下题目总数
        Long questionnaireId = ld.getQuestionnaireId();
        questionnaireRepository.updateByDeleteOneQuestion(questionnaireId, userName);
    }

    /**
     * 展示题目选项设置分数
     */
    public BaseResp showAddScoreAndLatitude(Long questionnaireId) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        JSONObject json = new JSONObject();
        //查询问卷
        Questionnaire qe = questionnaireRepository.loadById(questionnaireId);
        json.put("questionnaire", qe);

        //问卷步骤、按钮是否展示
        JSONArray btns = new JSONArray();
        JSONArray steps = new JSONArray();
        btns.add("next_btn");
        btns.add("pre_btn");
        steps.add(1, "question_set_step");
        steps.add(2, "score_set_step");
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())) {//学生问卷
            steps.add(3, "latitude_set_step");
        } else {
            btns.add("publish_btn");//教师问卷有发布按钮
        }
        json.put("btns", btns);
        json.put("steps", steps);

        //纬度列表
        List<Latitude> latitudes = latitudeRepository.loadAll();
        json.put("latitudes", latitudes);

        //查询问卷下所有问题
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        if (CollectionUtils.isEmpty(questions)) {
            json.put("questions", null);
            return resp.setData(json);
        }

        //查询问题所有选项
        List<QuestionOption> options = questionnaireRepository
                .loadQuestionnaireQuestionQuestionOptions(questionnaireId);

        //问卷问题+问题选项分数组装
        Map<String, BigDecimal> scoresMap = questionsMatchQuestionOption(questions, options, true);
        json.put("questions", questions);
        json.put("minScore", scoresMap.get("minScore"));
        json.put("maxScore", scoresMap.get("maxScore"));

        return resp.setData(json);
    }

    /**
     * 展示题目选项设置分数
     */
    @Transactional
    public void addScoreAndLatitude(Long questionnaireId, String questionsJsonStr, String userName) {

        //问卷下有问题列表
        JSONArray ja = JSONArray.parseArray(questionsJsonStr);
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目为空", HealthException.class);

        //循环保存问题 + 问题选项
        int size = ja.size();
        for (int i = 0; i < size; i++) {
            //每个题
            JSONObject q = ja.getJSONObject(i);
            String questionIdStr = (String) q.get("questionId");
            String latitudeIdStr = (String) q.get("latitudeId");
            //题目Id
            Long questionId = Long.parseLong(questionIdStr);
            //纬度Id
            Long latitudeId = Long.parseLong(latitudeIdStr);

            //每个题目下的所有选项
            String optionsStr = (String) q.get("optionsStr");
            JavaAssert.isTrue(StringUtils.isNoneBlank(optionsStr), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);

            //选项
            JSONArray ja = JSONArray.parseArray(optionsStr);
            JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = ja.size();
            for (int i = 0; i < size; i++) {

                JSONObject option = ja.getJSONObject(i);
                String optionNoStr = (String) option.get("optionNo");
                int optionNo = Integer.parseInt(optionNoStr);
                String displayName = (String) option.get("displayName");
                String scoreStr = (String) option.get("score");
                BigDecimal score = new BigDecimal(scoreStr);
                score.setScale(1);//1位小数

                QuestionOptionDO o = new QuestionOptionDO();
                o.setDisplayName(displayName);
                o.setCreateor(userName);
                o.setOptionNo(optionNo);
                o.setQuestionId(questionId);
                o.setQuestionnaireId(questionnaireId);
                o.setScore(score);
                o.setCreateor(userName);
                o.setCreatedTime(today);
                o.setUpdatedTime(today);
                o.setIsDeleted(IsDeleted.N.name());

                count = questionOptionDOMapper.insertSelective(o);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项保存异常", HealthException.class);
            }

            //分数
            String scoreStr = (String) q.get("score");
            BigDecimal score = new BigDecimal(scoreStr);
            score.setScale(1);//1位小数

            //更新题目纬度

            //更新题目选项分数+纬度
        }
    }

    /**
     * 更新题目纬度
     * 
     * @param questionId
     * @param latitudedId
     */
    @Transactional
    private void updateQuestionLatitude(Long questionId, Long latitudedId) {

    }

    /**
     * 更新题目选项分数
     * 
     * @param questionId
     * @param latitudedId
     */
    @Transactional
    private void addQuestionOptionScore() {

    }
}
