package com.xueduoduo.health.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.QuestionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionOptionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionnaireLatitudeScoreDOMapper;
import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireAnswerStatus;
import com.xueduoduo.health.domain.enums.QuestionnaireStatusType;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;
import com.xueduoduo.health.domain.questionnaire.Question;
import com.xueduoduo.health.domain.questionnaire.QuestionOption;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.QuestionnaireLatitudeScore;
import com.xueduoduo.health.domain.questionnaire.UserQuestionAnswer;
import com.xueduoduo.health.domain.questionnaire.UserQuestionnaire;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.questionnaire.repository.UserQuestionnaireRepository;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * @author wangzhifeng
 * @date 2018年8月13日 下午10:28:55
 */
@Service
public class QuestionnaireService {
    private static final Logger                logger = LoggerFactory.getLogger(QuestionnaireService.class);

    @Autowired
    private QuestionnaireRepository            questionnaireRepository;
    @Autowired
    private QuestionDOMapper                   questionDOMapper;
    @Autowired
    private QuestionOptionDOMapper             questionOptionDOMapper;
    @Autowired
    private LatitudeRepository                 latitudeRepository;
    @Autowired
    private QuestionnaireLatitudeScoreDOMapper questionnaireLatitudeScoreDOMapper;
    @Autowired
    private UserRepository                     userRepository;
    @Autowired
    private UserQuestionnaireRepository        userQuestionnaireRepository;

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
        JSONArray steps = new JSONArray(3);
        btns.add("next_btn");
        steps.add(0, "question_set_step");
        steps.add(1, "score_set_step");
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())) {//学生问卷
            steps.add(2, "latitude_set_step");
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
                        score = score == null ? new BigDecimal(0) : score;
                        if (score.doubleValue() == 0 || minScore.doubleValue() > score.doubleValue()) {
                            minScore = score;
                        }
                        maxScore = maxScore.add(score);
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
     * @param questionnaireId
     * @param questionsJsonStr
     */
    @Transactional
    public void addQuestionnaireQuestions(Long questionnaireId, String title, String introduction,
                                          String questionsJsonStr, String userName) {
        //构建问卷内容
        Questionnaire ld = new Questionnaire();
        ld.setId(questionnaireId);
        ld.setTitle(title);
        ld.setIntroduction(introduction);

        //如果没有题目直接保存
        if (StringUtils.isBlank(questionsJsonStr)) {
            ld.setCount(0);
            questionnaireRepository.updateToSaveQuestions(ld, userName);
            return;
        }

        //原问卷问题列表
        List<Long> originQuestionIds = new ArrayList<Long>();
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        for (Question qn : questions) {
            originQuestionIds.add(qn.getId());
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
            Long updateQuestionId = saveOrUpdateQuestionAndOptions(q, userName, questionnaireId);
            if (null != updateQuestionId) {
                updateQuestionIds.add(updateQuestionId);
            }
        }

        //删除此次被更新的问题
        originQuestionIds.removeAll(updateQuestionIds);
        //未被更新的问题需要被删除
        for (Long toDeleteId : originQuestionIds) {
            updateQuestionToDeleted(toDeleteId, userName);
        }

    }

    /**
     * 保存或更新题目选项
     */
    @Transactional
    private Long saveOrUpdateQuestionAndOptions(JSONObject q, String userName, Long questionnaireId) {

        String questionName = (String) q.get("questionName");
        Integer questionNo = (Integer) q.get("questionNo");
        String audioUrl = (String) q.get("questionAudioUrl");

        Integer questionId = (Integer) q.get("questionId");
        if (null != questionId) {
            //更新题目
            Date today = new Date();
            QuestionDO qst = new QuestionDO();
            qst.setAudioUrl(audioUrl);
            qst.setUpdatedTime(today);
            qst.setQuestionName(questionName);
            qst.setQuestionNo(questionNo);
            qst.setId(questionId.longValue());
            int count = questionDOMapper.updateByPrimaryKeySelective(qst);
            JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目更新异常", HealthException.class);

            JSONArray ja = (JSONArray) q.get("optionsStr");
            JavaAssert.isTrue(null != ja && ja.size() > 0, ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空", HealthException.class);

            //题目选项会被完全覆盖
            //查询原来所有选项
            QuestionOptionDOExample example = new QuestionOptionDOExample();
            QuestionOptionDOExample.Criteria cri = example.createCriteria();
            cri.andQuestionIdEqualTo(questionId.longValue());
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
            JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = ja.size();
            for (int i = 0; i < size; i++) {

                JSONObject option = ja.getJSONObject(i);
                Integer optionId = (Integer) option.get("optionId");
                Integer optionNo = (Integer) option.get("optionNo");
                String optionName = (String) option.get("optionName");

                if (null != optionId) {
                    //更新选项
                    QuestionOptionDO o = new QuestionOptionDO();
                    o.setDisplayName(optionName);
                    o.setOptionNo(optionNo);
                    o.setUpdatedTime(today);
                    o.setId(optionId.longValue());
                    count = questionOptionDOMapper.updateByPrimaryKeySelective(o);
                    JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项更新异常", HealthException.class);
                    map.remove(optionId.longValue());
                } else {
                    //添加选项
                    QuestionOptionDO o = new QuestionOptionDO();
                    o.setDisplayName(optionName);
                    o.setCreateor(userName);
                    o.setOptionNo(optionNo);
                    o.setQuestionId(questionId.longValue());
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
            return questionId.longValue();
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
            questionId = qst.getId().intValue();

            JSONArray ja = (JSONArray) q.get("optionsStr");
            //选项
            JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = ja.size();
            for (int i = 0; i < size; i++) {

                JSONObject option = ja.getJSONObject(i);
                Integer optionNo = (Integer) option.get("optionNo");
                String optionName = (String) option.get("optionName");

                QuestionOptionDO o = new QuestionOptionDO();
                o.setDisplayName(optionName);
                o.setCreateor(userName);
                o.setOptionNo(optionNo);
                o.setQuestionId(questionId.longValue());
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

        ld.setIsDeleted(IsDeleted.Y.name());
        ld.setAddition(ld.getAddition() + ";" + userName + "删除该题目");
        int count = questionDOMapper.updateToDeleted(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "题目删除异常,id=" + id, HealthException.class);
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
        steps.add(0, "question_set_step");
        steps.add(1, "score_set_step");
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())) {//学生问卷
            steps.add(2, "latitude_set_step");
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
        Date now = new Date();
        //问卷下有问题列表
        JSONArray ja = JSONArray.parseArray(questionsJsonStr);
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目为空", HealthException.class);

        //循环保存问题 + 问题选项
        int size = ja.size();
        for (int i = 0; i < size; i++) {
            //每个题
            JSONObject q = ja.getJSONObject(i);
            //题目Id
            Integer questionId = (Integer) q.get("questionId");
            //纬度Id
            Integer latitudeId = (Integer) q.get("latitudeId");

            //更新题目纬度
            QuestionDO question = new QuestionDO();
            question.setId(questionId.longValue());
            question.setLatitudeId(latitudeId.longValue());
            question.setUpdatedTime(now);
            question.setAddition(userName + "设置题目纬度");

            int qCount = questionDOMapper.updateAddLatitude(question);
            JavaAssert.isTrue(1 == qCount, ReturnCode.PARAM_ILLEGLE, "问卷题目纬度更新异常,题目ID=" + questionId,
                    HealthException.class);

            //每个题目下的所有选项
            JSONArray ops = (JSONArray) q.get("optionsStr");
            JavaAssert.isTrue((null != ops && ops.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);

            //更新每个选项分数+纬度
            int opsSize = ops.size();
            for (int j = 0; j < opsSize; j++) {

                JSONObject option = ops.getJSONObject(j);
                Integer optionId = (Integer) option.get("optionId");
                BigDecimal score = (BigDecimal) option.get("score");
                score.setScale(1);//1位小数

                QuestionOptionDO o = new QuestionOptionDO();
                o.setId(optionId.longValue());
                o.setScore(score);
                o.setUpdatedTime(now);
                o.setLatitudeId(latitudeId.longValue());
                o.setAddition(o.getAddition() + ";设置分数");
                int count = questionOptionDOMapper.updateByPrimaryKeySelective(o);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷题目选项分数保存异常", HealthException.class);
            }
        }
    }

    /**
     * 学生问卷展示添加问卷纬度描述设置
     */
    public JSONObject showQuestionnaireLatitudeScoreAndDesc(Long questionnaireId) {
        JSONObject json = new JSONObject();
        //查询问卷
        Questionnaire qe = questionnaireRepository.loadById(questionnaireId);
        json.put("questionnaire", qe);

        //问卷步骤、按钮是否展示
        JSONArray btns = new JSONArray();
        JSONArray steps = new JSONArray();

        steps.add(0, "question_set_step");
        steps.add(1, "score_set_step");
        steps.add(2, "latitude_set_step");

        btns.add("publish_btn");//教师问卷有发布按钮
        btns.add("pre_btn");

        json.put("btns", btns);
        json.put("steps", steps);

        //纬度列表
        List<Latitude> allLats = latitudeRepository.loadAll();

        Set<Long> latIds = new HashSet<Long>();
        QuestionDOExample qea = new QuestionDOExample();
        QuestionDOExample.Criteria qcri = qea.createCriteria();
        qcri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionDO> qs = questionDOMapper.selectByExample(qea);
        JavaAssert.isTrue(!CollectionUtils.isEmpty(qs), ReturnCode.PARAM_ILLEGLE, "文件无题目无法设置纬度描述",
                HealthException.class);
        for (QuestionDO q : qs) {
            latIds.add(q.getLatitudeId());
        }

        List<Latitude> latitudes = new ArrayList<Latitude>();
        for (Latitude src : allLats) {
            if (latIds.contains(src.getId())) {
                latitudes.add(src);
            }
        }

        //查询纬度设置
        QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
        QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionnaireLatitudeScoreDO> scores = questionnaireLatitudeScoreDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(scores)) {
            Collections.sort(scores, Comparator.comparing(QuestionnaireLatitudeScoreDO::getLatitudeId)
                    .thenComparing(QuestionnaireLatitudeScoreDO::getScoreMin));

            for (Latitude la : latitudes) {
                List<QuestionnaireLatitudeScore> scoreList = new ArrayList<QuestionnaireLatitudeScore>();
                for (QuestionnaireLatitudeScoreDO src : scores) {
                    if (src.getLatitudeId().longValue() == la.getId()) {
                        scoreList.add(convertQuestionnaireLatitudeScore(src));
                    }
                }
                la.setLatitudeScoreDesc(scoreList);
            }
        }

        json.put("latitudes", latitudes);
        return json;
    }

    /**
     * 添加问卷纬度描述设置
     */
    @Transactional
    public void addQuestionnaireLatitudeDesc(Long questionnaireId, String scoreDescJson, String userName) {
        Date now = new Date();
        //纬度描述
        JSONArray ja = JSONArray.parseArray(scoreDescJson);
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "纬度描述为空", HealthException.class);

        //查询纬度设置
        QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
        QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionnaireLatitudeScoreDO> oriScores = questionnaireLatitudeScoreDOMapper.selectByExample(example);

        //是否编辑
        boolean isUpdateFlag = false;
        Set<Long> updatedIds = new HashSet<Long>();

        //循环纬度描述
        int size = ja.size();
        for (int i = 0; i < size; i++) {
            //描述
            JSONObject q = ja.getJSONObject(i);
            //纬度Id
            Integer latitudeId = (Integer) q.get("latitudeId");
            BigDecimal minScore = (BigDecimal) q.get("minScore");
            BigDecimal maxScore = (BigDecimal) q.get("maxScore");
            minScore.setScale(1);
            maxScore.setScale(1);
            String descStr = (String) q.get("desc");

            Integer scoreId = (Integer) q.get("scoreId");
            if (null != scoreId) {
                //更新
                isUpdateFlag = true;
                updatedIds.add(scoreId.longValue());

                QuestionnaireLatitudeScoreDO scdb = new QuestionnaireLatitudeScoreDO();
                scdb.setId(scoreId.longValue());
                scdb.setComment(descStr);
                scdb.setScoreMax(maxScore);
                scdb.setScoreMin(minScore);
                int count = questionnaireLatitudeScoreDOMapper.updateByPrimaryKeySelective(scdb);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷纬度描述更新异常", HealthException.class);
            } else {
                //新增
                QuestionnaireLatitudeScoreDO scdb = new QuestionnaireLatitudeScoreDO();
                scdb.setComment(descStr);
                scdb.setCreatedTime(now);
                scdb.setCreateor(userName);
                scdb.setIsDeleted(IsDeleted.N.name());
                scdb.setLatitudeId(latitudeId.longValue());
                scdb.setQuestionnaireId(questionnaireId);
                scdb.setScoreMax(maxScore);
                scdb.setScoreMin(minScore);
                int count = questionnaireLatitudeScoreDOMapper.insertSelective(scdb);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷纬度描述保存异常", HealthException.class);
            }
        }

        //非更新操作
        if (!isUpdateFlag) {
            return;
        }

        //删除未被更新的纬度说明
        for (QuestionnaireLatitudeScoreDO oq : oriScores) {
            if (!updatedIds.contains(oq.getId())) {
                oq.setAddition(oq.getAddition() + ";删除");
                int count = questionnaireLatitudeScoreDOMapper.updateToDeleted(oq);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "问卷纬度描述删除异常", HealthException.class);
            }
        }
    }

    private QuestionnaireLatitudeScore convertQuestionnaireLatitudeScore(QuestionnaireLatitudeScoreDO src) {
        QuestionnaireLatitudeScore tar = new QuestionnaireLatitudeScore();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    /**
     * 测评问卷统计
     */
    public Page<Questionnaire> questionnaireAnswerSumary(String gradeNo, String title, String offSetStr,
                                                         String lengthStr, String questionnatireType) {

        //分页查询学生问卷
        Page<Questionnaire> page = questionnaireRepository.loadPage(null, title, offSetStr, lengthStr,
                questionnatireType, Integer.parseInt(gradeNo));

        //统计问卷测评数
        for (Questionnaire q : page.getPageData()) {
            //已经发布才统计
            if (QuestionnaireStatusType.PUBLISHED.name().equals(q.getCreateStatus())) {
                q = questionnaireRepository.questionnaireSummary(q);
            }
        }

        return page;
    }

    /**
     * 测评年级班级学生明细
     */
    public JSONArray questionnaireGradeClassSummary(Long questionnaireId, int gradeNo, int classNo) {
        //1查询学生问卷
        Questionnaire stuQu = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != stuQu, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId,
                HealthException.class);

        //2查询问卷对应的年级->班级学生用户
        List<User> users = userRepository.loadUser(gradeNo, classNo);

        //查询每个学生完成对该问卷答题
        Set<Long> answeredStudenIds = new HashSet<Long>();
        List<UserQuestionnaire> uqs = userQuestionnaireRepository.summaryStudentAnsweredQuestionnaire(questionnaireId);
        if (!CollectionUtils.isEmpty(uqs)) {
            for (UserQuestionnaire uq : uqs) {
                answeredStudenIds.add(uq.getId());
            }
        }

        //3查询教师对每个学生测评情况
        List<Questionnaire> teacherQus = questionnaireRepository.loadQuestionnaire(stuQu.getSchoolYear(),
                stuQu.getGradeNo(), QuestionnaireType.TEACHER.name(), QuestionnaireStatusType.PUBLISHED.name());
        if (!CollectionUtils.isEmpty(teacherQus)) {
            JavaAssert.isTrue(teacherQus.size() == 1, ReturnCode.PARAM_ILLEGLE, "同一学年统一年级有多份教师问卷",
                    HealthException.class);
        }
        Questionnaire teQu = teacherQus.get(0);
        //教师已经完成对学生测评
        Set<Long> alreadyTestStudenIds = new HashSet<Long>();
        List<UserQuestionnaire> teaStuAnsQus = userQuestionnaireRepository
                .summaryStudentAnsweredQuestionnaire(teQu.getId());
        if (!CollectionUtils.isEmpty(teaStuAnsQus)) {
            for (UserQuestionnaire tsaq : teaStuAnsQus) {
                alreadyTestStudenIds.add(tsaq.getId());
            }
        }

        //4 统计人数

        JSONArray sumnary = new JSONArray();
        for (User u : users) {
            JSONObject stu = new JSONObject();
            stu.put("studentId", u.getId());
            stu.put("studentName", u.getUserName());
            stu.put("studentHeadImgUrl", u.getHeaderImg());
            //学生自己完成测评
            if (answeredStudenIds.contains(u.getId().longValue())) {
                stu.put("studentAnswer", "Y");
            } else {
                stu.put("studentAnswer", "N");
            }

            //教师对学生完成测评
            if (alreadyTestStudenIds.contains(u.getId().longValue())) {
                stu.put("teacherAnswer", "Y");
            } else {
                stu.put("teacherAnswer", "N");
                stu.put("teacherQuestionnaireId", teQu.getId());
            }

            //完成统计
            sumnary.add(stu);
        }

        return sumnary;

    }

    /**
     * 展示教师对学生测评问卷
     */
    public BaseResp showTeacherTestQuestionnaire(Long questionnaireId, Long studentId) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        JSONObject json = new JSONObject();
        //1查询教师问卷
        Questionnaire tq = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != tq, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId, HealthException.class);
        json.put("questionnaire", tq);

        //2查询学生
        User stu = userRepository.loadUserById(studentId);
        JavaAssert.isTrue(null != stu, ReturnCode.DATA_NOT_EXIST, "学生不存在,id=" + studentId, HealthException.class);
        json.put("student", stu);

        //3查询问卷题目 + 选项
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
        resp.setData(json);
        return resp;
    }

    /**
     * 保存教师测评学生结果
     */
    @Transactional
    public void addTeacherTestQuestionnaire(Long questionnaireId, Long studentId, String questionOptionsJson,
                                            String userName) {
        //1查询教师问卷
        Questionnaire tq = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != tq, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId, HealthException.class);
        //题目总数
        int totalQuestionCount = tq.getCount();

        Date now = new Date();
        //测评结果
        JSONArray ja = JSONArray.parseArray(questionOptionsJson);
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "测评结果为空", HealthException.class);
        //循环结果
        int size = ja.size();

        JavaAssert.isTrue(totalQuestionCount == size, ReturnCode.DATA_NOT_EXIST,
                "问卷题目数=" + totalQuestionCount + ",与答案数量=" + size + ",不等." + questionnaireId, HealthException.class);

        UserQuestionAnswer ua = new UserQuestionAnswer();
        ua.setCreatedTime(now);
        ua.setCreateor(userName);
        ua.setIsDeleted(IsDeleted.N.name());
        ua.setQuestionnaireId(questionnaireId);
        ua.setSchoolYear(tq.getSchoolYear());
        ua.setUpdatedTime(now);
        ua.setUserId(studentId);

        List<UserQuestionAnswer> answers = new ArrayList<UserQuestionAnswer>();
        for (int i = 0; i < size; i++) {
            JSONObject q = ja.getJSONObject(i);
            //题目
            String questionIdStr = (String) q.get("questionId");
            Long questionId = Long.parseLong(questionIdStr);
            //选项
            String optionIdStr = (String) q.get("optionId");
            Long optionId = Long.parseLong(optionIdStr);

            UserQuestionAnswer tem = new UserQuestionAnswer();
            BeanUtils.copyProperties(ua, tem);
            ua.setOptionId(optionId);
            ua.setQuestionId(questionId);
            answers.add(ua);
        }

        //更新用户答题结果
        UserQuestionnaire uq = new UserQuestionnaire();
        uq.setAnswerStatus(QuestionnaireAnswerStatus.DONE.name());
        uq.setCount(size);
        uq.setQuestionnaireId(questionnaireId);
        uq.setUpdatedTime(now);
        uq.setUserId(studentId);
        uq.setAddition(userName + " 教师对学生测评完成");

        userQuestionnaireRepository.saveUserQuestionAnswer(uq, answers);
    }

    /**
     * 查询学生问卷列表
     */
    public JSONObject loadStudentUserQuestionnaires(Long studentId) {
        JSONObject json = new JSONObject();
        json.put("studentId", studentId);

        List<UserQuestionnaire> list = userQuestionnaireRepository.loadStudentUserQuestionnaires(studentId);
        if (CollectionUtils.isEmpty(list)) {
            json.put("finished", null);
            json.put("notFinished", null);
            return json;
        }

        Collections.sort(list, Comparator.comparing(UserQuestionnaire::getId));
        //已完成的
        List<UserQuestionnaire> finishedList = new ArrayList<UserQuestionnaire>();
        for (UserQuestionnaire uq : list) {
            if (QuestionnaireAnswerStatus.DONE.name().equals(uq.getAnswerStatus())) {
                finishedList.add(uq);
            }
        }

        list.removeAll(finishedList);
        json.put("finished", finishedList);
        json.put("notFinished", list);
        return json;
    }

    /**
     * 学生开始测评问卷第一题
     */
    public BaseResp startStudentTestQuestionnaire(Long questionnaireId, Long studentId) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        JSONObject json = new JSONObject();

        //1查询学生问卷
        Questionnaire stuQu = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != stuQu, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId,
                HealthException.class);

        //2查询问卷下所有问题
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        if (CollectionUtils.isEmpty(questions)) {
            json.put("question", null);
            return resp.setData(json);
        }
        Collections.sort(questions, Comparator.comparing(Question::getQuestionNo));
        Question question = questions.get(0);
        json.put("question", question);

        //3查询问题所有选项
        List<QuestionOption> options = questionnaireRepository.loadQuestionnaireQuestionQuestionOptions(questionnaireId,
                question.getId());
        Collections.sort(options, Comparator.comparing(QuestionOption::getOptionNo));
        json.put("options", options);

        //4查看学习对该题目答题情况
        Long choisedOptionId = userQuestionnaireRepository.loadUserQuestionAnswer(questionnaireId, studentId,
                question.getId());
        json.put("choised_option_id", choisedOptionId);

        resp.setData(json);
        return resp;
    }
}
