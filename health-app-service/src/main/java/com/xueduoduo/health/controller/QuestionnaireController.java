package com.xueduoduo.health.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.java.common.base.BaseResp;
import com.github.java.common.base.Page;
import com.github.java.common.utils.DateUtil;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.configuration.SchoolYearUtils;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.Questionnaire;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.service.QuestionnaireService;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 问卷、问题、选项、分数
 * 
 * @author wangzhifeng
 * @date 2018年8月12日 下午10:49:38
 */
@RestController
public class QuestionnaireController {

    private static final Logger     logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private QuestionnaireService    questionnaireService;

    @Autowired
    private SchoolYearUtils         schoolYearUtils;

    /**
     * 问卷列表
     */
    @RequestMapping(value = "admin/review/questionnaireList", method = RequestMethod.POST)
    public BaseResp questionnaireList(HttpServletRequest req) {
        logger.info("收到问卷列表查询:{}", req.getParameterMap());
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String schoolYear = (String) req.getParameter("schoolYear");
            String title = (String) req.getParameter("title");
            String offSetStr = (String) req.getParameter("offSet");
            String lengthStr = (String) req.getParameter("length");
            Page<Questionnaire> page = questionnaireRepository.loadPage(schoolYear, title, offSetStr, lengthStr);
            resp.setData(page);
        } catch (HealthException e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        } catch (Exception e) {
            logger.error("问卷列表查询异常", e);
            resp = BaseResp.buildFailResp("问卷列表查询异常", BaseResp.class);
        }
        return resp;
    }

    /**
     * 添加问卷
     */
    @RequestMapping(value = "admin/review/addQuestionnaire", method = RequestMethod.GET)
    public BaseResp addQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String questionnaireType = (String) req.getParameter("questionnaireType");
            JavaAssert.isTrue(StringUtils.isNotBlank(questionnaireType), ReturnCode.PARAM_ILLEGLE, "问卷类型不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireRepository.save(user.getUserName(), questionnaireType);
        } catch (Exception e) {
            logger.error("添加问卷异常", e);
            resp = BaseResp.buildFailResp("添加问卷异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 复制问卷
     */
    @RequestMapping(value = "admin/review/copyQuestionnaire", method = RequestMethod.GET)
    public BaseResp copyQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("id");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireService.copyQuestionnaire(Long.parseLong(idStr), user.getUserName());
        } catch (Exception e) {
            logger.error("复制问卷异常", e);
            resp = BaseResp.buildFailResp("复制问卷异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 展示添加编辑问卷题目
     */
    @RequestMapping(value = "admin/review/showAddQuestionnaireQuestions", method = RequestMethod.GET)
    public BaseResp showAddQuestionnaireQuestions(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            Long id = Long.parseLong(idStr);
            resp = questionnaireService.showAddQuestionnaireQuestions(id);
        } catch (Exception e) {
            logger.error("查询问卷题目异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 保存问卷题目+选项
     */
    @RequestMapping(value = "admin/review/addQuestionnaireQuestions", method = RequestMethod.GET)
    public BaseResp addQuestionnaireQuestions(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            String questions = req.getParameter("questions");
            JavaAssert.isTrue(StringUtils.isNotBlank(questions), ReturnCode.PARAM_ILLEGLE, "问卷问题不能为空",
                    HealthException.class);
            String title = req.getParameter("title");
            JavaAssert.isTrue(StringUtils.isNotBlank(title), ReturnCode.PARAM_ILLEGLE, "问卷标题不能为空",
                    HealthException.class);
            String introduction = req.getParameter("introduction");
            JavaAssert.isTrue(StringUtils.isNotBlank(introduction), ReturnCode.PARAM_ILLEGLE, "问卷导语不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireService.addQuestionnaireQuestions(Long.parseLong(idStr), title, introduction, questions,
                    user.getUserName());
        } catch (Exception e) {
            logger.error("保存问卷题目异常", e);
            resp = BaseResp.buildFailResp("保存问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 发布问卷
     */
    @RequestMapping(value = "admin/review/publishQuestionnaire", method = RequestMethod.GET)
    public BaseResp publishQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("id");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            String gradeNo = req.getParameter("gradeNo");
            JavaAssert.isTrue(StringUtils.isNotBlank(gradeNo), ReturnCode.PARAM_ILLEGLE, "问卷对应年级不能为空",
                    HealthException.class);
            String startDateStr = req.getParameter("startDate");
            JavaAssert.isTrue(StringUtils.isNotBlank(startDateStr), ReturnCode.PARAM_ILLEGLE, "问卷开始时间不能为空",
                    HealthException.class);
            String endedDateStr = req.getParameter("endedDate");
            JavaAssert.isTrue(StringUtils.isNotBlank(endedDateStr), ReturnCode.PARAM_ILLEGLE, "问卷结束时间不能为空",
                    HealthException.class);

            Date startDate = DateUtil.parseDate(startDateStr, DateUtil.shortFormat);
            Date endedDate = DateUtil.parseDate(endedDateStr, DateUtil.shortFormat);
            String schoolYear = schoolYearUtils.getSchoolYear(startDate, endedDate,
                    "问卷选择的时间,不在同一学年区间内,当前学年:" + schoolYearUtils.getCurrentSchoolYear());

            Questionnaire q = new Questionnaire();
            q.setId(Long.parseLong(idStr));
            q.setSchoolYear(schoolYear);
            q.setStartDate(startDate);
            q.setEndedDate(endedDate);
            q.setGradeNo(Integer.parseInt(gradeNo));

            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireRepository.updateToPublished(q, user.getUserName());
        } catch (Exception e) {
            logger.error("发布问卷题目异常", e);
            resp = BaseResp.buildFailResp("发布问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 删除问卷
     */
    @RequestMapping(value = "admin/review/deleteQuestionnaire", method = RequestMethod.GET)
    public BaseResp deleteQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("id");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireRepository.updateToDeleted(Long.parseLong(idStr), user.getUserName());
        } catch (Exception e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp("删除问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 未使用 - 删除某个题目
     */
    @RequestMapping(value = "admin/review/deleteQuestionnaireQuestion", method = RequestMethod.GET)
    public BaseResp deleteQuestion(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String questionIdStr = req.getParameter("questionId");
            JavaAssert.isTrue(StringUtils.isNotBlank(questionIdStr), ReturnCode.PARAM_ILLEGLE, "问卷问题ID不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession(req);
            questionnaireRepository.updateToDeleted(Long.parseLong(questionIdStr), user.getUserName());
        } catch (Exception e) {
            logger.error("删除问卷题目异常", e);
            resp = BaseResp.buildFailResp("删除问卷题目异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 展示设置分数+纬度
     */
    @RequestMapping(value = "admin/review/showAddScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp showAddScoreAndLatitude(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            resp = questionnaireService.showAddScoreAndLatitude(Long.parseLong(idStr));
        } catch (Exception e) {
            logger.error("查询问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("查询问卷题目分数纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 设置分数+纬度
     * 
     * @return
     */
    @RequestMapping(value = "admin/review/addScoreAndLatitude", method = RequestMethod.POST)
    public BaseResp addScoreAndLatitude(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            String questions = req.getParameter("questions");
            JavaAssert.isTrue(StringUtils.isNotBlank(questions), ReturnCode.PARAM_ILLEGLE, "问卷问题不能为空",
                    HealthException.class);
            User user = UserSessionUtils.getUserFromSession(req);

            questionnaireService.addScoreAndLatitude(Long.parseLong(idStr), questions, user.getUserName());
        } catch (Exception e) {
            logger.error("设置问卷题目分数纬度异常", e);
            resp = BaseResp.buildFailResp("设置问卷题目分数纬度异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }
}