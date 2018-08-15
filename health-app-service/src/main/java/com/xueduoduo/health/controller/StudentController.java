package com.xueduoduo.health.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.java.common.base.BaseResp;
import com.github.java.common.utils.JavaAssert;
import com.github.java.common.utils.StringUtils;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.ReturnCode;
import com.xueduoduo.health.domain.questionnaire.repository.QuestionnaireRepository;
import com.xueduoduo.health.domain.service.QuestionnaireService;
import com.xueduoduo.health.domain.user.User;
import com.xueduoduo.health.login.UserSessionUtils;

/**
 * 学生操作
 * 
 * @author wangzhifeng
 * @date 2018年8月15日 下午3:29:34
 */
@RestController
public class StudentController {

    private static final Logger     logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private QuestionnaireService    questionnaireService;

    /**
     * 展示学生测评问卷列表
     */
    @RequestMapping(value = "student/questionnaireList", method = RequestMethod.GET)
    public BaseResp questionnaireList(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String studentIdStr = req.getParameter("studentId");
            JavaAssert.isTrue(StringUtils.isNotBlank(studentIdStr), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空",
                    HealthException.class);

            JSONObject json = questionnaireService.loadStudentUserQuestionnaires(Long.parseLong(studentIdStr));
            resp.setData(json);
        } catch (Exception e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp("保存教师测评学生结果异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 学生开始测评第一题
     */
    @RequestMapping(value = "student/startStudentTestQuestionnaire", method = RequestMethod.GET)
    public BaseResp startStudentTestQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            String studentIdStr = req.getParameter("studentId");
            JavaAssert.isTrue(StringUtils.isNotBlank(studentIdStr), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空",
                    HealthException.class);

            return questionnaireService.startStudentTestQuestionnaire(Long.parseLong(idStr),
                    Long.parseLong(studentIdStr));
        } catch (Exception e) {
            logger.error("学生开始测评第一题异常", e);
            resp = BaseResp.buildFailResp("学生开始测评第一题异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }

    /**
     * 保存学生测评结果,并返回需要的题目信息
     */
    @RequestMapping(value = "student/addStudentTestQuestionnaire", method = RequestMethod.POST)
    public BaseResp addStudentTestQuestionnaire(HttpServletRequest req) {
        BaseResp resp = BaseResp.buildSuccessResp(BaseResp.class);
        try {
            String idStr = req.getParameter("questionnaireId");
            JavaAssert.isTrue(StringUtils.isNotBlank(idStr), ReturnCode.PARAM_ILLEGLE, "问卷ID不能为空",
                    HealthException.class);
            String studentIdStr = req.getParameter("studentId");
            JavaAssert.isTrue(StringUtils.isNotBlank(studentIdStr), ReturnCode.PARAM_ILLEGLE, "学生Id不能为空",
                    HealthException.class);
            String questionIdStr = req.getParameter("questionId");
            JavaAssert.isTrue(StringUtils.isNotBlank(questionIdStr), ReturnCode.PARAM_ILLEGLE, "学生测评题目不能为空",
                    HealthException.class);
            String optionIdStr = req.getParameter("optionId");
            JavaAssert.isTrue(StringUtils.isNotBlank(optionIdStr), ReturnCode.PARAM_ILLEGLE, "学生测评答案选项不能为空",
                    HealthException.class);
            String btn = req.getParameter("btn");
            JavaAssert.isTrue(StringUtils.isNotBlank(btn), ReturnCode.PARAM_ILLEGLE, "学生测评操作不能为空",
                    HealthException.class);

            User user = UserSessionUtils.getUserFromSession(req);
            //TODO 
        } catch (Exception e) {
            logger.error("保存教师测评学生结果异常", e);
            resp = BaseResp.buildFailResp("保存教师测评学生结果异常" + e.getMessage(), BaseResp.class);
        }
        return resp;
    }
}
