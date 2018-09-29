package com.xueduoduo.health.domain.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.DateUtil;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.QuestionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionOptionDOMapper;
import com.xueduoduo.health.dal.dao.QuestionnaireLatitudeScoreDOMapper;
import com.xueduoduo.health.dal.dataobject.QuestionDO;
import com.xueduoduo.health.dal.dataobject.QuestionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDO;
import com.xueduoduo.health.dal.dataobject.QuestionOptionDOExample;
import com.xueduoduo.health.dal.dataobject.QuestionnaireDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDO;
import com.xueduoduo.health.dal.dataobject.QuestionnaireLatitudeScoreDOExample;
import com.xueduoduo.health.dal.dataobject.UserQuestionAnswerDO;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.QuestionnaireAnswerStatus;
import com.xueduoduo.health.domain.enums.QuestionnaireStatusType;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
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
    private static final Logger                logger              = LoggerFactory
            .getLogger(QuestionnaireService.class);

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

    @Value("${student.submit.second}")
    private Integer                            studentSubmitSecond = 900;                           //900秒15分钟

    private static Map<Integer, String>        notices             = new HashMap<Integer, String>();
    static {
        //        notices.put(4, "哇！你真棒！已经完成4道题啦！");
        //        notices.put(8, "哇！你真棒！已经完成4道题啦！");
        //--以上测试
        notices.put(10, "哇！你真棒！已经完成10道题啦！");
        notices.put(20, "看来你完成的很顺利！加油哦！");
        notices.put(27, "题目已经过半啦！继续努力！");
        notices.put(33, "还有10题，加油吧！");
        notices.put(38, "最后5道题啦，胜利就在眼前！");
        notices.put(43, "恭喜你完成本次闯关，放松一下吧！");
    }

    /**
     * 复制问卷
     * 
     * @param id
     * @param userName
     */
    @Transactional
    public Long copyQuestionnaireCasecade(Long srcId, String userName) {
        //1问卷copy
        QuestionnaireDO instance = questionnaireRepository.copyAndSave(srcId, userName);

        Long newId = instance.getId();
        //2复制题目+复制选项 + 分数
        questionnaireRepository.deepCopyQuestionnaireQuestionAndOptions(srcId, newId);

        if (!instance.getQuestionnaireType().equals(QuestionnaireType.STUDENT.name())) {
            return newId;
        }
        //3学生问卷复制分数说明
        questionnaireRepository.copyQuestionnaireLatitudeScore(srcId, newId);
        return newId;
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
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())
                || QuestionnaireType.STUDENT.getDesc().equals(qe.getQuestionnaireType())) {//学生问卷
            steps.add(2, "latitude_set_step");
        }
        json.put("btns", btns);
        json.put("steps", steps);

        json.put("current_step", 0);

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
            int optitonCount = 0;
            for (QuestionOption qo : options) {
                if (qo.getQuestionId().longValue() == q.getId().longValue()) {

                    qos.add(qo);
                    if (withScore) {
                        BigDecimal score = qo.getScore();
                        score = score == null ? new BigDecimal(0) : score;

                        //首次赋值
                        if (optitonCount == 0) {
                            minScore = score;
                            maxScore = score;
                            optitonCount++;
                        }

                        if (minScore.doubleValue() > score.doubleValue()) {
                            minScore = score;
                        }
                        if (maxScore.doubleValue() < score.doubleValue()) {
                            maxScore = score;
                        }
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
                                          List<Question> reqQuestions, String userName) {
        //构建问卷内容
        Questionnaire ld = new Questionnaire();
        ld.setId(questionnaireId);
        ld.setTitle(title);
        ld.setIntroduction(introduction);

        //如果没有题目直接保存
        if (CollectionUtils.isEmpty(reqQuestions)) {
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
        //        JSONArray ja = JSONArray.parseArray(questionsJsonStr);
        JavaAssert.isTrue((null != reqQuestions && reqQuestions.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目为空",
                HealthException.class);

        //被更新的问题ID列表
        List<Long> updateQuestionIds = new ArrayList<Long>();
        //循环保存问题 + 问题选项
        int size = reqQuestions.size();
        for (int i = 0; i < size; i++) {
            //            JSONObject q = ja.getJSONObject(i);
            Question q = reqQuestions.get(i);
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

        questionnaireRepository.updateToSaveQuestions(ld, userName);
    }

    /**
     * 保存或更新题目选项JSONObject q,
     */
    @Transactional
    private Long saveOrUpdateQuestionAndOptions(Question q, String userName, Long questionnaireId) {

        String questionName = q.getQuestionName();//(String) q.get("questionName");
        Integer questionNo = q.getQuestionNo();//(Integer) q.get("questionNo");
        String audioUrl = q.getAudioUrl();// (String) q.get("audioUrl");

        Long questionId = q.getId();//(Integer) q.get("id");
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

            //            JSONArray ja = (JSONArray) q.get("options");
            List<QuestionOption> options = q.getOptions();
            JavaAssert.isTrue(null != options && options.size() > 0, ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);

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
            int size = options.size();
            for (int i = 0; i < size; i++) {

                //                JSONObject option = ja.getJSONObject(i);
                QuestionOption option = options.get(i);
                Long optionId = option.getId();//(Integer) option.get("id");
                Integer optionNo = option.getOptionNo();//(Integer) option.get("optionNo");
                String optionName = option.getDisplayName();//(String) option.get("displayName");
                String img = option.getImgUrl();
                if (null != optionId) {
                    //更新选项
                    QuestionOptionDO o = new QuestionOptionDO();
                    o.setDisplayName(optionName);
                    o.setOptionNo(optionNo);
                    o.setUpdatedTime(today);
                    o.setId(optionId.longValue());
                    o.setImgUrl(img);
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
                    o.setImgUrl(img);
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
            questionId = qst.getId();

            //            JSONArray ja = (JSONArray) q.get("optionsStr");
            //选项
            List<QuestionOption> options = q.getOptions();
            JavaAssert.isTrue((null != options && options.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目选项为空",
                    HealthException.class);
            int size = options.size();
            for (int i = 0; i < size; i++) {

                //                JSONObject option = ja.getJSONObject(i);
                QuestionOption option = options.get(i);
                Integer optionNo = option.getOptionNo();//(Integer) option.get("optionNo");
                String optionName = option.getDisplayName();//(String) option.get("displayName");

                QuestionOptionDO o = new QuestionOptionDO();
                o.setDisplayName(optionName);
                o.setCreateor(userName);
                o.setOptionNo(optionNo);
                o.setQuestionId(questionId.longValue());
                o.setQuestionnaireId(questionnaireId);
                o.setCreateor(userName);
                o.setCreatedTime(today);
                o.setUpdatedTime(today);
                o.setImgUrl(option.getImgUrl());
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
    private void updateQuestionToDeleted(Long id, String userName) {
        //1查找题目
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
        //3.删除题目
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
        btns.add("pre_btn");
        steps.add(0, "question_set_step");
        steps.add(1, "score_set_step");
        if (QuestionnaireType.STUDENT.name().equals(qe.getQuestionnaireType())
                || QuestionnaireType.STUDENT.getDesc().equals(qe.getQuestionnaireType())) {//学生问卷
            btns.add("next_btn");
            steps.add(2, "latitude_set_step");
        } else {
            btns.add("publish_btn");//教师问卷有发布按钮
        }
        json.put("btns", btns);
        json.put("steps", steps);

        json.put("current_step", 1);

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
    public void addScoreAndLatitude(Long questionnaireId, List<Question> questions, String userName) {
        Date now = new Date();
        //问卷下有问题列表
        //        JSONArray ja = JSONArray.parseArray(questionsJsonStr);
        JavaAssert.isTrue((null != questions && questions.size() > 0), ReturnCode.PARAM_ILLEGLE, "问卷题目为空",
                HealthException.class);

        //循环保存问题 + 问题选项
        int size = questions.size();
        for (int i = 0; i < size; i++) {
            //每个题
            //            JSONObject q = ja.getJSONObject(i);
            Question q = questions.get(i);
            //题目Id
            Long questionId = q.getId();//(Integer) q.get("questionId");
            //纬度Id
            Long latitudeId = q.getLatitudeId();// (Integer) q.get("latitudeId");

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
            //            JSONArray ops = (JSONArray) q.get("optionsStr");
            List<QuestionOption> options = q.getOptions();
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(options), ReturnCode.PARAM_ILLEGLE, "题目选项不能为空",
                    HealthException.class);

            //更新每个选项分数+纬度
            int opsSize = options.size();
            for (int j = 0; j < opsSize; j++) {

                //                JSONObject option = ops.getJSONObject(j);
                QuestionOption option = options.get(j);
                Long optionId = option.getId();//(Integer) option.get("optionId");
                BigDecimal score = option.getScore();//(BigDecimal) option.get("score");
                if (null == score) {
                    throw new HealthException(ReturnCode.PARAM_ILLEGLE, "第" + q.getQuestionNo() + "题分值设置不全");
                }
                score.setScale(1);//1位小数

                QuestionOptionDO o = new QuestionOptionDO();
                o.setId(optionId.longValue());
                o.setScore(score);
                o.setUpdatedTime(now);
                o.setLatitudeId(latitudeId.longValue());
                o.setAddition(o.getAddition() + ";设置分数");
                int count = questionOptionDOMapper.updateByPrimaryKeySelective(o);
                JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "题目选项分数更新异常", HealthException.class);
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

        json.put("current_step", 2);

        //纬度列表
        List<Latitude> allLats = latitudeRepository.loadAll();

        Set<Long> latIds = new HashSet<Long>();
        QuestionDOExample qea = new QuestionDOExample();
        QuestionDOExample.Criteria qcri = qea.createCriteria();
        qcri.andIsDeletedEqualTo(IsDeleted.N.name());
        qcri.andQuestionnaireIdEqualTo(qe.getId());
        List<QuestionDO> qs = questionDOMapper.selectByExample(qea);
        JavaAssert.isTrue(!CollectionUtils.isEmpty(qs), ReturnCode.PARAM_ILLEGLE, "文件无题目无法设置纬度描述",
                HealthException.class);
        for (QuestionDO q : qs) {
            latIds.add(q.getLatitudeId());
        }

        //本问卷的纬度列表
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
    public void addQuestionnaireLatitudeDesc(Long questionnaireId, List<Latitude> latitudes, String userName) {
        Date now = new Date();
        //纬度描述
        //        JSONArray ja = JSONArray.parseArray(scoreDescJson);
        JavaAssert.isTrue((null != latitudes && latitudes.size() > 0), ReturnCode.PARAM_ILLEGLE, "纬度描述为空",
                HealthException.class);

        //查询已有纬度设置
        QuestionnaireLatitudeScoreDOExample example = new QuestionnaireLatitudeScoreDOExample();
        QuestionnaireLatitudeScoreDOExample.Criteria cri = example.createCriteria();
        cri.andQuestionnaireIdEqualTo(questionnaireId);
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<QuestionnaireLatitudeScoreDO> oriScores = questionnaireLatitudeScoreDOMapper.selectByExample(example);

        //是否编辑
        boolean isUpdateFlag = false;
        Set<Long> updatedIds = new HashSet<Long>();

        //循环纬度描述
        int size = latitudes.size();
        for (int i = 0; i < size; i++) {
            //描述
            //            JSONObject q = ja.getJSONObject(i);
            Latitude q = latitudes.get(i);
            //纬度Id
            Long latitudeId = q.getId();//(Integer) q.get("latitudeId");
            List<QuestionnaireLatitudeScore> latitudeScoreDesc = q.getLatitudeScoreDesc();
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(latitudeScoreDesc), ReturnCode.PARAM_ILLEGLE,
                    "纬度" + q.getDisplayName() + "描述为空", HealthException.class);
            if (CollectionUtils.isNotEmpty(latitudeScoreDesc)) {
                for (QuestionnaireLatitudeScore score : latitudeScoreDesc) {
                    BigDecimal maxScore = score.getScoreMax();
                    BigDecimal minScore = score.getScoreMin();
                    minScore.setScale(1, BigDecimal.ROUND_HALF_UP);
                    maxScore.setScale(1, BigDecimal.ROUND_HALF_UP);

                    String descStr = score.getComment();
                    Long scoreId = score.getId();

                    if (null != scoreId) {
                        //更新
                        isUpdateFlag = true;
                        updatedIds.add(scoreId.longValue());

                        QuestionnaireLatitudeScoreDO scdb = new QuestionnaireLatitudeScoreDO();
                        scdb.setId(scoreId.longValue());
                        scdb.setComment(descStr);
                        scdb.setScoreMax(maxScore);
                        scdb.setScoreMin(minScore);
                        scdb.setUpdatedTime(now);
                        int count = questionnaireLatitudeScoreDOMapper.updateByPrimaryKeySelective(scdb);
                        JavaAssert.isTrue(1 == count, ReturnCode.PARAM_ILLEGLE, "学生问卷纬度描述更新异常,id=" + scoreId,
                                HealthException.class);
                    } else {
                        //新增
                        QuestionnaireLatitudeScoreDO scdb = new QuestionnaireLatitudeScoreDO();
                        scdb.setComment(descStr);
                        scdb.setCreatedTime(now);
                        scdb.setUpdatedTime(now);
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
        tar.setScoreMin(src.getScoreMin());
        tar.setScoreMax(src.getScoreMax());
        return tar;
    }

    /**
     * 测评问卷统计
     */
    public Page<Questionnaire> questionnaireAnswerSumary(int gradeNo, String title, int offSetStr, int lengthStr,
                                                         String questionnatireType) {

        //分页查询学生问卷
        Page<Questionnaire> page = questionnaireRepository.loadPage(null, title, offSetStr, lengthStr,
                questionnatireType, gradeNo);

        //统计问卷测评数
        List<Questionnaire> list = new ArrayList<Questionnaire>();
        if (CollectionUtils.isNotEmpty(page.getPageData())) {

            for (Questionnaire q : page.getPageData()) {
                //已经发布才统计
                if (QuestionnaireStatusType.PUBLISHED.name().equals(q.getCreateStatus())) {
                    q = questionnaireRepository.questionnaireSummary(q);
                    list.add(q);
                }
            }
        }

        page.setPageData(list);
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

        gradeNo = stuQu.getGradeNo();

        //2查询问卷对应的年级->班级学生用户
        List<User> users = userRepository.loadUser(gradeNo, classNo, UserRoleType.STUDENT.name(), "STUDENT");

        //查询每个学生完成对该问卷答题
        Set<Long> answeredStudenIds = new HashSet<Long>();
        List<UserQuestionnaire> uqs = userQuestionnaireRepository.summaryStudentAnsweredQuestionnaire(questionnaireId);
        if (!CollectionUtils.isEmpty(uqs)) {
            for (UserQuestionnaire uq : uqs) {
                answeredStudenIds.add(uq.getUserId());
            }
        }

        //3查询教师对每个学生测评情况
        //教师已经完成对学生测评
        Set<Long> alreadyTestStudenIds = new HashSet<Long>();

        List<Questionnaire> teacherQus = questionnaireRepository.loadQuestionnaire(stuQu.getSchoolYear(),
                stuQu.getGradeNo(), QuestionnaireType.TEACHER.name(), QuestionnaireStatusType.PUBLISHED.name());
        Questionnaire teQu = null;
        if (CollectionUtils.isNotEmpty(teacherQus)) {
            JavaAssert.isTrue(teacherQus.size() == 1, ReturnCode.PARAM_ILLEGLE, "同一学年统一年级有多份教师问卷",
                    HealthException.class);
            teQu = teacherQus.get(0);

            List<UserQuestionnaire> teaStuAnsQus = userQuestionnaireRepository
                    .summaryStudentAnsweredQuestionnaire(teQu.getId());
            if (!CollectionUtils.isEmpty(teaStuAnsQus)) {
                for (UserQuestionnaire tsaq : teaStuAnsQus) {
                    alreadyTestStudenIds.add(tsaq.getUserId());
                }
            }
        }

        //4 统计学生人数
        JSONArray sumnary = new JSONArray();
        for (User u : users) {
            if (!u.getRole().equals(UserRoleType.STUDENT.name())) {
                continue;
            }
            JSONObject stu = new JSONObject();
            stu.put("studentId", u.getId());
            stu.put("studentName", u.getUserName());
            stu.put("studentHeadImgUrl", u.getHeaderImg());
            //学生自己完成测评
            if (answeredStudenIds.contains(u.getId().longValue())) {
                stu.put("studentAnswer", "已测");
            } else {
                stu.put("studentAnswer", "未测");
            }

            //教师对学生完成测评
            if (alreadyTestStudenIds.contains(u.getId().longValue())) {

                stu.put("teacherAnswer", "已处理");
                stu.put("status", "FINISHED");
            } else {
                if (null != teQu) {

                    //测评已过期
                    if (null != stuQu.getEndedDate()) {
                        if (stuQu.getEndedDate().before(new Date())) {
                            stu.put("status", "FINISHED");
                        } else {
                            stu.put("status", "CHECKING");
                        }
                    }

                    stu.put("teacherAnswer", "未处理");
                    stu.put("teacherQuestionnaireId", teQu.getId());
                } else {
                    stu.put("teacherAnswer", "无问卷");
                    stu.put("status", "FINISHED");
                    stu.put("teacherQuestionnaireId", null);
                }
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

        //测评已过期
        if (null != tq.getEndedDate()) {
            if (tq.getEndedDate().before(new Date())) {
                resp = BaseResp.buildFailResp("测评已过期", BaseResp.class);
                return resp;
            }
        }

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
    public void addTeacherTestQuestionnaire(Long questionnaireId, Long studentId, JSONArray questionOptionsJson,
                                            String userName) {
        //1查询教师问卷
        Questionnaire tq = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != tq, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId, HealthException.class);
        //题目总数
        int totalQuestionCount = tq.getCount();

        Date now = new Date();
        //测评结果
        JSONArray ja = questionOptionsJson;
        JavaAssert.isTrue((null != ja && ja.size() > 0), ReturnCode.PARAM_ILLEGLE, "测评结果为空", HealthException.class);
        //循环结果
        int size = ja.size();

        JavaAssert.isTrue(totalQuestionCount == size, ReturnCode.DATA_NOT_EXIST,
                "问卷题目数=" + totalQuestionCount + ",与答案数量=" + size + ",不等." + questionnaireId, HealthException.class);

        User stu = userRepository.loadUserById(studentId);

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
            Integer questionId = (Integer) q.get("questionId");
            //选项
            Integer optionId = (Integer) q.get("optionId");
            QuestionOptionDO op = questionOptionDOMapper.selectByPrimaryKey(new Long(optionId));
            Long latitudeId = op.getLatitudeId();
            Integer optionNo = op.getOptionNo();

            UserQuestionAnswer tem = new UserQuestionAnswer();
            BeanUtils.copyProperties(ua, tem);
            tem.setOptionId(optionId.longValue());
            tem.setOptionNo(optionNo);
            tem.setLatitudeId(latitudeId);
            tem.setQuestionId(questionId.longValue());
            tem.setScore(op.getScore());
            tem.setClassNo(stu.getClassNo());
            tem.setGradeNo(stu.getGradeNo());
            answers.add(tem);
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
        List<UserQuestionnaire> notFinishedList = new ArrayList<UserQuestionnaire>();
        for (UserQuestionnaire uq : list) {
            QuestionnaireDO tem = questionnaireRepository.loadQuestionnaireDOById(uq.getQuestionnaireId());
            if (!QuestionnaireType.STUDENT.name().equals(tem.getQuestionnaireType())
                    || !IsDeleted.N.name().equals(tem.getIsDeleted())) {
                continue;
            }
            Questionnaire q = questionnaireRepository.loadById(uq.getQuestionnaireId());
            uq.setQuestionnaireName(q.getTitle());
            //测评已过期
            if (null != q.getEndedDate()) {
                if (q.getEndedDate().before(new Date())) {
                    uq.setStatus("FINISHED");
                } else {
                    uq.setStatus("CHECKING");
                }
            }

            if (QuestionnaireAnswerStatus.DONE.name().equals(uq.getAnswerStatus())) {
                finishedList.add(uq);
            } else {
                notFinishedList.add(uq);
            }
        }

        json.put("finished", finishedList);
        json.put("notFinished", notFinishedList);
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

        //测评已过期
        if (null != stuQu.getEndedDate()) {
            if (stuQu.getEndedDate().before(new Date())) {
                resp = BaseResp.buildFailResp("测评已过期", BaseResp.class);
                return resp;
            }
        }

        json.put("introduction", stuQu.getIntroduction());
        json.put("notice", stuQu.getIntroduction());

        //2查询问卷下所有问题
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        if (CollectionUtils.isEmpty(questions)) {
            json.put("question", null);
            return resp.setData(json);
        }
        Collections.sort(questions, Comparator.comparing(Question::getQuestionNo));
        Question question = questions.get(0);

        //3查询问题所有选项
        List<QuestionOption> options = questionnaireRepository.loadQuestionnaireQuestionQuestionOptions(questionnaireId,
                question.getId());
        Collections.sort(options, Comparator.comparing(QuestionOption::getOptionNo));
        if (StringUtils.isNoneBlank(options.get(0).getImgUrl())) {
            question.setViewType("IMG");
        } else {
            question.setViewType("TEXT");
        }
        json.put("options", options);
        json.put("question", question);
        //4查看学习对该题目答题情况
        UserQuestionAnswerDO userAnswer = userQuestionnaireRepository.loadUserQuestionAnswer(questionnaireId, studentId,
                question.getId());
        if (null != userAnswer) {
            json.put("answerId", userAnswer.getId());
            json.put("optionId", userAnswer.getOptionId());
        }

        //5.操作是否有上一题、下一题
        json.put("preQuestionId", null);
        if (questions.size() == 1) {
            json.put("nextQuestionId", null);
        } else {
            json.put("nextQuestionId", questions.get(1).getId());
        }

        json.put("totalCount", questions.size());
        json.put("currentCount", 1);
        json.put("answerRate", "0");

        //开始测评
        userQuestionnaireRepository.updateStudentStartAnswer(questionnaireId, studentId);
        resp.setData(json);
        return resp;
    }

    /**
     * 保存学生答题结果并展示另外一题目
     */
    @Transactional
    public void saveStudentTestQuestionnaire(Long questionnaireId, Long studentId, Long questionId, Long optinId,
                                             Long answerId, User student) {
        //1查询学生问卷
        Questionnaire stuQu = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != stuQu, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId,
                HealthException.class);

        QuestionOptionDO qo = questionOptionDOMapper.selectByPrimaryKey(optinId);
        JavaAssert.isTrue(null != qo, ReturnCode.DATA_NOT_EXIST, "选项不存在,id=" + optinId, HealthException.class);
        JavaAssert.isTrue(qo.getQuestionId().longValue() == questionId.longValue(), ReturnCode.DATA_NOT_EXIST,
                "题目不存在该选项,选项id=" + optinId + "题目id=" + questionId, HealthException.class);

        //2保存结果
        Date now = new Date();
        UserQuestionAnswer a = new UserQuestionAnswer();
        a.setCreatedTime(now);
        a.setIsDeleted(IsDeleted.N.name());
        a.setLatitudeId(qo.getLatitudeId());
        a.setOptionId(qo.getId());
        a.setQuestionId(qo.getQuestionId());
        a.setQuestionnaireId(questionnaireId);
        a.setSchoolYear(stuQu.getSchoolYear());
        a.setScore(qo.getScore());
        a.setUpdatedTime(now);
        a.setUserId(studentId);
        a.setId(answerId);
        a.setClassNo(student.getClassNo());
        a.setGradeNo(student.getGradeNo());
        a.setOptionNo(qo.getOptionNo());

        //更新
        if (null != answerId) {
            userQuestionnaireRepository.updateStudentQuestionAnswer(a);
        } else {//新增
            userQuestionnaireRepository.saveStudentQuestionAnswer(a);
            //新回答一题目
            userQuestionnaireRepository.updateStudentAnswerCountAndOne(questionnaireId, studentId);
        }
    }

    /**
     * 展现学生选择的题目
     * 
     * @param questionnaireId
     * @param nextQuestionId
     * @param preQuestionId
     * @return
     */
    public BaseResp showUserNextQuestion(Long questionnaireId, Long nextQuestionId, Long preQuestionId,
                                         Long studentId) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        JSONObject json = new JSONObject();
        //3查询题目
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        Collections.sort(questions, Comparator.comparing(Question::getQuestionNo));

        //4要展示的问题
        Question showQuestion = null;
        Long showQuesId = null;
        if (null != nextQuestionId) {
            showQuesId = nextQuestionId;
        } else if (null != preQuestionId) {
            showQuesId = preQuestionId;
        }
        Long nextQuesId = null;
        Long preQuesId = null;
        int size = questions.size();
        for (int i = 0; i < size; i++) {
            Question q = questions.get(i);

            //用户操作的按钮对应题目
            if (q.getId().longValue() == showQuesId.longValue()) {
                showQuestion = q;
                int qustionNo = q.getQuestionNo();
                json.put("currentCount", qustionNo);

                //完成百分比
                int num1 = qustionNo;
                int num2 = size;
                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后0位
                numberFormat.setMaximumFractionDigits(0);
                String rate = numberFormat.format((float) num1 / (float) num2 * 100);
                json.put("answerRate", rate);

                if (notices.containsKey(q.getQuestionNo())) {
                    json.put("notice", notices.get(q.getQuestionNo()));
                }

                //最后一题
                if (i == (size - 1)) {
                    nextQuesId = null;
                    json.put("submit", "submit");
                } else {
                    nextQuesId = questions.get(i + 1).getId();
                }

                //上一题
                if ((i - 1) >= 0) {
                    preQuesId = questions.get(i - 1).getId();
                } else {
                    preQuesId = null;
                }

                break;
            }
        }
        json.put("totalCount", questions.size());
        json.put("preQuestionId", preQuesId);
        json.put("nextQuestionId", nextQuesId);

        //5查询问题所有选项
        List<QuestionOption> options = questionnaireRepository.loadQuestionnaireQuestionQuestionOptions(questionnaireId,
                showQuestion.getId());
        Collections.sort(options, Comparator.comparing(QuestionOption::getOptionNo));
        json.put("options", options);

        if (StringUtils.isNoneBlank(options.get(0).getImgUrl())) {
            showQuestion.setViewType("IMG");
        } else {
            showQuestion.setViewType("TEXT");
        }
        json.put("question", showQuestion);

        //6查看学习对该题目答题情况
        UserQuestionAnswerDO userAnswer = userQuestionnaireRepository.loadUserQuestionAnswer(questionnaireId, studentId,
                showQuestion.getId());
        if (null != userAnswer) {
            json.put("answerId", userAnswer.getId());
            json.put("optionId", userAnswer.getOptionId());
        }

        resp.setData(json);
        return resp;
    }

    /**
     * 学生交卷
     */
    @Transactional
    public BaseResp studentSumbit(Long questionnaireId, Long studentId, Long questionId, Long optionId, Long answerId,
                                  User student) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        Date now = new Date();
        UserQuestionnaire dbuq = userQuestionnaireRepository.loadUserQuestionnaireByQuesIdAndStudentId(questionnaireId,
                studentId);
        if (QuestionnaireAnswerStatus.DONE.name().equals(dbuq.getAnswerStatus())) {
            resp.setReturnMsg("已提交成功,请勿重复提交");
        }

        //0保存问卷
        saveStudentTestQuestionnaire(questionnaireId, studentId, questionId, optionId, answerId, student);

        //1查询学生问卷
        Questionnaire stuQu = questionnaireRepository.loadById(questionnaireId);
        JavaAssert.isTrue(null != stuQu, ReturnCode.DATA_NOT_EXIST, "问卷不存在,id=" + questionnaireId,
                HealthException.class);

        UserQuestionnaire uq = userQuestionnaireRepository.loadUserQuestionnaireByQuesIdAndStudentId(questionnaireId,
                studentId);
        //2检查时间 studentSubmitSecond==900
        long diffSeconds = DateUtil.getDiffSeconds(now, uq.getStarttime());
        JavaAssert.isTrue(studentSubmitSecond < diffSeconds, ReturnCode.PARAM_ILLEGLE, "测量时间过短（15分钟以内），不可提交问卷",
                HealthException.class);

        List<UserQuestionAnswer> uqaList = userQuestionnaireRepository.loadUserQuestionOptionsAnswers(questionnaireId,
                studentId);
        //3检查题目选项是否都一样
        Set<Integer> optionNos = new HashSet<Integer>();
        for (UserQuestionAnswer ua : uqaList) {
            optionNos.add(ua.getOptionNo());
        }
        JavaAssert.isTrue(optionNos.size() > 1, ReturnCode.PARAM_ILLEGLE, "你是不是有点太心急了呢，别着急，要认真回答每一题哦",
                HealthException.class);

        //4检查答题个数 = 问卷题目数量
        List<Question> questions = questionnaireRepository.loadQuestionnaireQuestions(questionnaireId);
        Map<Long, Integer> quesIds = new HashMap<Long, Integer>();
        for (Question q : questions) {
            quesIds.put(q.getId(), q.getQuestionNo());
        }

        List<Integer> unAnswerQuestionNos = new ArrayList<Integer>();
        if (stuQu.getCount() != uqaList.size()) {

            //第几题没有完成
            String returnMst = "第";
            for (UserQuestionAnswer qa : uqaList) {
                if (null == quesIds.get(qa.getQuestionId())) {
                    unAnswerQuestionNos.add(qa.getOptionNo());
                }
            }
            returnMst = returnMst + unAnswerQuestionNos.toString() + "题没有完成";
            resp = BaseResp.buildFailResp(returnMst, BaseResp.class);
            resp.setReturnMsg(returnMst);
            return resp;
        }

        //5更新答卷状态为完成
        uq.setAnswerStatus(QuestionnaireAnswerStatus.DONE.name());
        uq.setUpdatedTime(new Date());
        userQuestionnaireRepository.updateToSubmit(uq);
        return resp;
    }
}
