package com.xueduoduo.health.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.controller.dto.StudentReportReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.QuestionnaireAnswerStatus;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.UserQuestionAnswer;
import com.xueduoduo.health.domain.questionnaire.UserQuestionnaire;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.questionnaire.repository.UserQuestionnaireRepository;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;

/**
 * 学生档案
 * 
 * @author wangzhifeng
 * @date 2018年8月19日 下午7:10:36
 */
@RestController
public class StudentReportController {
    private static final Logger         logger = LoggerFactory.getLogger(StudentReportController.class);

    @Autowired
    private UserRepository              userRepository;
    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;
    @Autowired
    private LatitudeRepository          latitudeRepository;
    @Autowired
    private QuestionnaireRepository     questionnaireRepository;

    /**
     * 学生档案列表
     */
    @RequestMapping(value = "admin/student/studentList", method = RequestMethod.POST)
    public BaseResp studentList(@RequestBody StudentReportReq req) {
        logger.info("收到学生档案列表查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            List<String> userRole = new ArrayList<String>();
            userRole.add(UserRoleType.STUDENT.name());
            Page<User> users = userRepository.loadUser(req.getGradeNo(), req.getClassNo(), req.getUserName(),
                    req.getOffSet(), req.getLength(), userRole, true, "STUDENT");

            List<User> list = new ArrayList<User>();
            for (User stu : users.getPageData()) {
                List<UserQuestionnaire> uqs = userQuestionnaireRepository.loadStudentUserQuestionnaires(stu.getId());
                int count = 0;
                for (UserQuestionnaire uq : uqs) {
                    if (QuestionnaireAnswerStatus.DONE.name().equals(uq.getAnswerStatus())) {
                        Questionnaire questionnaire = questionnaireRepository.loadById(uq.getQuestionnaireId());
                        if (QuestionnaireType.STUDENT.getDesc().equals(questionnaire.getQuestionnaireType())
                                || QuestionnaireType.STUDENT.name().equals(questionnaire.getQuestionnaireType())) {
                            count++;
                        }
                    }
                }
                stu.setStudentReportCount(count);
                if (stu.isReport()) {
                    stu.setUserStatus("有数据");
                } else {
                    stu.setUserStatus("暂无数据");
                }
                list.add(stu);
            }
            users.setPageData(list);
            resp.setData(users);
        } catch (HealthException e) {
            logger.error("学生档案列表查询异常", e);
            resp = BaseResp.buildFailResp("学生档案列表查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("学生档案列表查询异常", e);
            resp = BaseResp.buildFailResp("学生档案列表查询异常", BaseResp.class);
        }
        logger.info("学生档案列表查询返回:{}", resp);
        return resp;
    }

    /**
     * 学生档案图
     */
    @RequestMapping(value = "admin/student/studentScoreChart", method = RequestMethod.POST)
    public BaseResp studentScoreChart(@RequestBody StudentReportReq req) {
        logger.info("收到学生档案图查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            //userAnswer 统计
            List<UserQuestionAnswer> ans = userQuestionnaireRepository.loadUserQuestionOptionsAnswers(null,
                    req.getStudentId());
            //年 + 纬度
            if (CollectionUtils.isNotEmpty(ans)) {
                Collections.sort(ans, Comparator.comparing(UserQuestionAnswer::getQuestionnaireId)
                        .thenComparing(UserQuestionAnswer::getLatitudeId));

                List<Latitude> latis = latitudeRepository.loadAll();
                Map<Long, String> latiMap = new HashMap<Long, String>();
                for (Latitude la : latis) {
                    latiMap.put(la.getId(), la.getDisplayName());
                }

                //questionnaire
                Map<Long, String> questions = new HashMap<Long, String>();
                for (UserQuestionAnswer a : ans) {
                    questions.put(a.getQuestionnaireId(), a.getSchoolYear());
                }

                JSONArray ja = new JSONArray();
                JSONArray columns = new JSONArray();

                int times = 0;
                //latitude
                Set<Long> questionnairIdSets = questions.keySet();
                List<Long> questionnairIds = new ArrayList<Long>();
                questionnairIds.addAll(questionnairIdSets);
                Collections.sort(questionnairIds, Comparator.comparing(Long::intValue).reversed());
                for (Long quesId : questionnairIds) {

                    Questionnaire questionnaire = questionnaireRepository.loadById(quesId);
                    if (QuestionnaireType.TEACHER.getDesc().equals(questionnaire.getQuestionnaireType())
                            || QuestionnaireType.TEACHER.name().equals(questionnaire.getQuestionnaireType())) {
                        continue;
                    }

                    Map<Long, BigDecimal> scores = new HashMap<Long, BigDecimal>();
                    Map<Long, Integer> counts = new HashMap<Long, Integer>();
                    for (UserQuestionAnswer a : ans) {
                        if (quesId.longValue() != a.getQuestionnaireId().longValue()) {
                            continue;
                        }
                        if (null != scores.get(a.getLatitudeId())) {
                            BigDecimal add = scores.get(a.getLatitudeId()).add(a.getScore());
                            scores.put(a.getLatitudeId(), add);

                            int count = counts.get(a.getLatitudeId()) + 1;
                            counts.put(a.getLatitudeId(), count);
                        } else {
                            scores.put(a.getLatitudeId(), a.getScore());
                            counts.put(a.getLatitudeId(), 1);
                        }
                    }

                    //每个问卷
                    JSONObject json = new JSONObject(new LinkedHashMap());
                    int order = 0;
                    json.put("schoolYear", questions.get(quesId));
                    if (times == 0) {
                        columns.add(order, "schoolYear");
                        order++;
                    }
                    //一个问卷结束 算平均
                    for (Long latiId : scores.keySet()) {
                        BigDecimal scoreAvg = new BigDecimal(0.0d);
                        int count = counts.get(latiId);
                        BigDecimal sco = scores.get(latiId);
                        if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                            scoreAvg = new BigDecimal(0.0d);
                        } else {
                            scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                        }

                        //一个维度的平均
                        json.put(latiMap.get(latiId), scoreAvg);
                        if (times == 0) {
                            columns.add(order, latiMap.get(latiId));
                            order++;
                        }
                    }
                    times++;
                    ja.add(json);
                }
                JSONObject json = new JSONObject();
                json.put("columns", columns);
                json.put("rows", ja);
                resp.setData(json);
            }
        } catch (HealthException e) {
            logger.error("学生档案图查询异常", e);
            resp = BaseResp.buildFailResp("学生档案图查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("学生档案图查询异常", e);
            resp = BaseResp.buildFailResp("学生档案图查询异常", BaseResp.class);
        }
        logger.info("学生档案图查询返回:{}", resp);
        return resp;
    }
}
