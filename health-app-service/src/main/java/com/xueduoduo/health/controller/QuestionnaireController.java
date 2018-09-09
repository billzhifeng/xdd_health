package com.xueduoduo.health.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.github.java.common.utils.DateUtil;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.configuration.SchoolYearUtils;
import com.xueduoduo.health.controller.dto.AddQuesLatitudeDescReq;
import com.xueduoduo.health.controller.dto.AddQuestionnaireQuestionsInfo;
import com.xueduoduo.health.controller.dto.QuestionnaireReq;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.QuestionnaireStatusType;
import com.xueduoduo.health.domain.enums.QuestionnaireType;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.enums.UserRoleType;
import com.xueduoduo.health.domain.grade.GradeClass;
import com.xueduoduo.health.domain.latitude.Latitude;
import com.xueduoduo.health.domain.latitude.LatitudeRepository;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.QuestionnaireLatitudeScore;
import com.xueduoduo.health.domain.questionnaire.UserQuestionAnswer;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.questionnaire.repository.UserQuestionnaireRepository;
import com.xueduoduo.health.domain.service.QuestionnaireService;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.domain.user.UserRepository;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 问卷、问题、选项、分数
 * 
 * @author wangzhifeng
 * @date 2018年8月12日 下午10:49:38
 */
@RestController
public class QuestionnaireController {

    private static final Logger         logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireRepository     questionnaireRepository;

    @Autowired
    private QuestionnaireService        questionnaireService;

    @Autowired
    private UserRepository              userRepository;

    @Autowired
    private SchoolYearUtils             schoolYearUtils;
    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;
    @Autowired
    private LatitudeRepository          latitudeRepository;

    /**
     * 问卷列表
     */
    @RequestMapping(value = "admin/review/questionnaireList", method = RequestMethod.POST)
    public BaseResp questionnaireList(@RequestBody QuestionnaireReq req) {
        logger.info("收到问卷列表查询 req:{}", req);
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<Questionnaire> page = questionnaireRepository.loadPage(req.getSchoolYear(), req.getTitle(),
                    req.getOffSet(), req.getLength());
            resp.setData(page);
        } catch (HealthException e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        }
        logger.info("问卷列表查询返回:{}", resp);
        return resp;
    }

    /**
     * 添加问卷
     */
    @RequestMapping(value = "admin/review/addQuestionnaire", method = RequestMethod.POST)
    public BaseResp addQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getQuestionnaireType()), ReturnCode.PARAM_ILLEGLE, "问卷类型不能为空",
                    HealthException.class);
            logger.info("添加问卷请求,questionnaireType:{}", req.getQuestionnaireType());
            User user = UserSessionUtils.getUserFromSession();
            Long id = questionnaireRepository.save(user.getUserName(), req.getQuestionnaireType());
            resp.setData(id);
        } catch (HealthException e) {
            logger.error("添加问卷异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("添加问卷异常", e);
            resp = BaseResp.buildFailResp("添加问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("添加问卷返回:{}", resp);
        return resp;
    }

    /**
     * 复制问卷
     */
    @RequestMapping(value = "admin/review/copyQuestionnaire", method = RequestMethod.POST)
    public BaseResp copyQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Long id = req.getQuestionnaireId();//req.getParameter("questionnaireId");
            JavaAssert.isTrue(null != id, ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空", HealthException.class);
            logger.info("复制问卷请求,questionnaireId:{}", id);
            User user = UserSessionUtils.getUserFromSession();

            Long newId = questionnaireService.copyQuestionnaireCasecade(id, user.getUserName());
            resp.setData(newId);
        } catch (HealthException e) {
            logger.error("复制问卷异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("复制问卷异常", e);
            resp = BaseResp.buildFailResp("复制问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("复制问卷结果,resp:{}", resp);
        return resp;
    }

    /**
     * 展示添加编辑问卷题目+选项
     */
    @RequestMapping(value = "admin/review/showAddQuestionnaireQuestions", method = RequestMethod.POST)
    public BaseResp showAddQuestionnaireQuestions(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示添加编辑问卷题目选项,请求questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            resp = questionnaireService.showAddQuestionnaireQuestions(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("查询问卷题目异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("查询问卷题目选项结果,resp:{}", resp);
        return resp;
    }

    /**
     * 保存问卷题目+选项
     */
    @RequestMapping(value = "admin/review/addQuestionnaireQuestions", method = RequestMethod.POST)
    public BaseResp addQuestionnaireQuestions(@RequestBody AddQuestionnaireQuestionsInfo req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            logger.info("保存问卷题目+选项,请求req:{}", req);
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getTitle()), ReturnCode.PARAM_ILLEGLE, "问卷标题不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getIntroduction()), ReturnCode.PARAM_ILLEGLE, "问卷导语不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            questionnaireService.addQuestionnaireQuestions(req.getQuestionnaireId(), req.getTitle(),
                    req.getIntroduction(), req.getQuestions(), user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (HealthException e) {
            logger.error("保存问卷题目异常", e);
            resp = BaseResp.buildFailResp("已发布问卷不能编辑", BaseResp.class);
        } catch (Exception e) {
            logger.error("保存问卷题目异常", e);
            resp = BaseResp.buildFailResp("保存问卷题目异常", BaseResp.class);
        }
        logger.info("保存问卷题目+选项,resp:{}", resp);
        return resp;
    }

    /**
     * 发布问卷
     */
    @RequestMapping(value = "admin/review/publishQuestionnaire", method = RequestMethod.POST)
    public BaseResp publishQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            logger.info("发布问卷,req :{}", req);

            JavaAssert.isTrue(StringUtils.isNotBlank(req.getEndedDate()), ReturnCode.PARAM_ILLEGLE, "问卷结束时间不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getGradeNos() && req.getGradeNos().size() > 0, ReturnCode.PARAM_ILLEGLE,
                    "问卷对应年级不能为空", HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getStartDate()), ReturnCode.PARAM_ILLEGLE, "问卷开始时间不能为空",
                    HealthException.class);
            JavaAssert.isTrue(StringUtils.isNotBlank(req.getEndedDate()), ReturnCode.PARAM_ILLEGLE, "问卷结束时间不能为空",
                    HealthException.class);
            String startDateStr = req.getStartDate().substring(0, 10).replace("-", "");
            String endDateStr = req.getEndedDate().substring(0, 10).replace("-", "");
            Date startDate = DateUtil.parseDate(startDateStr, DateUtil.shortFormat);
            Date endedDate = DateUtil.parseDate(endDateStr, DateUtil.shortFormat);
            String schoolYear = schoolYearUtils.getSchoolYear(startDate, endedDate,
                    "问卷选择的时间,不在同一学年区间内,当前学年:" + schoolYearUtils.getCurrentSchoolYear());

            JSONArray gradeNos = req.getGradeNos();
            int size = gradeNos.size();
            for (int i = 0; i < size; i++) {
                int gradeNo = gradeNos.getInteger(i);

                Questionnaire q = new Questionnaire();
                q.setSchoolYear(schoolYear);
                q.setStartDate(startDate);
                q.setEndedDate(endedDate);
                q.setGradeNo(gradeNo);

                if (i == 0) {
                    q.setId(req.getQuestionnaireId());
                } else {
                    BaseResp copyResp = copyQuestionnaire(req);
                    Long newId = (Long) copyResp.getData();
                    q.setId(newId);
                }

                User user = UserSessionUtils.getUserFromSession();
                questionnaireRepository.updateToPublished(q, user.getUserName());
                //生成学生问卷
                userQuestionnaireRepository.createStudentQuestionAnswer(q.getId(), q.getGradeNo(), user.getUserName());

            }

        } catch (HealthException e) {
            logger.error("发布问卷题目异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("发布问卷题目异常", e);
            resp = BaseResp.buildFailResp("发布问卷题目异常:" + e.getMessage(), BaseResp.class);
        }
        logger.info("发布问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 删除问卷
     */
    @RequestMapping(value = "admin/review/deleteQuestionnaire", method = RequestMethod.POST)
    public BaseResp deleteQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("删除问卷,req,questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();
            questionnaireRepository.updateToDeleted(req.getQuestionnaireId(), user.getUserName());
        } catch (HealthException e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp("删除问卷题目异常", BaseResp.class);
        }
        logger.info("删除问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 展示设置分数+纬度
     */
    @RequestMapping(value = "admin/review/showAddScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp showAddScoreAndLatitude(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示设置分数和纬度,req questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            resp = questionnaireService.showAddScoreAndLatitude(req.getQuestionnaireId());
        } catch (Exception e) {
            logger.error("查询问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目分数纬度异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示设置分数和纬度,resp:{}", resp);
        return resp;
    }

    /**
     * 设置分数+纬度
     * 
     * @return
     */
    @RequestMapping(value = "admin/review/addScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp addScoreAndLatitude(@RequestBody AddQuestionnaireQuestionsInfo req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示设置分数和纬度,req :{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(req.getQuestions()), ReturnCode.PARAM_ILLEGLE, "问卷问题不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addScoreAndLatitude(req.getQuestionnaireId(), req.getQuestions(), user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (HealthException e) {
            logger.error("设置问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("设置问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("设置问卷题目分数纬度异常，" + e.getMessage(), BaseResp.class);
        }
        logger.info("设置分数和纬度,resp:{}", resp);
        return resp;
    }

    /**
     * 展示添加问卷纬度描述设置
     * 
     * @return
     */
    @RequestMapping(value = "admin/review/showQuestionnaireLatitudeDesc", method = RequestMethod.POST)
    public BaseResp showQuestionnaireLatitudeDesc(@RequestBody QuestionnaireReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("展示添加问卷纬度描述设置,req questionnaireId:{}", req.getQuestionnaireId());
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);

            JSONObject json = questionnaireService.showQuestionnaireLatitudeScoreAndDesc(req.getQuestionnaireId());
            resp.setData(json);
        } catch (Exception e) {
            logger.error("展示添加问卷纬度描述异常", e);
            resp = BaseResp.buildFailResp("展示添加问卷纬度描述异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示添加问卷纬度描述设置,resp:{}", resp);
        return resp;
    }

    /**
     * 添加问卷纬度描述设置
     */
    @RequestMapping(value = "admin/review/addQuestionnaireLatitudeDesc", method = RequestMethod.POST)
    public BaseResp addQuestionnaireLatitudeDesc(@RequestBody AddQuesLatitudeDescReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(CollectionUtils.isNotEmpty(req.getLatitudes()), ReturnCode.PARAM_ILLEGLE, "纬度描述不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addQuestionnaireLatitudeDesc(req.getQuestionnaireId(), req.getLatitudes(),
                    user.getUserName());
            resp.setData(req.getQuestionnaireId());
        } catch (HealthException e) {
            logger.error("添加问卷纬度描述设置异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("添加问卷纬度描述设置异常", e);
            resp = BaseResp.buildFailResp("添加问卷纬度描述设置异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("添加问卷纬度描述设置,resp:{}", resp);
        return resp;
    }

    /**
     * 测评问卷:问卷对学生答题和老师测评统计
     */
    @RequestMapping(value = "admin/review/questionnaireAnswerSumary", method = RequestMethod.POST)
    public BaseResp questionnaireAnswerSumary(@RequestBody QuestionnaireReq req) {

        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            Page<Questionnaire> page = questionnaireService.questionnaireAnswerSumary(req.getGradeNo(), req.getTitle(),
                    req.getOffSet(), req.getLength(), QuestionnaireType.STUDENT.name());
            resp.setData(page);
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示测评问卷,resp:{}", resp);
        return resp;
    }

    /**
     * 测评年级班级学生明细
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "admin/review/questionnaireGradeClassSummary", method = RequestMethod.POST)
    public BaseResp questionnaireGradeClassSummary(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("测评年级班级学生明细,req {}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(req.getClassNo() > -1, ReturnCode.PARAM_ILLEGLE, "班级不能为空", HealthException.class);

            JSONArray ja = questionnaireService.questionnaireGradeClassSummary(req.getQuestionnaireId(),
                    req.getGradeNo(), req.getClassNo());
            resp.setData(ja);
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("测评年级班级学生明细,resp:{}", resp);
        return resp;
    }

    /**
     * 教师测评学生
     */
    @RequestMapping(value = "admin/review/showTeacherTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp showTeacherTestQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);

            logger.info("测评年级班级学生明细,req:{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);
            User user = UserSessionUtils.getUserFromSession();

            return questionnaireService.showTeacherTestQuestionnaire(req.getQuestionnaireId(), req.getStudentId());
        } catch (Exception e) {
            logger.error("展示测评问卷异常", e);
            resp = BaseResp.buildFailResp("展示测评问卷异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("展示教师测评学生,resp:{}", resp);
        return resp;
    }

    /**
     * 保存教师测评学生结果
     */
    @RequestMapping(value = "admin/review/addTeacherTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp addTeacherTestQuestionnaire(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("保存教师测评学生结果,req :{}", req);

            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionOptionsJson(), ReturnCode.PARAM_ILLEGLE, "教师对学生测评结果不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession();

            questionnaireService.addTeacherTestQuestionnaire(req.getQuestionnaireId(), req.getStudentId(),
                    req.getQuestionOptionsJson(), user.getUserName());
        } catch (HealthException e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp(e.getReturnMsg(), BaseResp.class);
        } catch (Exception e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp("保存教师测评学生结果异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("保存教师测评学生,resp:{}", resp);
        return resp;
    }

    //---报表
    /**
     * 测评报表
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "admin/review/report", method = RequestMethod.POST)
    public BaseResp questionnairReport(@RequestBody QuestionnaireReq req, HttpServletRequest request) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            logger.info("测评问卷查询,req :{}", req);

            User teacher = (User) request.getSession().getAttribute("userId");
            int gradeNo = -1;
            if (UserRoleType.CLASS_HEADER.name().equals(teacher.getRole())) {
                List<GradeClass> gcs = userRepository.loadGradeClassByTeacherId(teacher.getId());
                if (CollectionUtils.isNotEmpty(gcs)) {
                    gradeNo = gcs.get(0).getGradeNo();
                }
            }
            Page<Questionnaire> page = questionnaireRepository.loadPage(req.getSchoolYear(), req.getTitle(),
                    req.getOffSet(), req.getLength(), QuestionnaireType.STUDENT.name(), -1);

            List<Questionnaire> publishs = new ArrayList<Questionnaire>();
            if (CollectionUtils.isNotEmpty(page.getPageData())) {
                for (Questionnaire q : page.getPageData()) {
                    if (q.getCreateStatus().equals(QuestionnaireStatusType.PUBLISHED.name())) {
                        //班主任默认看到自己的
                        if (gradeNo > 0 && q.getGradeNo() == gradeNo) {
                            publishs.add(q);
                        } else {
                            publishs.add(q);
                        }
                    }
                }
            }
            page.setPageData(publishs);
            resp.setData(page);

        } catch (Exception e) {
            logger.error("测评问卷查询异常", e);
            resp = BaseResp.buildFailResp("测评问卷查询异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("测评问卷查询,resp:{}", resp);
        return resp;
    }

    /**
     * 学生报表
     * 
     * @param req
     * @return
     */
    @RequestMapping(value = "admin/review/report/student", method = RequestMethod.POST)
    public BaseResp studentReport(@RequestBody QuestionnaireReq req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            logger.info("学生报表查询,req :{}", req);
            JavaAssert.isTrue(null != req, ReturnCode.PARAM_ILLEGLE, "请求不能为空", HealthException.class);
            JavaAssert.isTrue(null != req.getQuestionnaireId(), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            JavaAssert.isTrue(null != req.getStudentId(), ReturnCode.PARAM_ILLEGLE, "学生ID不能为空", HealthException.class);

            Questionnaire qe = questionnaireRepository.loadById(req.getQuestionnaireId());
            JavaAssert.isTrue(null != qe, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getQuestionnaireId() + "的问卷不存在",
                    HealthException.class);

            User student = userRepository.loadUserById(req.getStudentId());
            JavaAssert.isTrue(null != student, ReturnCode.PARAM_ILLEGLE, "ID=" + req.getStudentId() + "的学生不存在",
                    HealthException.class);

            //所有的纬度
            List<Latitude> latis = latitudeRepository.loadAll();
            Map<Long, String> latiMap = new HashMap<Long, String>();
            for (Latitude la : latis) {
                latiMap.put(la.getId(), la.getDisplayName());
            }

            JSONObject data = new JSONObject();
            //---学生文件
            buildStudentData(req, data, latiMap, student);
            //-----------教师问卷信息----------------------
            List<Questionnaire> teacherQus = questionnaireRepository.loadQuestionnaire(qe.getSchoolYear(),
                    qe.getGradeNo(), QuestionnaireType.TEACHER.name(), QuestionnaireStatusType.PUBLISHED.name());
            Questionnaire teQu = null;
            if (CollectionUtils.isNotEmpty(teacherQus)) {
                JavaAssert.isTrue(teacherQus.size() == 1, ReturnCode.PARAM_ILLEGLE, "同一学年统一年级有多份教师问卷",
                        HealthException.class);
                teQu = teacherQus.get(0);
                buildTeacherData(data, latiMap, student, teQu);
            }
            //-----
            resp.setData(data);

        } catch (Exception e) {
            logger.error("学生报表异常", e);
            resp = BaseResp.buildFailResp("学生报表异常" + e.getMessage(), BaseResp.class);
        }
        logger.info("学生报表,resp:{}", resp);
        return resp;
    }

    private void buildTeacherData(JSONObject data, Map<Long, String> latiMap, User student,
                                  Questionnaire questionnaire) {
        //问卷所有答案
        List<UserQuestionAnswer> gradeAns = userQuestionnaireRepository
                .loadUserQuestionOptionsAnswers(questionnaire.getId(), null);
        if (CollectionUtils.isNotEmpty(gradeAns)) {
            //title值
            JSONArray columns = new JSONArray();

            //-----柱状图数据
            JSONArray ja = new JSONArray();

            //1年级平均 userAnswer 统计-------------------------------------------------------------
            //latitude对应的次数和分值
            Map<Long, BigDecimal> gradeScores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> gradeCounts = new HashMap<Long, Integer>();
            for (UserQuestionAnswer a : gradeAns) {
                if (null != gradeScores.get(a.getLatitudeId())) {
                    BigDecimal add = gradeScores.get(a.getLatitudeId()).add(a.getScore());
                    gradeScores.put(a.getLatitudeId(), add);

                    int count = gradeCounts.get(a.getLatitudeId()) + 1;
                    gradeCounts.put(a.getLatitudeId(), count);
                } else {
                    gradeScores.put(a.getLatitudeId(), a.getScore());
                    gradeCounts.put(a.getLatitudeId(), 1);
                }
            }

            //年级算平均
            Set<Long> gradeQuestionnairIdSets = gradeScores.keySet();
            List<Long> gradeQuestionnairIds = new ArrayList<Long>();
            gradeQuestionnairIds.addAll(gradeQuestionnairIdSets);
            Collections.sort(gradeQuestionnairIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(gradeQuestionnairIds)) {
                int order = 0;
                columns.add(order, "name");
                order++;

                //年级数值
                JSONObject gradeScore = new JSONObject(new LinkedHashMap());
                gradeScore.put("name", "年级");
                for (Long latiId : gradeQuestionnairIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = gradeCounts.get(latiId);
                    BigDecimal sco = gradeScores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个维度的平均
                    gradeScore.put(latiMap.get(latiId), scoreAvg);

                    columns.add(order, latiMap.get(latiId));
                    order++;
                }
                ja.add(gradeScore);
            }

            //2班级平均-----------------------------------------------------------
            //latitude对应的次数和分值
            int classNo = student.getClassNo();
            Map<Long, BigDecimal> classScores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> classCounts = new HashMap<Long, Integer>();
            for (UserQuestionAnswer a : gradeAns) {

                //只要同班
                if (classNo != a.getClassNo()) {
                    continue;
                }

                if (null != classScores.get(a.getLatitudeId())) {
                    BigDecimal add = classScores.get(a.getLatitudeId()).add(a.getScore());
                    classScores.put(a.getLatitudeId(), add);

                    int count = classCounts.get(a.getLatitudeId()) + 1;
                    classCounts.put(a.getLatitudeId(), count);
                } else {
                    classScores.put(a.getLatitudeId(), a.getScore());
                    classCounts.put(a.getLatitudeId(), 1);
                }
            }

            //年级算平均
            Set<Long> classQuestionnairIdSets = classScores.keySet();
            List<Long> classQuestionnairIds = new ArrayList<Long>();
            classQuestionnairIds.addAll(classQuestionnairIdSets);
            Collections.sort(classQuestionnairIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(classQuestionnairIds)) {
                //年级数值
                JSONObject classScore = new JSONObject(new LinkedHashMap());
                classScore.put("name", "班级");
                for (Long latiId : classQuestionnairIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = classCounts.get(latiId);
                    BigDecimal sco = classScores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个班级的维度的平均
                    classScore.put(latiMap.get(latiId), scoreAvg);
                }
                ja.add(classScore);
            }
            //3个人------------------------------
            //userAnswer 统计
            List<UserQuestionAnswer> ans = userQuestionnaireRepository
                    .loadUserQuestionOptionsAnswers(questionnaire.getId(), student.getId());

            //latitude对应的次数和分值
            Map<Long, BigDecimal> scores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> counts = new HashMap<Long, Integer>();
            if (CollectionUtils.isNotEmpty(ans)) {
                for (UserQuestionAnswer a : ans) {
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
            }

            //一个问卷结束 算平均
            Set<Long> latitudeIdSets = scores.keySet();
            List<Long> latitudeIds = new ArrayList<Long>();
            latitudeIds.addAll(latitudeIdSets);
            Collections.sort(latitudeIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(latitudeIds)) {
                //个人数值
                JSONObject personalScore = new JSONObject(new LinkedHashMap());
                personalScore.put("name", "个人");

                for (Long latiId : latitudeIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = counts.get(latiId);
                    BigDecimal sco = scores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个维度的平均
                    personalScore.put(latiMap.get(latiId), scoreAvg);

                }
                ja.add(personalScore);
                JSONObject teacherData = new JSONObject();
                teacherData.put("columns", columns);
                teacherData.put("rows", ja);
                //个人信息
                data.put("teacherReportData", teacherData);
            }

        }
    }

    private void buildStudentData(QuestionnaireReq req, JSONObject data, Map<Long, String> latiMap, User student) {
        //问卷所有答案
        List<UserQuestionAnswer> gradeAns = userQuestionnaireRepository
                .loadUserQuestionOptionsAnswers(req.getQuestionnaireId(), null);
        if (CollectionUtils.isNotEmpty(gradeAns)) {
            //年级 班级 个人数组
            JSONArray ja = new JSONArray();
            //title值
            JSONArray columns = new JSONArray();
            int classNo = student.getClassNo();
            //个人 userAnswer 统计
            List<UserQuestionAnswer> ans = userQuestionnaireRepository
                    .loadUserQuestionOptionsAnswers(req.getQuestionnaireId(), req.getStudentId());

            //latitude对应的次数和分值
            Map<Long, BigDecimal> scores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> counts = new HashMap<Long, Integer>();
            if (CollectionUtils.isNotEmpty(ans)) {
                for (UserQuestionAnswer a : ans) {
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
            }

            //1年级平均 userAnswer 统计-------------------------------------------------------------
            //latitude对应的次数和分值
            Map<Long, BigDecimal> gradeScores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> gradeCounts = new HashMap<Long, Integer>();
            for (UserQuestionAnswer a : gradeAns) {
                if (null != gradeScores.get(a.getLatitudeId())) {
                    BigDecimal add = gradeScores.get(a.getLatitudeId()).add(a.getScore());
                    gradeScores.put(a.getLatitudeId(), add);

                    int count = gradeCounts.get(a.getLatitudeId()) + 1;
                    gradeCounts.put(a.getLatitudeId(), count);
                } else {
                    gradeScores.put(a.getLatitudeId(), a.getScore());
                    gradeCounts.put(a.getLatitudeId(), 1);
                }
            }
            //年级数值
            JSONObject gradeScore = new JSONObject(new LinkedHashMap());
            gradeScore.put("name", "年级");
            //年级算平均
            Set<Long> gradeQuestionnairIdSets = gradeScores.keySet();
            List<Long> gradeQuestionnairIds = new ArrayList<Long>();
            gradeQuestionnairIds.addAll(gradeQuestionnairIdSets);
            Collections.sort(gradeQuestionnairIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(gradeQuestionnairIds)) {
                int order = 0;

                columns.add(order, "name");
                order++;

                for (Long latiId : gradeQuestionnairIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = gradeCounts.get(latiId);
                    BigDecimal sco = gradeScores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个维度的平均
                    gradeScore.put(latiMap.get(latiId), scoreAvg);
                    columns.add(order, latiMap.get(latiId));
                    order++;
                }
                ja.add(gradeScore);
            }
            //2班级平均-----------------------------------------------------------
            //latitude对应的次数和分值
            Map<Long, BigDecimal> classScores = new HashMap<Long, BigDecimal>();
            Map<Long, Integer> classCounts = new HashMap<Long, Integer>();
            for (UserQuestionAnswer a : gradeAns) {

                //只要同班
                if (classNo != a.getClassNo()) {
                    continue;
                }

                if (null != classScores.get(a.getLatitudeId())) {
                    BigDecimal add = classScores.get(a.getLatitudeId()).add(a.getScore());
                    classScores.put(a.getLatitudeId(), add);

                    int count = classCounts.get(a.getLatitudeId()) + 1;
                    classCounts.put(a.getLatitudeId(), count);
                } else {
                    classScores.put(a.getLatitudeId(), a.getScore());
                    classCounts.put(a.getLatitudeId(), 1);
                }
            }
            //班级数值
            JSONObject classScore = new JSONObject(new LinkedHashMap());
            //班级算平均
            Set<Long> classQuestionnairIdSets = classScores.keySet();
            List<Long> classQuestionnairIds = new ArrayList<Long>();
            classQuestionnairIds.addAll(classQuestionnairIdSets);
            Collections.sort(classQuestionnairIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(classQuestionnairIds)) {
                classScore.put("name", "班级");
                for (Long latiId : classQuestionnairIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = classCounts.get(latiId);
                    BigDecimal sco = classScores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个班级的维度的平均
                    classScore.put(latiMap.get(latiId), scoreAvg);
                }
                ja.add(classScore);
            }

            //3个人------------------------------
            //----------学生问卷纬度分数描述----------------------
            List<QuestionnaireLatitudeScore> qls = questionnaireRepository
                    .loadQuestionnaireLatitudeScore(req.getQuestionnaireId());

            //个人纬度描述说明
            JSONArray descs = new JSONArray();

            //个人数值
            JSONObject personalScore = new JSONObject(new LinkedHashMap());

            //一个问卷结束 算平均
            Set<Long> latitudeIdSets = scores.keySet();
            List<Long> latitudeIds = new ArrayList<Long>();
            latitudeIds.addAll(latitudeIdSets);
            Collections.sort(latitudeIds, Comparator.comparing(Long::intValue).reversed());
            if (CollectionUtils.isNotEmpty(latitudeIds)) {
                personalScore.put("name", "个人");

                for (Long latiId : latitudeIds) {
                    BigDecimal scoreAvg = new BigDecimal(0.0d);
                    int count = counts.get(latiId);
                    BigDecimal sco = scores.get(latiId);
                    if (BigDecimal.ZERO.compareTo(sco) == 0 || count == 0) {
                        scoreAvg = new BigDecimal(0.0d);
                    } else {
                        scoreAvg = sco.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                    }

                    //一个维度的平均
                    personalScore.put(latiMap.get(latiId), scoreAvg);

                    for (QuestionnaireLatitudeScore desc : qls) {
                        if (desc.getLatitudeId().longValue() == latiId.longValue()
                                && (nullToZero(desc.getScoreMin()).compareTo(nullToZero(scoreAvg)) <= 0
                                        && nullToZero(desc.getScoreMax()).compareTo(nullToZero(scoreAvg)) >= 0)) {
                            JSONObject jo = new JSONObject();
                            jo.put("latitudeName", latiMap.get(latiId));
                            jo.put("latitudeScore", scoreAvg);
                            jo.put("latitudeDesc", desc.getComment());
                            descs.add(jo);
                        }
                    }

                }
                ja.add(personalScore);
                //-----个人雷达图数据
                JSONObject personData = new JSONObject();
                personData.put("columns", columns);
                personData.put("rows", ja);

                //个人信息
                data.put("studentReportData", personData);
                //用户纬度说明
                data.put("studentLatitudeData", descs);
            }

        }
    }

    private BigDecimal nullToZero(BigDecimal bd) {
        if (null == bd) {
            return BigDecimal.ZERO;
        }
        return bd;
    }
}
